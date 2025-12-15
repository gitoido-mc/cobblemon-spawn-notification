package us.timinc.mc.cobblemon.spawnnotification.data

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.profiling.ProfilerFiller
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.Situation
import us.timinc.mc.cobblemon.timcore.AbstractReloadListener

object SituationDataManager : AbstractReloadListener(Gson(), "notification/situation") {
    private val situations: MutableMap<ResourceLocation, MutableList<Situation>> = mutableMapOf()

    override fun apply(
        objectMap: MutableMap<ResourceLocation, JsonElement>,
        resourceManager: ResourceManager,
        profilerFiller: ProfilerFiller,
    ) {
        situations.clear()
        objectMap.entries.forEach { (id, json) ->
            val situation = Situation.CODEC.parse(JsonOps.INSTANCE, json).orThrow
            situation.id = id
            for (trigger in situation.triggers) {
                situations.getOrPut(trigger, ::mutableListOf).add(situation)
            }
        }
    }

    fun findMatches(context: BroadcastContext, trigger: ResourceLocation): Set<ResourceLocation> {
        val matches: MutableSet<ResourceLocation> = mutableSetOf()
        for (notification in situations[trigger] ?: return emptySet()) {
            if (notification.id != null && notification.matches(context) && !notification.disabled) {
                matches += notification.id!!
                matches += notification.otherSituations
            }
        }
        return matches
    }
}