package us.timinc.mc.cobblemon.spawnnotification.segment

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext
import us.timinc.mc.cobblemon.spawnnotification.api.condition.BroadcastCondition
import us.timinc.mc.cobblemon.spawnnotification.api.message.Message
import us.timinc.mc.cobblemon.spawnnotification.api.message.Segment
import us.timinc.mc.cobblemon.spawnnotification.api.message.SegmentList
import us.timinc.mc.cobblemon.spawnnotification.api.message.SegmentType
import java.util.*
import kotlin.jvm.optionals.getOrNull

/**
 * A full segment, with its own message and conditions. Composes out to whatever its message composes out to if the
 * context meets the conditions, otherwise falls back.
 */
class FullSegment(
    val message: Message,
    override val fallback: String = "",
    val conditions: List<BroadcastCondition> = emptyList(),
    val delimiter: String = "",
    val before: String = "",
    val after: String = "",
) : Segment {
    companion object {
        val CODEC: MapCodec<FullSegment> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.STRING.optionalFieldOf("message").forGetter { Optional.ofNullable(it.message.message) },
                SegmentList.MAP_CODEC.optionalFieldOf("segments", emptyMap())
                    .forGetter { SegmentList.toEither(it.message.segments.segments) },
                Codec.STRING.optionalFieldOf("fallback", "").forGetter(FullSegment::fallback),
                BroadcastCondition.CODEC.listOf().optionalFieldOf("conditions", emptyList())
                    .forGetter(FullSegment::conditions),
                Codec.STRING.optionalFieldOf("delimiter", "").forGetter(FullSegment::delimiter),
                Codec.STRING.optionalFieldOf("before", "").forGetter(FullSegment::before),
                Codec.STRING.optionalFieldOf("after", "").forGetter(FullSegment::after),
            ).apply(instance) { message, segments, fallback, conditions, delimiter, before, after ->
                FullSegment(
                    Message(
                        message.getOrNull(),
                        SegmentList(SegmentList.fromEither(segments)),
                        delimiter,
                        before,
                        after
                    ),
                    fallback,
                    conditions,
                )
            }
        }

        val SEGMENT_TYPE = SegmentType(CODEC)
    }

    override var id: ResourceLocation?
        get() = message.segments.id
        set(value) {
            message.segments.id = value
        }

    override fun getType(): SegmentType<*> = SpawnNotification.SegmentTypes.FULL

    override fun compose(context: BroadcastContext.WithSituations): Component? =
        if (!conditions.all { it.matches(context) }) getFallback() else message.compose(context)
}