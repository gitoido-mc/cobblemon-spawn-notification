package us.timinc.mc.cobblemon.spawnnotification.api.extension

import com.mojang.datafixers.util.Either

fun <O, L : O, R : O> Either<L, R>.getHomogenized(): O {
    var homogenized: O? = null

    ifLeft { homogenized = it }
    ifRight { homogenized = it }

    return homogenized ?: throw Exception("Neither left nor right was available.")
}

fun <K, A, B> Map<K, Either<A, B>>.fromEitherOnRightMap(
    toRight: (left: A) -> B,
): Map<K, B> = entries.fold(mapOf<K, B>()) { acc, (k, v) ->
    acc.plus(k to v.mapLeft { toRight(it) }.getHomogenized())
}

fun <K, A, B> Map<K, Either<A, B>>.fromEitherOnLeftMap(
    toLeft: (right: B) -> A,
): Map<K, A> = entries.fold(mapOf<K, A>()) { acc, (k, v) ->
    acc.plus(k to v.mapRight { toLeft(it) }.getHomogenized())
}

fun <K, A, B> Map<K, A>.toEitherOnRightMap(): Map<K, Either<B, A>> =
    entries.fold(mapOf<K, Either<B, A>>()) { acc, (k, v) ->
        acc.plus(k to Either<A, B>.right(v))
    }

fun <K, A, B> Map<K, A>.toEitherOnLeftMap(): Map<K, Either<A, B>> =
    entries.fold(mapOf<K, Either<A, B>>()) { acc, (k, v) ->
        acc.plus(k to Either<A, B>.left(v))
    }

fun <A, B> List<Either<A, B>>.fromEitherOnRightList(
    toRight: (left: A) -> B,
): List<B> = map { entry -> entry.mapLeft { toRight(it) }.getHomogenized() }

fun <A, B> List<Either<A, B>>.fromEitherOnLeftList(
    toLeft: (right: B) -> A,
): List<A> = map { entry -> entry.mapRight { toLeft(it) }.getHomogenized() }

fun <A, B> List<A>.toEitherOnRightList(): List<Either<B, A>> =
    map { entry -> Either<B, A>.right(entry) }

fun <A, B> List<A>.toEitherOnLeftList(): List<Either<A, B>> =
    map { entry -> Either<A, B>.left(entry) }