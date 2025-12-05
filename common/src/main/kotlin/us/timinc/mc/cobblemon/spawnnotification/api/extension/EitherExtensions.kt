package us.timinc.mc.cobblemon.spawnnotification.api.extension

import com.mojang.datafixers.util.Either

fun <O, L : O, R : O> Either<L, R>.getHomogenized(): O {
    var homogenized: O? = null

    ifLeft { homogenized = it }
    ifRight { homogenized = it }

    return homogenized ?: throw Exception("Neither left nor right was available.")
}