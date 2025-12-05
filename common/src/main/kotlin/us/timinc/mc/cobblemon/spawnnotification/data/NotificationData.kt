package us.timinc.mc.cobblemon.spawnnotification.data

import com.cobblemon.mod.common.api.PrioritizedList
import com.cobblemon.mod.common.api.Priority
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.profiling.ProfilerFiller
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.Broadcast
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext
import us.timinc.mc.cobblemon.spawnnotification.api.condition.BroadcastCondition
import us.timinc.mc.cobblemon.timcore.AbstractReloadListener

class NotificationData(
    val conditions: List<BroadcastCondition>,
    val triggers: List<ResourceLocation>,
    val broadcasts: List<Broadcast<*, *>>,
    val priority: Priority,
) {
    companion object {
        val CODEC: Codec<NotificationData> = RecordCodecBuilder.create { instance ->
            instance.group(
                BroadcastCondition.CODEC.listOf().fieldOf("conditions").forGetter(NotificationData::conditions),
                ResourceLocation.CODEC.listOf().fieldOf("triggers").forGetter(NotificationData::triggers),
                Broadcast.CODEC.listOf().optionalFieldOf("broadcasts", emptyList())
                    .forGetter(NotificationData::broadcasts),
                Priority.CODEC.optionalFieldOf("priority", Priority.NORMAL).forGetter(NotificationData::priority)
            ).apply(instance, ::NotificationData)
        }
    }

    var id: ResourceLocation? = null

    fun matches(context: BroadcastContext) = conditions.all { it.matches(context) }

    object Manager : AbstractReloadListener(Gson(), "notification") {
        private val notifications: MutableMap<ResourceLocation, PrioritizedList<NotificationData>> = mutableMapOf()

        override fun apply(
            objectMap: MutableMap<ResourceLocation, JsonElement>,
            resourceManager: ResourceManager,
            profilerFiller: ProfilerFiller,
        ) {
            notifications.clear()
            objectMap.entries.forEach { (id, json) ->
                val notification = CODEC.parse(JsonOps.INSTANCE, json).orThrow
                notification.id = id
                for (trigger in notification.triggers) {
                    notifications.getOrPut(trigger, ::PrioritizedList).add(notification.priority, notification)
                }
            }
        }

        fun findMatches(context: BroadcastContext, trigger: ResourceLocation) =
            notifications[trigger]?.find { it.matches(context) }
    }
}
