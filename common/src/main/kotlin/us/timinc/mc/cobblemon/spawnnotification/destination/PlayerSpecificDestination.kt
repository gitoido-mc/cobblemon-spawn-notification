package us.timinc.mc.cobblemon.spawnnotification.destination

import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.phys.Vec3
import us.timinc.mc.cobblemon.spawnnotification.data.AbstractBroadcastData

abstract class PlayerSpecificDestination<T : AbstractBroadcastData<T>> : Destination<T>() {
    fun getRelevantPlayers(
        world: ServerLevel,
        position: Vec3,
        broadcastRange: Int,
        playerLimit: Int,
        broadcastAcrossDimensions: Boolean,
    ): Iterable<ServerPlayer> {
        if (broadcastAcrossDimensions) return world.server.playerList.players

        val eligiblePlayers = if (broadcastRange <= 0) world.players() else world.getPlayers { player ->
            player.distanceToSqr(position) <= broadcastRange
        }

        if (playerLimit > 0) {
            eligiblePlayers.sortBy { it.distanceToSqr(position) }
            return eligiblePlayers.take(playerLimit)
        }

        return eligiblePlayers
    }
}