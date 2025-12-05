package us.timinc.mc.cobblemon.spawnnotification.api.broadcast

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import net.minecraft.resources.ResourceLocation
import us.timinc.mc.cobblemon.spawnnotification.api.condition.BroadcastCondition

interface Broadcast<P, C> {
    companion object {
        val CODEC: Codec<Broadcast<*, *>> =
            BroadcastType.REGISTRY.byNameCodec()
                .dispatch("destination", Broadcast<*, *>::getType) {
                    @Suppress("UNCHECKED_CAST")
                    it.codec as MapCodec<Broadcast<*, *>>
                }
    }

    class Child<P>(
        val conditions: List<BroadcastCondition>,
        val params: P,
        val children: List<Child<P>> = emptyList(),
    ) {
        fun matches(context: BroadcastContext): Boolean = conditions.all { it.matches(context) }
    }

    fun getType(): BroadcastType<*, *, *>
    fun broadcast(broadcastContext: BroadcastContext, params: P)
    fun mergeChild(params: P, childParams: C): P
    fun resolvedParams(context: BroadcastContext): P = resolveParamsForChildren(params, children, context)
    fun resolveParamsForChildren(base: P, candidates: List<Child<C>>, context: BroadcastContext): P {
        var resolved = base
        for (child in candidates) {
            if (child.matches(context)) {
                resolved = mergeChild(resolved, child.params)
                resolved = resolveParamsForChildren(resolved, child.children, context)
            }
        }
        return resolved
    }

    fun deliver(broadcastContext: BroadcastContext) {
        broadcast(broadcastContext, resolvedParams(broadcastContext))
    }

    val destination: ResourceLocation
    val params: P
    val children: List<Child<C>>
    val conditions: List<BroadcastCondition>
}