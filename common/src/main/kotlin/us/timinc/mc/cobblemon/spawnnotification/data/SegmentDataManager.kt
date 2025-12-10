package us.timinc.mc.cobblemon.spawnnotification.data

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.profiling.ProfilerFiller
import us.timinc.mc.cobblemon.spawnnotification.api.message.Segment
import us.timinc.mc.cobblemon.timcore.AbstractReloadListener

object SegmentDataManager : AbstractReloadListener(Gson(), "notification/segment") {
    private val segments: MutableMap<ResourceLocation, Segment> = mutableMapOf()

    override fun apply(
        objectMap: MutableMap<ResourceLocation, JsonElement>,
        resourceManager: ResourceManager,
        profilerFiller: ProfilerFiller,
    ) {
        segments.clear()
        objectMap.entries.forEach { (id, json) ->
            val segment =
                Segment.PURE_CODEC.parse(JsonOps.INSTANCE, json).orThrow
            segment.id = id
            segments[id] = segment
        }
    }

    fun find(id: ResourceLocation): Segment? = segments[id]
}
