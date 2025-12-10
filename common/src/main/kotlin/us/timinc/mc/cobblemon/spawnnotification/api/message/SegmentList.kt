package us.timinc.mc.cobblemon.spawnnotification.api.message

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.UnboundedMapCodec
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext
import us.timinc.mc.cobblemon.spawnnotification.api.codec.IntKeyCodec
import us.timinc.mc.cobblemon.spawnnotification.api.extension.fromEitherOnRightMap
import us.timinc.mc.cobblemon.spawnnotification.api.extension.toEitherOnRightMap
import us.timinc.mc.cobblemon.spawnnotification.data.SegmentAdditionDataManager
import us.timinc.mc.cobblemon.spawnnotification.segment.ReferenceSegment

/**
 * A list of segments, with a couple of convenience functions attached.
 */
class SegmentList(
    val segments: Map<Int, Segment> = emptyMap(),
    var id: ResourceLocation? = null,
) {
    companion object {
        val MAP_CODEC: UnboundedMapCodec<Int, Either<ResourceLocation, Segment>> =
            Codec.unboundedMap(IntKeyCodec, Segment.CODEC)

        fun fromEither(map: Map<Int, Either<ResourceLocation, Segment>>): Map<Int, Segment> =
            map.fromEitherOnRightMap(::ReferenceSegment)

        fun toEither(map: Map<Int, Segment>): Map<Int, Either<ResourceLocation, Segment>> =
            map.toEitherOnRightMap()
    }

    /**
     * Evaluates out the Map into a proper Array of Components, ready to be inserted into a translatable component.
     */
    fun compose(context: BroadcastContext.WithSituations): Array<Component> {
        val usedSegments = segments.toMutableMap()
        id?.let { id ->
            val additions = SegmentAdditionDataManager.getAdditionsFor(id)
            var currentIndex = usedSegments.keys.filter { it > 0 }.maxOrNull() ?: return@let
            additions.forEach { additionData ->
                additionData.segments.segments.forEach { (_, v) ->
                    usedSegments[++currentIndex] = v
                }
            }
        }

        if (usedSegments.isEmpty()) return emptyArray()

        val validSegments = usedSegments.filter { (k) -> k > 0 }.toSortedMap()
        val maxIndex = validSegments.keys.maxOrNull() ?: return emptyArray()
        val composed: MutableList<Component?> = MutableList(maxIndex) { null }
        for ((index, segment) in validSegments) {
            composed[index - 1] = segment.validateAndCompose(context)
        }

        return composed.filterNotNull().toTypedArray()
    }

    /**
     * Returns a new SegmentList with all the segments in this list, substituting in any segments at the same index in the other list.
     */
    fun merge(otherList: SegmentList): SegmentList {
        if (segments.isEmpty()) return SegmentList(emptyMap())

        val newList = segments.toMutableMap()
        for ((index, segment) in otherList.segments) {
            newList[index] = segment
        }
        return SegmentList(newList)
    }
}