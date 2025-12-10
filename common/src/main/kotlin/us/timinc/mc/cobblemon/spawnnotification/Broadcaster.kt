package us.timinc.mc.cobblemon.spawnnotification

import net.minecraft.resources.ResourceLocation
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext
import us.timinc.mc.cobblemon.spawnnotification.data.BroadcastDataManager
import us.timinc.mc.cobblemon.spawnnotification.data.SituationDataManager

object Broadcaster {
    fun broadcast(broadcastContext: BroadcastContext, trigger: ResourceLocation) {
        val situations = SituationDataManager.findMatches(broadcastContext, trigger)
        situations.forEach { notificationId ->
            BroadcastDataManager.findAllBroadcastsForNotification(notificationId).forEach { broadcast ->
                broadcast.deliver(broadcastContext)
            }
        }
    }
}