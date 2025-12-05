package us.timinc.mc.cobblemon.spawnnotification.segment

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext
import us.timinc.mc.cobblemon.spawnnotification.api.message.Segment
import us.timinc.mc.cobblemon.spawnnotification.api.message.SegmentType
import us.timinc.mc.cobblemon.spawnnotification.data.SegmentData

// A reference segment, which points to and composes based on a stand-alone segment in the registry, falling back if it
// can't find it. May also be represented as a plain string.
class ReferenceSegment(
    val ref: ResourceLocation,
    override val fallback: String = "",
) : Segment {
    companion object {
        val CODEC: MapCodec<ReferenceSegment> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                ResourceLocation.CODEC.fieldOf("ref").forGetter(ReferenceSegment::ref),
                Codec.STRING.optionalFieldOf("fallback", "").forGetter(ReferenceSegment::fallback)
            ).apply(instance, ::ReferenceSegment)
        }

        val SEGMENT_TYPE = SegmentType(CODEC)
    }

    override fun getType(): SegmentType<*> = SpawnNotification.SegmentTypes.REFERENCE

    override fun compose(context: BroadcastContext): Component =
        SegmentData.find(ref)?.compose(context) ?: getFallback()
}