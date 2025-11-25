package us.timinc.mc.cobblemon.spawnnotification.destination

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.context.BroadcastContext
import us.timinc.mc.cobblemon.spawnnotification.data.AbstractBroadcastData
import us.timinc.mc.cobblemon.timcore.PokemonMatcher
import java.util.*

object ActionBar : PlayerSpecificDestination<ActionBar.BroadcastData>() {
    class BroadcastData(
        override val matcher: List<PokemonMatcher>,
        override val antiMatcher: List<PokemonMatcher>?,
        override val destination: ResourceLocation,
        override val triggers: List<ResourceLocation>,
        val message: String,
        val parts: List<ResourceLocation>,
        val broadcastRange: Int = SpawnNotification.config.broadcastRange,
        val playerLimit: Int = SpawnNotification.config.playerLimit,
        val broadcastAcrossDimensions: Boolean = SpawnNotification.config.broadcastAcrossDimensions,
    ) : AbstractBroadcastData<BroadcastData>(matcher, antiMatcher, destination, triggers) {
        override fun broadcast(broadcastContext: BroadcastContext) {
            broadcast(this, broadcastContext)
        }
    }

    override fun broadcast(broadcastData: BroadcastData, broadcastContext: BroadcastContext) {
        val debugger = SpawnNotification.debugger.getCaseDebugger(broadcastContext.id.toString())
        val parts = broadcastData.parts.map {
            SpawnNotification.REGISTRIES.PARTS.get(it)?.getPart(broadcastContext) ?: Component.empty()
        }.toTypedArray()
        val message = Component.translatable(broadcastData.message, *parts)
        debugger.debug("Message $message")
        getRelevantPlayers(
            broadcastContext.world,
            broadcastContext.position,
            broadcastData.broadcastRange,
            broadcastData.playerLimit,
            broadcastData.broadcastAcrossDimensions
        ).forEach { player ->
            debugger.debug("Sent message to $player")
            player.sendSystemMessage(message, true)
        }
    }

    override val codec: Codec<BroadcastData> = RecordCodecBuilder.create { instance ->
        instance.group(
            PokemonMatcher.STRING_CODEC.listOf().fieldOf("matcher").forGetter(BroadcastData::matcher),
            PokemonMatcher.STRING_CODEC.listOf().optionalFieldOf("antiMatcher")
                .forGetter { Optional.ofNullable(it.antiMatcher) },
            ResourceLocation.CODEC.fieldOf("destination").forGetter(BroadcastData::destination),
            ResourceLocation.CODEC.listOf().fieldOf("triggers").forGetter(BroadcastData::triggers),
            Codec.STRING.fieldOf("message").forGetter(BroadcastData::message),
            ResourceLocation.CODEC.listOf().fieldOf("parts").orElse(emptyList()).forGetter(BroadcastData::parts),
            Codec.INT.fieldOf("broadcastRange").orElse(SpawnNotification.config.broadcastRange)
                .forGetter(BroadcastData::broadcastRange),
            Codec.INT.fieldOf("playerLimit").orElse(SpawnNotification.config.playerLimit)
                .forGetter(BroadcastData::playerLimit),
            Codec.BOOL.fieldOf("announceAcrossDimensions").orElse(SpawnNotification.config.broadcastAcrossDimensions)
                .forGetter(BroadcastData::broadcastAcrossDimensions)
        )
            .apply(instance) { matcher, antiMatcher, destination, triggers, message, parts, broadcastRange, playerLimit, broadcastAcrossDimensions ->
                BroadcastData(
                    matcher,
                    antiMatcher.orElse(null),
                    destination,
                    triggers,
                    message,
                    parts,
                    broadcastRange,
                    playerLimit,
                    broadcastAcrossDimensions
                )
            }
    }
}