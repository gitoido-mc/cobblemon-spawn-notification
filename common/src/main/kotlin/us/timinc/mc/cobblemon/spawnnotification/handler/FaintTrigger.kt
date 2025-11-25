package us.timinc.mc.cobblemon.spawnnotification.handler

import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent
import net.minecraft.server.level.ServerLevel
import us.timinc.mc.cobblemon.spawnnotification.Broadcaster
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.context.BroadcastContext
import us.timinc.mc.cobblemon.timcore.AbstractHandler

object FaintTrigger : AbstractHandler<BattleFaintedEvent>() {
    override fun handle(evt: BattleFaintedEvent) {
        val pokemon = evt.killed.effectedPokemon
        if (!pokemon.isWild()) return
        val entity = evt.killed.entity ?: return
        val fainter = evt.killed.facedOpponents.firstOrNull { it.effectedPokemon.getOwnerPlayer() != null }?.effectedPokemon?.getOwnerPlayer()
        Broadcaster.broadcast(
            BroadcastContext(
                pokemon,
                entity.level() as? ServerLevel ?: return,
                entity.position(),
                fainter
            ),
            SpawnNotification.KEYS.TRIGGERS.FAINTED
        )
        SpawnNotification.CUSTOM_POKEMON_PROPERTIES.BROADCASTED_DESPAWN.pokemonApplicator(pokemon, true)
    }
}