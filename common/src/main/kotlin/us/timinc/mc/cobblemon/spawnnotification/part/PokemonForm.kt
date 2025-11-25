package us.timinc.mc.cobblemon.spawnnotification.part

import net.minecraft.network.chat.Component
import us.timinc.mc.cobblemon.spawnnotification.SpawnNotification
import us.timinc.mc.cobblemon.spawnnotification.context.BroadcastContext

object PokemonForm : Part() {
    override fun getPart(ctx: BroadcastContext): Component = SpawnNotification.COMPONENTS.form(ctx.pokemon.form)
}