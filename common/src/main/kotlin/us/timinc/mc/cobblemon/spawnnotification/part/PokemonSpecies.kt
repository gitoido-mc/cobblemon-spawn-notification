package us.timinc.mc.cobblemon.spawnnotification.part

import net.minecraft.network.chat.MutableComponent
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext
import us.timinc.mc.cobblemon.spawnnotification.api.message.Part

object PokemonSpecies : Part {
    override fun compose(context: BroadcastContext): MutableComponent = context.pokemon.getDisplayName()
}