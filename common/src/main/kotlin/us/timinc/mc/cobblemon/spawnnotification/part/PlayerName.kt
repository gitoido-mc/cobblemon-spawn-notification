package us.timinc.mc.cobblemon.spawnnotification.part

import net.minecraft.network.chat.MutableComponent
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext
import us.timinc.mc.cobblemon.spawnnotification.api.message.Part

object PlayerName : Part {
    override fun compose(context: BroadcastContext): MutableComponent? {
        val player =
            context.player ?: context.pokemon.entity?.let { context.world.getNearestPlayer(it, 10.0) } ?: return null
        return player.name.plainCopy()
    }
}