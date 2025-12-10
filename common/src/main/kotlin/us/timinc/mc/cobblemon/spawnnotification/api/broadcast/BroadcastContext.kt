package us.timinc.mc.cobblemon.spawnnotification.api.broadcast

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.phys.Vec3
import us.timinc.mc.cobblemon.timcore.getIdentifier
import java.util.*

open class BroadcastContext(
    val pokemon: Pokemon,
    val world: ServerLevel,
    val position: Vec3,
    val player: ServerPlayer? = null,
    val id: UUID = UUID.randomUUID(),
) {
    class WithSituations(
        pokemon: Pokemon,
        world: ServerLevel,
        position: Vec3,
        player: ServerPlayer?,
        id: UUID,
        val situations: List<ResourceLocation>,
    ) :
        BroadcastContext(pokemon, world, position, player, id)

    override fun toString(): String {
        return "BroadcastContext(pokemon=${pokemon.getIdentifier()}, world=${world.dimension()}, position=${position}, player=${player})"
    }

    fun withSituations(situations: List<ResourceLocation>): WithSituations = WithSituations(
        pokemon, world, position, player, id, situations
    )
}