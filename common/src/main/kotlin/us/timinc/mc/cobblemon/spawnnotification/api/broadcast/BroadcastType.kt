package us.timinc.mc.cobblemon.spawnnotification.api.broadcast

import com.mojang.serialization.Lifecycle
import com.mojang.serialization.MapCodec
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification

/**
 * Add broadcast types of your own if you choose!
 */
data class BroadcastType<T : Broadcast>(
    val codec: MapCodec<T>,
) {
    companion object {
        val REGISTRY: Registry<BroadcastType<*>> = MappedRegistry(
            ResourceKey.createRegistryKey(SpawnNotification.modResource("broadcast_types")),
            Lifecycle.stable()
        )
    }
}