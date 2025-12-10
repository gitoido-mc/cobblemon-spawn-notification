package us.timinc.mc.cobblemon.spawnnotification.api.broadcast

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import net.minecraft.resources.ResourceLocation

interface Broadcast {
    companion object {
        val CODEC: Codec<Broadcast> =
            BroadcastType.REGISTRY.byNameCodec()
                .dispatch("destination", Broadcast::getType) {
                    @Suppress("UNCHECKED_CAST")
                    it.codec as MapCodec<Broadcast>
                }
    }

    fun getType(): BroadcastType<*>
    fun deliver(broadcastContext: BroadcastContext.WithSituations)

    val destination: ResourceLocation
}