package us.timinc.mc.cobblemon.spawnnotification.part

import net.minecraft.network.chat.MutableComponent
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastContext
import us.timinc.mc.cobblemon.spawnnotification.api.message.Part
import us.timinc.mc.cobblemon.timcore.getBucketTranslationKey

object Bucket : Part {
    override fun compose(context: BroadcastContext): MutableComponent =
        context.pokemon.getBucketTranslationKey().plainCopy()
}