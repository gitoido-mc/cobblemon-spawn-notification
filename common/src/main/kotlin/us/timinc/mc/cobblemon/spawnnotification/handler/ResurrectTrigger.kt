package us.timinc.mc.cobblemon.spawnnotification.handler

import com.cobblemon.mod.common.api.events.pokemon.FossilRevivedEvent
import net.minecraft.server.level.ServerLevel
import us.timinc.mc.cobblemon.spawnnotification.Broadcaster
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext
import us.timinc.mc.cobblemon.timcore.AbstractHandler

object ResurrectTrigger : AbstractHandler<FossilRevivedEvent>() {
    override fun handle(evt: FossilRevivedEvent) {
        val locationalEntity = evt.pokemon.entity ?: evt.player ?: return
        Broadcaster.broadcast(
            BroadcastContext(
                evt.pokemon,
                locationalEntity.level() as? ServerLevel ?: return,
                locationalEntity.position(),
                evt.player
            ),
            SpawnNotification.KEYS.TRIGGERS.RESURRECTED
        )
        SpawnNotification.CUSTOM_POKEMON_PROPERTIES.BROADCASTED_SPAWN.pokemonApplicator(evt.pokemon, true)
    }
}