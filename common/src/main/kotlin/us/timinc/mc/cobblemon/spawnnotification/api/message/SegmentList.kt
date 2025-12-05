package us.timinc.mc.cobblemon.spawnnotification.api.message

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.UnboundedMapCodec
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext
import us.timinc.mc.cobblemon.spawnnotification.api.codec.IntKeyCodec
import us.timinc.mc.cobblemon.spawnnotification.api.extension.getHomogenized
import us.timinc.mc.cobblemon.spawnnotification.segment.ReferenceSegment

// A list of segments, with a couple of convenience functions attached.
class SegmentList(
    val segments: Map<Int, Segment> = emptyMap(),
) {
    companion object {
        val MAP_CODEC: UnboundedMapCodec<Int, Either<ResourceLocation, Segment>> =
            Codec.unboundedMap(IntKeyCodec, Segment.CODEC)

        fun fromEither(map: Map<Int, Either<ResourceLocation, Segment>>): Map<Int, Segment> =
            map.entries.fold(mutableMapOf()) { acc, (k, v) ->
                acc.plus(k to v.mapLeft(::ReferenceSegment).getHomogenized()).toMutableMap()
            }

        fun toEither(map: Map<Int, Segment>): Map<Int, Either<ResourceLocation, Segment>> =
            map.entries.fold(mutableMapOf()) { acc, (k, v) ->
                acc.plus(k to Either<ResourceLocation, Segment>.right(v)).toMutableMap()
            }
    }

    // Turns the Map into a proper Array of Components, ready to be inserted into a translatable component.
    fun compose(context: BroadcastContext): Array<Component> {
        if (segments.isEmpty()) return emptyArray()

        val validSegments = segments.filter { (k) -> k > 0 }
        val maxIndex = validSegments.keys.maxOrNull() ?: return emptyArray()
        val composed: MutableList<Component> = MutableList(maxIndex) { Component.empty() }
        for ((index, segment) in validSegments) {
            composed[index - 1] = segment.compose(context)
        }

        return composed.toTypedArray()
    }

    // Returns a new SegmentList with all the segments in this list, substituting in any segments at the same index in
    // the other list.
    fun merge(otherList: SegmentList): SegmentList {
        if (segments.isEmpty()) return SegmentList(emptyMap())

        val newList = segments.toMutableMap()
        for ((index, segment) in otherList.segments) {
            newList[index] = segment
        }
        return SegmentList(newList)
    }
}