package us.timinc.mc.cobblemon.spawnnotification.destination

import com.cobblemon.mod.common.util.playSoundServer
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.context.BroadcastContext
import us.timinc.mc.cobblemon.spawnnotification.data.AbstractBroadcastData
import us.timinc.mc.cobblemon.timcore.PokemonMatcher
import java.util.*

object Sound : Destination<Sound.BroadcastData>() {
    override fun broadcast(
        broadcastData: BroadcastData,
        broadcastContext: BroadcastContext,
    ) {
        val soundEvent = SoundEvent.createVariableRangeEvent(broadcastData.sound)
        broadcastContext.world.playSoundServer(
            broadcastContext.position,
            soundEvent,
            SoundSource.valueOf(broadcastData.source),
            broadcastData.volume,
            broadcastData.pitch
        )

        val debugger = SpawnNotification.debugger.getCaseDebugger(broadcastContext.id.toString())
        debugger.debug("Played sound $soundEvent")
    }

    override val codec: Codec<BroadcastData> = RecordCodecBuilder.create { instance ->
        instance.group(
            PokemonMatcher.STRING_CODEC.listOf().fieldOf("matcher").forGetter(BroadcastData::matcher),
            PokemonMatcher.STRING_CODEC.listOf().optionalFieldOf("antiMatcher")
                .forGetter { Optional.ofNullable(it.antiMatcher) },
            ResourceLocation.CODEC.fieldOf("destination").forGetter(BroadcastData::destination),
            ResourceLocation.CODEC.listOf().fieldOf("triggers").forGetter(BroadcastData::triggers),
            ResourceLocation.CODEC.fieldOf("sound").forGetter(BroadcastData::sound),
            Codec.FLOAT.optionalFieldOf("volume").forGetter { Optional.ofNullable((it.volume)) },
            Codec.FLOAT.optionalFieldOf("pitch").forGetter { Optional.ofNullable((it.pitch)) },
            Codec.STRING.optionalFieldOf("source").forGetter { Optional.ofNullable((it.source)) }
        )
            .apply(instance) { matcher, antiMatcher, destination, triggers, sound, volume, pitch, source ->
                BroadcastData(
                    matcher,
                    antiMatcher.orElse(null),
                    destination,
                    triggers,
                    sound,
                    volume.orElse(1F),
                    pitch.orElse(1F),
                    source.orElse("neutral")
                )
            }
    }

    class BroadcastData(
        override val matcher: List<PokemonMatcher>,
        override val antiMatcher: List<PokemonMatcher>?,
        override val destination: ResourceLocation,
        override val triggers: List<ResourceLocation>,
        val sound: ResourceLocation,
        val volume: Float,
        val pitch: Float,
        val source: String,
    ) : AbstractBroadcastData<BroadcastData>(matcher, antiMatcher, destination, triggers) {
        override fun broadcast(broadcastContext: BroadcastContext) {
            broadcast(this, broadcastContext)
        }
    }
}