package us.timinc.mc.cobblemon.spawnnotification.data

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.profiling.ProfilerFiller
import us.timinc.mc.cobblemon.spawnnotification.api.extension.getHomogenized
import us.timinc.mc.cobblemon.spawnnotification.api.message.Segment
import us.timinc.mc.cobblemon.spawnnotification.segment.ReferenceSegment
import us.timinc.mc.cobblemon.timcore.AbstractReloadListener

object SegmentData : AbstractReloadListener(Gson(), "segment") {
    private val segments: MutableMap<ResourceLocation, Segment> = mutableMapOf()

    override fun apply(
        objectMap: MutableMap<ResourceLocation, JsonElement>,
        resourceManager: ResourceManager,
        profilerFiller: ProfilerFiller,
    ) {
        segments.clear()
        objectMap.entries.forEach { (id, json) ->
            val segment = Segment.CODEC.parse(JsonOps.INSTANCE, json).orThrow
            segments[id] = segment.mapLeft(::ReferenceSegment).getHomogenized()
        }
    }

    fun find(id: ResourceLocation): Segment? = segments[id]
}
