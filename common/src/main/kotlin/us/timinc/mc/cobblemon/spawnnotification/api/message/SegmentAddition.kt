package us.timinc.mc.cobblemon.spawnnotification.api.message

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation

class SegmentAddition(
    val target: ResourceLocation,
    val segments: SegmentList,
) {
    companion object {
        val CODEC: Codec<SegmentAddition> = RecordCodecBuilder.create { instance ->
            instance.group(
                ResourceLocation.CODEC.fieldOf("target").forGetter(SegmentAddition::target),
                SegmentList.MAP_CODEC.fieldOf("segments").forGetter { SegmentList.toEither(it.segments.segments) },
            ).apply(instance) { target, segments ->
                SegmentAddition(
                    target,
                    SegmentList(SegmentList.fromEither(segments))
                )
            }
        }
    }

    var id: ResourceLocation? = null

}