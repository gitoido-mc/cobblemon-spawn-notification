package us.timinc.mc.cobblemon.spawnnotification.part

import com.cobblemon.mod.common.util.toBlockPos
import net.minecraft.network.chat.Component
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.context.BroadcastContext

object Coordinates : Part() {
    override fun getPart(ctx: BroadcastContext): Component =
        SpawnNotification.COMPONENTS.coordinates(ctx.position.toBlockPos())
}