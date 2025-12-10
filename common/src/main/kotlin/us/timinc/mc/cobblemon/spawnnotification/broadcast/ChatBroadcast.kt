package us.timinc.mc.cobblemon.spawnnotification.broadcast

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.ChatFormatting
import net.minecraft.resources.ResourceLocation
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.Broadcast
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastType
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.PlayerSpecific
import us.timinc.mc.cobblemon.spawnnotification.api.extension.withPossibleStyle
import us.timinc.mc.cobblemon.spawnnotification.api.message.Message
import us.timinc.mc.cobblemon.spawnnotification.api.message.SegmentList
import java.util.*
import kotlin.jvm.optionals.getOrNull

class ChatBroadcast(
    override val destination: ResourceLocation,
    val message: Message,
    val broadcastRange: Int = -1,
    val playerLimit: Int = -1,
    val broadcastAcrossDimensions: Boolean = false,
    val actionBar: Boolean = false,
    val delimiter: String = "",
    val before: String = "",
    val after: String = "",
) : PlayerSpecific, Broadcast {
    companion object {
        val CODEC: MapCodec<ChatBroadcast> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                ResourceLocation.CODEC.fieldOf("destination").forGetter(ChatBroadcast::destination),
                Codec.STRING.optionalFieldOf("message").forGetter { Optional.ofNullable(it.message.message) },
                SegmentList.MAP_CODEC.optionalFieldOf("segments", emptyMap())
                    .forGetter { SegmentList.toEither(it.message.segments.segments) },
                Codec.INT.optionalFieldOf("broadcastRange", SpawnNotification.config.broadcastRange)
                    .forGetter(ChatBroadcast::broadcastRange),
                Codec.INT.optionalFieldOf("playerLimit", SpawnNotification.config.playerLimit)
                    .forGetter(ChatBroadcast::playerLimit),
                Codec.BOOL.optionalFieldOf(
                    "broadcastAcrossDimensions",
                    SpawnNotification.config.broadcastAcrossDimensions
                )
                    .forGetter(ChatBroadcast::broadcastAcrossDimensions),
                Codec.BOOL.optionalFieldOf("actionBar", SpawnNotification.config.actionBar)
                    .forGetter(ChatBroadcast::actionBar),
                Codec.STRING.optionalFieldOf("delimiter", "").forGetter(ChatBroadcast::delimiter),
                Codec.STRING.optionalFieldOf("before", "").forGetter(ChatBroadcast::before),
                Codec.STRING.optionalFieldOf("after", "").forGetter(ChatBroadcast::after),
            )
                .apply(instance) { destination, message, segments, broadcastRange, playerLimit, broadcastAcrossDimensions, actionBar, delimiter, before, after ->
                    ChatBroadcast(
                        destination,
                        Message(message.getOrNull(), SegmentList(SegmentList.fromEither(segments)), delimiter, before, after),
                        broadcastRange,
                        playerLimit,
                        broadcastAcrossDimensions,
                        actionBar
                    )
                }
        }

        val BROADCAST_TYPE = BroadcastType(CODEC)
    }

    override fun getType(): BroadcastType<*> = SpawnNotification.BroadcastTypes.CHAT_BROADCAST

    override fun deliver(broadcastContext: BroadcastContext) {
        val debugger = SpawnNotification.debugger.getCaseDebugger(broadcastContext.id.toString())
        val message = message.compose(broadcastContext)
            ?.withPossibleStyle(ChatFormatting.getByName(SpawnNotification.config.baseColor)) ?: return
        debugger.debug("Message $message")
        getRelevantPlayers(
            broadcastContext.world,
            broadcastContext.position,
            broadcastRange,
            playerLimit,
            broadcastAcrossDimensions
        ).forEach { player ->
            debugger.debug("Sent message to $player")
            player.sendSystemMessage(message, actionBar)
        }
    }
}