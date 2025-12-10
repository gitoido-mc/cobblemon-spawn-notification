package us.timinc.mc.cobblemon.spawnnotification.handler

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.server.level.ServerLevel
import us.timinc.mc.cobblemon.spawnnotification.Broadcaster
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext
import us.timinc.mc.cobblemon.timcore.AbstractHandler
import us.timinc.mc.cobblemon.timcore.TimCore
import us.timinc.mc.cobblemon.timcore.event.EntityDidSpawnEvent
import us.timinc.mc.cobblemon.timcore.getType

object SpawnTriggers : AbstractHandler<EntityDidSpawnEvent<PokemonEntity>>() {
    override fun handle(evt: EntityDidSpawnEvent<PokemonEntity>) {
        val trigger = when (evt.spawner.getType()) {
            TimCore.DataKeys.SpawnerTypes.FISHING -> SpawnNotification.KEYS.TRIGGERS.FISHED
            TimCore.DataKeys.SpawnerTypes.PLAYER -> SpawnNotification.KEYS.TRIGGERS.SPAWNED
            TimCore.DataKeys.SpawnerTypes.SNACK -> SpawnNotification.KEYS.TRIGGERS.SNACKED
            else -> null
        }
        if (trigger == null) return
        Broadcaster.broadcast(
            BroadcastContext(
                evt.entity.pokemon,
                evt.entity.level() as? ServerLevel ?: return,
                evt.entity.position(),
                evt.playerCause
            ),
            trigger
        )
        SpawnNotification.CUSTOM_POKEMON_PROPERTIES.BROADCASTED_SPAWN.entityApplicator(evt.entity, true)
    }
}