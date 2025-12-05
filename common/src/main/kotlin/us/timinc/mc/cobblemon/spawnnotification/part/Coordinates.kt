package us.timinc.mc.cobblemon.spawnnotification.part

import com.cobblemon.mod.common.util.toBlockPos
import net.minecraft.network.chat.MutableComponent
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext
import us.timinc.mc.cobblemon.spawnnotification.api.message.Part

object Coordinates : Part {
    override fun compose(context: BroadcastContext): MutableComponent =
        SpawnNotification.COMPONENTS.coordinates(context.position.toBlockPos())
}