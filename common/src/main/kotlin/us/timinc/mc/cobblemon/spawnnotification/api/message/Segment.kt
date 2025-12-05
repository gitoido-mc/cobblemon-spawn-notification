package us.timinc.mc.cobblemon.spawnnotification.api.message

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext

// Something that can be composed out to a Component, ready for use as a translatable component's subtitutions.
interface Segment {
    companion object {
        val CODEC: Codec<Either<ResourceLocation, Segment>> = Codec.either(
            ResourceLocation.CODEC,
            SegmentType.REGISTRY.byNameCodec().dispatch(
                "type",
                Segment::getType
            ) {
                @Suppress("UNCHECKED_CAST")
                it.codec as MapCodec<Segment>
            }
        )
    }

    val fallback: String

    fun getType(): SegmentType<*>
    fun compose(context: BroadcastContext): Component
    fun getFallback(): MutableComponent =
        if (fallback.isEmpty()) Component.empty() else Component.translatable(fallback)
}