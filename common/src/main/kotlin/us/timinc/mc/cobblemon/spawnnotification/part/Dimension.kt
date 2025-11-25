package us.timinc.mc.cobblemon.spawnnotification.part

import net.minecraft.network.chat.Component
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.context.BroadcastContext

object Dimension : Part() {
    override fun getPart(ctx: BroadcastContext): Component =
        SpawnNotification.COMPONENTS.dimension(ctx.world.dimension())
}