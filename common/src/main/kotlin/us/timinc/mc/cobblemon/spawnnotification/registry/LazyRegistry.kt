package us.timinc.mc.cobblemon.spawnnotification.registry

import com.cobblemon.mod.common.api.reactive.EventObservable
import net.minecraft.resources.ResourceLocation

class LazyRegistry<T>(val name: ResourceLocation) {
    private val registered: MutableMap<ResourceLocation, T> = mutableMapOf()

    @JvmField
    val entryRegistered = EventObservable<RegistryEntryLoadedEvent<T>>()

    data class RegistryEntryLoadedEvent<T>(
        val entry: T,
        val key: ResourceLocation,
        val registry: LazyRegistry<T>,
    )

    fun register(key: ResourceLocation, value: T): T {
        if (registered.containsKey(key)) throw Exception("Key $key already present in registry $name.")
        registered[key] = value
        entryRegistered.post(RegistryEntryLoadedEvent(value, key, this))
        return value
    }

    fun get(key: ResourceLocation): T? = registered[key]
}