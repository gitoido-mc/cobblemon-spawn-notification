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
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.PlayerSpecificBroadcast
import us.timinc.mc.cobblemon.spawnnotification.api.condition.BroadcastCondition
import us.timinc.mc.cobblemon.spawnnotification.api.extension.withPossibleStyle
import us.timinc.mc.cobblemon.spawnnotification.api.message.Message
import us.timinc.mc.cobblemon.spawnnotification.api.message.SegmentList
import java.util.*
import kotlin.jvm.optionals.getOrNull

class ChatBroadcast(
    override val destination: ResourceLocation,
    override val params: Params,
    override val children: List<Broadcast.Child<ChildParams>>,
    override val conditions: List<BroadcastCondition>,
) : PlayerSpecificBroadcast<ChatBroadcast.Params, ChatBroadcast.ChildParams>() {
    companion object {
        val PARAMS_CODEC: MapCodec<Params> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.STRING.optionalFieldOf("message", $$"%1$s").forGetter { it.message.message },
                SegmentList.MAP_CODEC.optionalFieldOf("segments", emptyMap())
                    .forGetter { SegmentList.toEither(it.message.segments.segments) },
                Codec.INT.optionalFieldOf("broadcastRange", -1).forGetter(Params::broadcastRange),
                Codec.INT.optionalFieldOf("playerLimit", -1).forGetter(Params::playerLimit),
                Codec.BOOL.optionalFieldOf("broadcastAcrossDimensions", false)
                    .forGetter(Params::broadcastAcrossDimensions),
            ).apply(instance) { message, segments, broadcastRange, playerLimit, broadcastAcrossDimensions ->
                Params(
                    Message(message, SegmentList(SegmentList.fromEither(segments))),
                    broadcastRange,
                    playerLimit,
                    broadcastAcrossDimensions
                )
            }
        }

        val CHILD_CODEC: MapCodec<ChildParams> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.STRING.optionalFieldOf("message").forGetter { Optional.ofNullable(it.message?.message) },
                SegmentList.MAP_CODEC.optionalFieldOf("segments")
                    .forGetter { Optional.ofNullable(it.message?.segments?.segments?.let(SegmentList::toEither)) },
                Codec.INT.optionalFieldOf("broadcastRange").forGetter { Optional.ofNullable(it.broadcastRange) },
                Codec.INT.optionalFieldOf("playerLimit").forGetter { Optional.ofNullable(it.playerLimit) },
                Codec.BOOL.optionalFieldOf("broadcastAcrossDimensions")
                    .forGetter { Optional.ofNullable(it.broadcastAcrossDimensions) },
            ).apply(instance) { message, segments, broadcastRange, playerLimit, broadcastAcrossDimensions ->
                ChildParams(
                    if (message.isEmpty && segments.isEmpty) null else Message(
                        message.orElse(""),
                        segments.getOrNull()?.let { SegmentList(SegmentList.fromEither(it)) } ?: SegmentList()
                    ),
                    broadcastRange.getOrNull(),
                    playerLimit.getOrNull(),
                    broadcastAcrossDimensions.getOrNull()
                )
            }
        }

        val BROADCAST_TYPE = BroadcastType(
            PARAMS_CODEC,
            CHILD_CODEC,
            ::ChatBroadcast
        )
    }

    class Params(
        val message: Message,
        val broadcastRange: Int = -1,
        val playerLimit: Int = -1,
        val broadcastAcrossDimensions: Boolean = false,
    )

    class ChildParams(
        val message: Message?,
        val broadcastRange: Int?,
        val playerLimit: Int?,
        val broadcastAcrossDimensions: Boolean?,
    )

    override fun getType(): BroadcastType<*, *, *> = SpawnNotification.BroadcastTypes.CHAT_BROADCAST

    override fun mergeChild(params: Params, childParams: ChildParams): Params =
        Params(
            childParams.message?.let { params.message.merge(it) } ?: params.message,
            childParams.broadcastRange ?: params.broadcastRange,
            childParams.playerLimit ?: params.playerLimit,
            childParams.broadcastAcrossDimensions ?: params.broadcastAcrossDimensions,
        )

    override fun broadcast(broadcastContext: BroadcastContext, params: Params) {
        val debugger = SpawnNotification.debugger.getCaseDebugger(broadcastContext.id.toString())
        val message = params.message.compose(broadcastContext)
            .withPossibleStyle(ChatFormatting.getByName(SpawnNotification.config.baseColor))
        debugger.debug("Message $message")
        getRelevantPlayers(
            broadcastContext.world,
            broadcastContext.position,
            params.broadcastRange,
            params.playerLimit,
            params.broadcastAcrossDimensions
        ).forEach { player ->
            debugger.debug("Sent message to $player")
            player.sendSystemMessage(message)
        }
    }
}