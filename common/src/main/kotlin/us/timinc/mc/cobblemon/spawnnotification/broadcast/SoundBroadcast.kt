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
import us.timinc.mc.cobblemon.spawnnotification.api.condition.BroadcastCondition
import java.util.*

class SoundBroadcast(
    override val destination: ResourceLocation,
    override val params: Params,
    override val children: List<Broadcast.Child<ChildParams>>,
    override val conditions: List<BroadcastCondition>,
) : Broadcast<SoundBroadcast.Params, SoundBroadcast.ChildParams> {
    companion object {
        val PARAMS_CODEC: MapCodec<Params> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                ResourceLocation.CODEC.fieldOf("sound").forGetter(Params::sound),
                Codec.FLOAT.optionalFieldOf("volume", 1F).forGetter(Params::volume),
                Codec.FLOAT.optionalFieldOf("pitch", 1F).forGetter(Params::pitch),
                Codec.STRING.optionalFieldOf("source", SoundSource.NEUTRAL.name).forGetter(Params::source)
            ).apply(instance, ::Params)
        }

        val CHILD_CODEC: MapCodec<ChildParams> = RecordCodecBuilder.mapCodec { childInstance ->
            childInstance.group(
                ResourceLocation.CODEC.optionalFieldOf("sound").forGetter { Optional.ofNullable(it.sound) },
                Codec.FLOAT.optionalFieldOf("volume").forGetter { Optional.ofNullable(it.volume) },
                Codec.FLOAT.optionalFieldOf("pitch").forGetter { Optional.ofNullable(it.pitch) },
                Codec.STRING.optionalFieldOf("source").forGetter { Optional.ofNullable(it.source) }
            ).apply(childInstance) { sound, volume, pitch, source ->
                ChildParams(
                    sound.orElse(null),
                    volume.orElse(null),
                    pitch.orElse(null),
                    source.orElse(null)
                )
            }
        }

        val BROADCAST_TYPE = BroadcastType(
            PARAMS_CODEC,
            CHILD_CODEC,
            ::SoundBroadcast
        )
    }

    class Params(
        val sound: ResourceLocation,
        val volume: Float,
        val pitch: Float,
        val source: String,
    )

    class ChildParams(
        val sound: ResourceLocation?,
        val volume: Float?,
        val pitch: Float?,
        val source: String?,
    )

    override fun getType(): BroadcastType<*, *, *> = SpawnNotification.BroadcastTypes.SOUND_BROADCAST

    override fun mergeChild(params: Params, childParams: ChildParams): Params =
        Params(
            childParams.sound ?: params.sound,
            childParams.volume ?: params.volume,
            childParams.pitch ?: params.pitch,
            childParams.source ?: params.source,
        )

    override fun broadcast(
        broadcastContext: BroadcastContext,
        params: Params,
    ) {
        val soundEvent = SoundEvent.createVariableRangeEvent(params.sound)
        broadcastContext.world.playSoundServer(
            broadcastContext.position,
            soundEvent,
            SoundSource.valueOf(params.source),
            params.volume,
            params.pitch
        )

        val debugger = SpawnNotification.debugger.getCaseDebugger(broadcastContext.id.toString())
        debugger.debug("Played sound $soundEvent")
    }
}