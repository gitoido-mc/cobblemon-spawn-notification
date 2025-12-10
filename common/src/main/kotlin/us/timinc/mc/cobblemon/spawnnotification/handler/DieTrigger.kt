package us.timinc.mc.cobblemon.spawnnotification.handler

import com.cobblemon.mod.common.api.events.pokemon.PokemonFaintedEvent
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import us.timinc.mc.cobblemon.spawnnotification.Broadcaster
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext
import us.timinc.mc.cobblemon.timcore.AbstractHandler

object DieTrigger : AbstractHandler<PokemonFaintedEvent>() {
    override fun handle(evt: PokemonFaintedEvent) {
        val entity = evt.pokemon.entity ?: return
        Broadcaster.broadcast(
            BroadcastContext(
                evt.pokemon,
                entity.level() as? ServerLevel ?: return,
                entity.position(),
                entity.lastAttacker as? ServerPlayer ?: return
            ),
            SpawnNotification.KEYS.TRIGGERS.DIED
        )
        SpawnNotification.CUSTOM_POKEMON_PROPERTIES.BROADCASTED_DESPAWN.pokemonApplicator(evt.pokemon, true)
    }
}