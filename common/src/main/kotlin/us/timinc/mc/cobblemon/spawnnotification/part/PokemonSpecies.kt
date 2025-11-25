package us.timinc.mc.cobblemon.spawnnotification.part

import us.timinc.mc.cobblemon.spawnnotification.context.BroadcastContext

object PokemonSpecies : Part() {
    override fun getPart(ctx: BroadcastContext) =
        ctx.pokemon.getDisplayName()
}