package us.timinc.mc.cobblemon.spawnnotification.data

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.profiling.ProfilerFiller
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.Broadcast
import us.timinc.mc.cobblemon.timcore.AbstractReloadListener

object BroadcastDataManager : AbstractReloadListener(Gson(), "notification/broadcast") {
    class BroadcastDataWrapper(
        val situations: List<ResourceLocation>,
        val broadcast: Broadcast,
    ) {
        companion object {
            val CODEC = RecordCodecBuilder.create { instance ->
                instance.group(
                    ResourceLocation.CODEC.listOf().fieldOf("situations")
                        .forGetter(BroadcastDataWrapper::situations),
                    Broadcast.CODEC.fieldOf("broadcast").forGetter(BroadcastDataWrapper::broadcast)
                ).apply(instance, ::BroadcastDataWrapper)
            }
        }
    }

    private val broadcasts: MutableMap<ResourceLocation, MutableList<BroadcastDataWrapper>> = mutableMapOf()

    override fun apply(
        objectMap: MutableMap<ResourceLocation, JsonElement>,
        resourceManager: ResourceManager,
        profilerFiller: ProfilerFiller,
    ) {
        broadcasts.clear()
        objectMap.entries.forEach { (_, json) ->
            val broadcast = BroadcastDataWrapper.CODEC.parse(JsonOps.INSTANCE, json).orThrow
            broadcast.situations.forEach { notification ->
                val notificationList = broadcasts.getOrPut(notification, ::mutableListOf)
                notificationList += broadcast
            }
        }
    }

    fun findAllBroadcastsForNotification(id: ResourceLocation): List<Broadcast> =
        broadcasts[id]?.map { it.broadcast } ?: emptyList()
}