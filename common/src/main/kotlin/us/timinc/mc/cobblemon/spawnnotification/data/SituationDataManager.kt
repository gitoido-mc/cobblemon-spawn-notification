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
    private val notifications: MutableMap<ResourceLocation, MutableList<Situation>> = mutableMapOf()

    override fun apply(
        objectMap: MutableMap<ResourceLocation, JsonElement>,
        resourceManager: ResourceManager,
        profilerFiller: ProfilerFiller,
    ) {
        notifications.clear()
        objectMap.entries.forEach { (id, json) ->
            val notification = Situation.CODEC.parse(JsonOps.INSTANCE, json).orThrow
            notification.id = id
            for (trigger in notification.triggers) {
                notifications.getOrPut(trigger, ::mutableListOf).add(notification)
            }
        }
    }

    fun findMatches(context: BroadcastContext, trigger: ResourceLocation): Set<ResourceLocation> {
        val matches: MutableSet<ResourceLocation> = mutableSetOf()
        for (notification in notifications[trigger] ?: return emptySet()) {
            if (notification.id != null && !notification.disabled && notification.matches(context)) {
                matches += notification.id!!
                matches += notification.otherSituations
            }
        }
        return matches
    }
}