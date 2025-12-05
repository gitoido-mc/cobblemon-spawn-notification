package us.timinc.mc.cobblemon.spawnnotification.api.message

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext

/**
 * A data representation of a translatable component and its interpolated segments.
 */
class Message(
    val message: String,
    val segments: SegmentList,
) {
    // Evaluates out a translatable component from the message and its segments.
    fun compose(context: BroadcastContext): MutableComponent =
        if (message.isEmpty()) Component.empty() else Component.translatable(
            message,
            *segments.compose(context)
        )

    // Merges in another message, giving its properties priority if they aren't empty.
    fun merge(otherMessage: Message): Message =
        Message(
            otherMessage.message.takeIf { !it.isEmpty() } ?: message,
            segments.merge(otherMessage.segments)
        )
}