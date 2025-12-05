package us.timinc.mc.cobblemon.spawnnotification

import net.minecraft.resources.ResourceLocation
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext
import us.timinc.mc.cobblemon.spawnnotification.data.NotificationData

object Broadcaster {
    fun broadcast(broadcastContext: BroadcastContext, trigger: ResourceLocation) {
        val notification = NotificationData.Manager.findMatches(broadcastContext, trigger) ?: return
        val debugger = SpawnNotification.debugger.getCaseDebugger(broadcastContext.id.toString())
        debugger.debug("Found ${notification.id} for $trigger w/context $broadcastContext")
        notification.broadcasts.forEach { broadcast ->
            if (!broadcast.conditions.all { it.matches(broadcastContext) }) return@forEach
            debugger.debug("Broadcasting for ${broadcast.destination}")
            broadcast.deliver(broadcastContext)
        }
    }
}