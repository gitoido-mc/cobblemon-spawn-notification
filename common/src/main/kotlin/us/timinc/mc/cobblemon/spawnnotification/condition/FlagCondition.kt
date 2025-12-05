package us.timinc.mc.cobblemon.spawnnotification.condition

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext
import us.timinc.mc.cobblemon.spawnnotification.api.condition.BroadcastCondition
import us.timinc.mc.cobblemon.spawnnotification.api.condition.BroadcastConditionType

class FlagCondition(
    val flags: List<String>,
) : BroadcastCondition {
    companion object {
        val CODEC: MapCodec<FlagCondition> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.STRING.listOf().fieldOf("flags").forGetter(FlagCondition::flags)
            ).apply(instance, ::FlagCondition)
        }

        val CONDITION_TYPE = BroadcastConditionType(CODEC)
    }

    override fun getType(): BroadcastConditionType<*> = SpawnNotification.ConditionTypes.FLAG

    override fun matches(context: BroadcastContext): Boolean =
        !flags.any(SpawnNotification.config.disabledFlags::contains)
}