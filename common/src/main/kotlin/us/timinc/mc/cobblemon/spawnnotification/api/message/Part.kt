package us.timinc.mc.cobblemon.spawnnotification.api.message

import com.mojang.serialization.Lifecycle
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceKey
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext

interface Part {
    companion object {
        val REGISTRY: Registry<Part> = MappedRegistry(
            ResourceKey.createRegistryKey(SpawnNotification.modResource("parts")),
            Lifecycle.stable()
        )
    }

    fun compose(context: BroadcastContext): MutableComponent?
}