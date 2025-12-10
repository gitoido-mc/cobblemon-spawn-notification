package us.timinc.mc.cobblemon.spawnnotification.api.extension

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.MutableComponent

fun MutableComponent.withPossibleStyle(formatting: ChatFormatting?): MutableComponent =
    if (formatting == null) this else this.withStyle(formatting)