package us.timinc.mc.cobblemon.spawnnotification.part

import com.cobblemon.mod.common.util.toBlockPos
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext
import us.timinc.mc.cobblemon.spawnnotification.api.message.Part

object CoordX : Part {
    override fun compose(context: BroadcastContext): MutableComponent =
        Component.literal(context.position.toBlockPos().x.toString())
}