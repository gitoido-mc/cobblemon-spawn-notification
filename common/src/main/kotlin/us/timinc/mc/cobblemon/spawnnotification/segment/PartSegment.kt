package us.timinc.mc.cobblemon.spawnnotification.segment

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext
import us.timinc.mc.cobblemon.spawnnotification.api.extension.withPossibleStyle
import us.timinc.mc.cobblemon.spawnnotification.api.message.Part
import us.timinc.mc.cobblemon.spawnnotification.api.message.Segment
import us.timinc.mc.cobblemon.spawnnotification.api.message.SegmentType

/**
 * A part segment, which uses internal logic to create a component to compose out to based on the context.
 */
class PartSegment(
    val part: ResourceLocation,
    override val fallback: String = "",
) : Segment {
    companion object {
        val CODEC: MapCodec<PartSegment> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                ResourceLocation.CODEC.fieldOf("part").forGetter(PartSegment::part),
                Codec.STRING.optionalFieldOf("fallback", "").forGetter(PartSegment::fallback)
            ).apply(instance, ::PartSegment)
        }

        val SEGMENT_TYPE = SegmentType(CODEC)
    }

    override var id: ResourceLocation? = null

    override fun getType(): SegmentType<*> = SpawnNotification.SegmentTypes.PART

    override fun compose(context: BroadcastContext.WithSituations): MutableComponent? =
        (Part.REGISTRY[part]?.compose(context) ?: getFallback())?.withPossibleStyle(
            ChatFormatting.getByName(
                SpawnNotification.config.partColors[part.toString()] ?: SpawnNotification.config.baseColor
            )
        )
}