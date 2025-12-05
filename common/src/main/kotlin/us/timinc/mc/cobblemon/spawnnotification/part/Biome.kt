package us.timinc.mc.cobblemon.spawnnotification.part

import com.cobblemon.mod.common.util.toBlockPos
import net.minecraft.network.chat.MutableComponent
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext
import us.timinc.mc.cobblemon.spawnnotification.api.message.Part

object Biome : Part {
    override fun compose(context: BroadcastContext): MutableComponent =
        SpawnNotification.COMPONENTS.biome(context.world.getBiome(context.position.toBlockPos()))
}