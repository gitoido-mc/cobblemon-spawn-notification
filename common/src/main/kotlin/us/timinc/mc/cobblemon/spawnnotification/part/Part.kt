package us.timinc.mc.cobblemon.spawnnotification.part

import net.minecraft.network.chat.Component
import us.timinc.mc.cobblemon.spawnnotification.context.BroadcastContext

abstract class Part {
    abstract fun getPart(ctx: BroadcastContext): Component?
}