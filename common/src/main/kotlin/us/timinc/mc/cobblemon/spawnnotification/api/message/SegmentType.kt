package us.timinc.mc.cobblemon.spawnnotification.api.message

import com.mojang.serialization.Lifecycle
import com.mojang.serialization.MapCodec
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification

/**
 * Add segment types of your own if you choose!
 */
data class SegmentType<T : Segment>(
    val codec: MapCodec<T>,
) {
    companion object {
        val REGISTRY: Registry<SegmentType<*>> = MappedRegistry(
            ResourceKey.createRegistryKey(SpawnNotification.modResource("segment")),
            Lifecycle.stable()
        )
    }
}
