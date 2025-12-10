package us.timinc.mc.cobblemon.spawnnotification.data

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.profiling.ProfilerFiller
import us.timinc.mc.cobblemon.spawnnotification.api.message.SegmentAddition
import us.timinc.mc.cobblemon.timcore.AbstractReloadListener

object SegmentAdditionDataManager : AbstractReloadListener(Gson(), "notification/segment_addition") {
    private val additions: MutableMap<ResourceLocation, MutableList<SegmentAddition>> = mutableMapOf()

    override fun apply(
        objectMap: MutableMap<ResourceLocation, JsonElement>,
        resourceManager: ResourceManager,
        profilerFiller: ProfilerFiller,
    ) {
        additions.clear()
        objectMap.entries.forEach { (id, json) ->
            val addition = SegmentAddition.CODEC.parse(JsonOps.INSTANCE, json).orThrow
            addition.id = id
            val additionList = additions.getOrPut(addition.target, ::mutableListOf)
            additionList += addition
        }
    }

    fun getAdditionsFor(id: ResourceLocation): List<SegmentAddition> = additions[id] ?: emptyList()
}