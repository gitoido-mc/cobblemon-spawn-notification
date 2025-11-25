package us.timinc.mc.cobblemon.spawnnotification

import net.minecraft.resources.ResourceLocation
import us.timinc.mc.cobblemon.spawnnotification.context.BroadcastContext
import us.timinc.mc.cobblemon.spawnnotification.data.AbstractBroadcastData
import us.timinc.mc.cobblemon.timcore.TimCore

object Broadcaster {
    fun broadcast(broadcastContext: BroadcastContext, trigger: ResourceLocation) {
        val broadcasts = AbstractBroadcastData.Manager.findMatches(broadcastContext.pokemon, trigger)

        val debugger = SpawnNotification.debugger.getCaseDebugger(broadcastContext.id.toString())
        debugger.debug("Found ${broadcasts.size} broadcasts for $trigger w/context $broadcastContext")

        for (broadcastData in broadcasts) {
            debugger.debug("Broadcasting for ${broadcastData.id}")
            broadcastData.broadcast(broadcastContext)
        }
    }
}