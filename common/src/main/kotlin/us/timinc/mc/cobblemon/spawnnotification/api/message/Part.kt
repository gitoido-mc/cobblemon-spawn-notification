package us.timinc.mc.cobblemon.spawnnotification.api.message

import com.mojang.serialization.Lifecycle
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceKey
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext

/**
 * A message segment whose content depends on the context of the broadcast.
 */
interface Part {
    companion object {
        val REGISTRY: Registry<Part> = MappedRegistry(
            ResourceKey.createRegistryKey(SpawnNotification.modResource("parts")),
            Lifecycle.stable()
        )
    }

    /**
     * Evaluates out the value of this message part.
     */
    fun compose(context: BroadcastContext): MutableComponent?
}