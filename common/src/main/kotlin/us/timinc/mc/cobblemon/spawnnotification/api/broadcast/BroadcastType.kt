package us.timinc.mc.cobblemon.spawnnotification.api.broadcast

import com.mojang.serialization.Codec
import com.mojang.serialization.Lifecycle
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.api.condition.BroadcastCondition

data class BroadcastType<P, C, T : Broadcast<P, C>>(
    val paramCodec: MapCodec<P>,
    val childParamCodec: MapCodec<C>,
    val factory: (ResourceLocation, P, List<Broadcast.Child<C>>, List<BroadcastCondition>) -> T,
) {
    val codec: MapCodec<T> = RecordCodecBuilder.mapCodec { instance ->
        instance.group(
            ResourceLocation.CODEC.fieldOf("destination").forGetter { it.destination },
            paramCodec.codec().fieldOf("params").forGetter { it.params },
            childCodec().listOf().optionalFieldOf("children", emptyList()).forGetter { it.children },
            BroadcastCondition.CODEC.listOf().optionalFieldOf("conditions", emptyList()).forGetter { it.conditions }
        ).apply(instance, factory)
    }

    private fun childCodec(): Codec<Broadcast.Child<C>> = Codec.recursive("broadcast_child") { codec ->
        RecordCodecBuilder.create { childInstance ->
            childInstance.group(
                BroadcastCondition.CODEC.listOf().fieldOf("conditions").forGetter(Broadcast.Child<C>::conditions),
                childParamCodec.codec().fieldOf("params").forGetter(Broadcast.Child<C>::params),
                codec.listOf().optionalFieldOf("children", emptyList()).forGetter { it.children }
            ).apply(childInstance, Broadcast<*, C>::Child)
        }
    }

    companion object {
        val REGISTRY: Registry<BroadcastType<*, *, *>> = MappedRegistry(
            ResourceKey.createRegistryKey(SpawnNotification.modResource("broadcast_types")),
            Lifecycle.stable()
        )
    }
}