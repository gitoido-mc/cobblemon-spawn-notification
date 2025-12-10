package us.timinc.mc.cobblemon.spawnnotification.condition

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext
import us.timinc.mc.cobblemon.spawnnotification.api.condition.BroadcastCondition
import us.timinc.mc.cobblemon.spawnnotification.api.condition.BroadcastConditionType
import us.timinc.mc.cobblemon.timcore.PokemonMatcher

class MatcherCondition(
    val matcher: List<PokemonMatcher>,
    val antiMatcher: List<PokemonMatcher>,
) : BroadcastCondition {
    companion object {
        val CODEC: MapCodec<MatcherCondition> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                PokemonMatcher.STRING_CODEC.listOf().optionalFieldOf("matcher", emptyList())
                    .forGetter(MatcherCondition::matcher),
                PokemonMatcher.STRING_CODEC.listOf().optionalFieldOf("antiMatcher", emptyList())
                    .forGetter(MatcherCondition::antiMatcher),
            ).apply(instance, ::MatcherCondition)
        }

        val CONDITION_TYPE = BroadcastConditionType(CODEC)
    }

    override fun getType(): BroadcastConditionType<*> = SpawnNotification.ConditionTypes.MATCHER

    override fun matches(context: BroadcastContext): Boolean =
        matcher.any { it.matches(context.pokemon) } && !antiMatcher.any { it.matches(context.pokemon) }
}