package us.timinc.mc.cobblemon.spawnnotification.part

import net.minecraft.network.chat.Component
import us.timinc.mc.cobblemon.spawnnotification.context.BroadcastContext
import us.timinc.mc.cobblemon.timcore.getBucketTranslationKey

object Bucket : Part() {
    override fun getPart(ctx: BroadcastContext): Component = ctx.pokemon.getBucketTranslationKey()
}