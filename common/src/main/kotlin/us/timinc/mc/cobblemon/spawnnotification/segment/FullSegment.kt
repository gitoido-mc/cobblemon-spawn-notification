package us.timinc.mc.cobblemon.spawnnotification.segment

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.chat.Component
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext
import us.timinc.mc.cobblemon.spawnnotification.api.condition.BroadcastCondition
import us.timinc.mc.cobblemon.spawnnotification.api.message.Message
import us.timinc.mc.cobblemon.spawnnotification.api.message.Segment
import us.timinc.mc.cobblemon.spawnnotification.api.message.SegmentList
import us.timinc.mc.cobblemon.spawnnotification.api.message.SegmentType

// A full segment, with its own message and conditions. Composes out to whatever its message composes out to if the
// context meets the conditions, otherwise falls back.
class FullSegment(
    val message: Message,
    override val fallback: String = "",
    val conditions: List<BroadcastCondition> = emptyList(),
) : Segment {
    companion object {
        val CODEC: MapCodec<FullSegment> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.STRING.optionalFieldOf("message", $$"%1$s").forGetter { it.message.message },
                SegmentList.MAP_CODEC.optionalFieldOf("segments", emptyMap())
                    .forGetter { SegmentList.toEither(it.message.segments.segments) },
                Codec.STRING.optionalFieldOf("fallback", "").forGetter(FullSegment::fallback),
                BroadcastCondition.CODEC.listOf().optionalFieldOf("conditions", emptyList())
                    .forGetter(FullSegment::conditions)
            ).apply(instance) { message, segments, fallback, conditions ->
                FullSegment(
                    Message(message, SegmentList(SegmentList.fromEither(segments))),
                    fallback,
                    conditions,
                )
            }
        }

        val SEGMENT_TYPE = SegmentType(CODEC)
    }

    override fun getType(): SegmentType<*> = SpawnNotification.SegmentTypes.FULL

    override fun compose(context: BroadcastContext): Component =
        if (!conditions.all { it.matches(context) }) getFallback() else message.compose(context)
}