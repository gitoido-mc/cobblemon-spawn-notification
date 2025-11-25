package us.timinc.mc.cobblemon.spawnnotification.data

import com.cobblemon.mod.common.pokemon.Pokemon
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.profiling.ProfilerFiller
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.context.BroadcastContext
import us.timinc.mc.cobblemon.timcore.AbstractReloadListener
import us.timinc.mc.cobblemon.timcore.PokemonMatcher

abstract class AbstractBroadcastData<T : AbstractBroadcastData<T>>(
    open val matcher: List<PokemonMatcher>,
    open val antiMatcher: List<PokemonMatcher>?,
    open val destination: ResourceLocation,
    open val triggers: List<ResourceLocation>,
) {
    var id: ResourceLocation? = null

    abstract fun broadcast(broadcastContext: BroadcastContext)

    fun matches(pokemon: Pokemon) =
        matcher.any { it.matches(pokemon) } && antiMatcher?.any { it.matches(pokemon) } != true

    object Manager : AbstractReloadListener(Gson(), "broadcaster") {
        private val broadcasters: MutableMap<ResourceLocation, MutableList<AbstractBroadcastData<*>>> = mutableMapOf()

        override fun apply(
            objectMap: MutableMap<ResourceLocation, JsonElement>,
            resourceManager: ResourceManager,
            profilerFiller: ProfilerFiller,
        ) {
            broadcasters.clear()
            objectMap.entries.forEach { (id, json) ->
                val destination = ResourceLocation.parse(json.asJsonObject.get("destination").asString)
                val codec = SpawnNotification.REGISTRIES.DESTINATIONS.get(destination)?.codec ?: return@forEach
                val broadcaster = codec.parse(JsonOps.INSTANCE, json).orThrow
                broadcaster.id = id
                for (trigger in broadcaster.triggers) {
                    broadcasters.getOrPut(trigger, ::mutableListOf).add(broadcaster)
                }
            }
        }

        fun findMatches(pokemon: Pokemon, trigger: ResourceLocation) =
            broadcasters[trigger]?.filter { it.matches(pokemon) } ?: emptyList()
    }
}