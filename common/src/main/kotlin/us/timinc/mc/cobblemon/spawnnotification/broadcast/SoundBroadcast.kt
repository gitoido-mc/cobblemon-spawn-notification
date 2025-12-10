package us.timinc.mc.cobblemon.spawnnotification.broadcast

import com.cobblemon.mod.common.util.playSoundServer
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.Broadcast
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastType

class SoundBroadcast(
    override val destination: ResourceLocation,
    val sound: ResourceLocation,
    val volume: Float,
    val pitch: Float,
    val source: String,
) : Broadcast {
    companion object {
        val CODEC: MapCodec<SoundBroadcast> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                ResourceLocation.CODEC.fieldOf("destination").forGetter(SoundBroadcast::destination),
                ResourceLocation.CODEC.fieldOf("sound").forGetter(SoundBroadcast::sound),
                Codec.FLOAT.optionalFieldOf("volume", 1F).forGetter(SoundBroadcast::volume),
                Codec.FLOAT.optionalFieldOf("pitch", 1F).forGetter(SoundBroadcast::pitch),
                Codec.STRING.optionalFieldOf("source", SoundSource.NEUTRAL.name).forGetter(SoundBroadcast::source)
            ).apply(instance, ::SoundBroadcast)
        }

        val BROADCAST_TYPE = BroadcastType(CODEC)
    }

    override fun getType(): BroadcastType<*> = SpawnNotification.BroadcastTypes.SOUND_BROADCAST

    override fun deliver(
        broadcastContext: BroadcastContext.WithSituations,
    ) {
        val soundEvent = SoundEvent.createVariableRangeEvent(sound)
        broadcastContext.world.playSoundServer(
            broadcastContext.position,
            soundEvent,
            SoundSource.valueOf(source),
            volume,
            pitch
        )

        val debugger = SpawnNotification.debugger.getCaseDebugger(broadcastContext.id.toString())
        debugger.debug("Played sound $soundEvent")
    }
}