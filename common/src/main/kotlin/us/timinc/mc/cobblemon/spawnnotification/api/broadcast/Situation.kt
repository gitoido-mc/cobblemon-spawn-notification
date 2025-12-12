package us.timinc.mc.cobblemon.spawnnotification.api.broadcast

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.api.condition.BroadcastCondition

class Situation(
    val conditions: List<BroadcastCondition>,
    val triggers: List<ResourceLocation>,
    val otherSituations: List<ResourceLocation>,
) {
    companion object {
        val CODEC: Codec<Situation> = RecordCodecBuilder.create { instance ->
            instance.group(
                BroadcastCondition.CODEC.listOf().fieldOf("conditions").forGetter(Situation::conditions),
                ResourceLocation.CODEC.listOf().fieldOf("triggers").forGetter(Situation::triggers),
                ResourceLocation.CODEC.listOf().optionalFieldOf("otherSituations", emptyList())
                    .forGetter(Situation::otherSituations)
            ).apply(instance, ::Situation)
        }
    }

    var id: ResourceLocation? = null

    fun matches(context: BroadcastContext) = conditions.all { it.matches(context) }

    val disabled: Boolean by lazy {
        id?.let {
            SpawnNotification.config.disabledSituations.any { disabledSituation ->
                disabledSituation.replace(
                    "*",
                    ".*"
                ).toRegex().matches(it.toString())
            }
        } ?: false
    }
}