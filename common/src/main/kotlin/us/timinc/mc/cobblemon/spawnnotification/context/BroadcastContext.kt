package us.timinc.mc.cobblemon.spawnnotification.context

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.phys.Vec3
import us.timinc.mc.cobblemon.timcore.getIdentifier
import java.util.UUID

data class BroadcastContext(
    val pokemon: Pokemon,
    val world: ServerLevel,
    val position: Vec3,
    val player: ServerPlayer? = null,
    val id: UUID = UUID.randomUUID()
) {
    override fun toString(): String {
        return "BroadcastContext(pokemon=${pokemon.getIdentifier()}, world=${world.dimension()}, position=${position}, player=${player})"
    }
}
