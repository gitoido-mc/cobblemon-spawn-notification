package us.timinc.mc.cobblemon.spawnnotification.handler

import com.cobblemon.mod.common.api.events.pokemon.HatchEggEvent
import net.minecraft.server.level.ServerLevel
import us.timinc.mc.cobblemon.spawnnotification.Broadcaster
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext
import us.timinc.mc.cobblemon.timcore.AbstractHandler

object HatchTrigger : AbstractHandler<HatchEggEvent.Post>() {
    override fun handle(evt: HatchEggEvent.Post) {
        Broadcaster.broadcast(
            BroadcastContext(
                evt.pokemon,
                evt.player.level() as? ServerLevel ?: return,
                evt.player.position(),
                evt.player
            ),
            SpawnNotification.KEYS.TRIGGERS.HATCHED
        )
    }
}