package us.timinc.mc.cobblemon.spawnnotification.api.message

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext

/**
 * Something that can be composed out to a Component, ready for use as a translatable component's substitutions.
 */
interface Segment {
    companion object {
        val PURE_CODEC: Codec<Segment> = SegmentType.REGISTRY.byNameCodec().dispatch(
            "type", Segment::getType
        ) {
            @Suppress("UNCHECKED_CAST") it.codec as MapCodec<Segment>
        }

        val CODEC: Codec<Either<ResourceLocation, Segment>> = Codec.either(
            ResourceLocation.CODEC, PURE_CODEC
        )
    }

    var id: ResourceLocation?
    val fallback: String

    fun getType(): SegmentType<*>

    /**
     * Evaluates out a translatable component from this segment and the context.
     */
    fun compose(context: BroadcastContext.WithSituations): Component?

    fun validateAndCompose(context: BroadcastContext.WithSituations): Component? {
        id?.let { id ->
            if (SpawnNotification.config.disabledSegments.any {
                    it.replace("*", ".*").toRegex().matches(id.toString())
                }) return null
            if (SpawnNotification.config.disabledSegmentsBySituation.any {
                    it.matches(
                        context.situations,
                        id
                    )
                }) return null
        }
        return compose(context)
    }

    /**
     * Convenience function to get the fallback value, including an empty one if necessary.
     */
    fun getFallback(): MutableComponent? = if (fallback.isEmpty()) null else Component.translatable(fallback)
}