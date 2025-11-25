package us.timinc.mc.cobblemon.spawnnotification.part

import net.minecraft.network.chat.Component
import us.timinc.mc.cobblemon.spawnnotification.context.BroadcastContext

object PlayerName : Part() {
    override fun getPart(ctx: BroadcastContext): Component? = ctx.player?.name
}