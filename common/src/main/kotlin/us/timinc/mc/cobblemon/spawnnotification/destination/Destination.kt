package us.timinc.mc.cobblemon.spawnnotification.destination

import com.mojang.serialization.Codec
import us.timinc.mc.cobblemon.spawnnotification.context.BroadcastContext
import us.timinc.mc.cobblemon.spawnnotification.data.AbstractBroadcastData

abstract class Destination<T : AbstractBroadcastData<T>> {
    abstract fun broadcast(broadcastData: T, broadcastContext: BroadcastContext)
    abstract val codec: Codec<T>
}