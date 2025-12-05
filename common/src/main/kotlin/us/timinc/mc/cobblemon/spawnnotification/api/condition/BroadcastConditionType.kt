package us.timinc.mc.cobblemon.spawnnotification.api.condition

import com.mojang.serialization.Lifecycle
import com.mojang.serialization.MapCodec
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification

data class BroadcastConditionType<T : BroadcastCondition>(
    val codec: MapCodec<T>,
) {
    companion object {
        val REGISTRY: Registry<BroadcastConditionType<*>> = MappedRegistry(
            ResourceKey.createRegistryKey(SpawnNotification.modResource("broadcast_condition")),
            Lifecycle.stable()
        )
    }
}
