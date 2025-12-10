package us.timinc.mc.cobblemon.spawnnotification.api.message

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext

/**
 * A data representation of a translatable component and its interpolated segments.
 */
class Message(
    val message: String?,
    val segments: SegmentList,
    val delimiter: String = "",
    val before: String = "",
    val after: String = "",
) {
    /**
     * Evaluates out a translatable component from the message and its segments.
     */
    fun compose(context: BroadcastContext): MutableComponent? {
        val translatedSegments = segments.compose(context)
        if (translatedSegments.isEmpty() && message == null) return null
        val usedMessage = message ?: "$before${List(translatedSegments.size) { i -> $$"%$${ i + 1 }$s" }.joinToString(delimiter)}$after"
        if (usedMessage.isEmpty()) return Component.empty()
        return Component.translatable(usedMessage, *translatedSegments)
    }
}