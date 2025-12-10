package us.timinc.mc.cobblemon.spawnnotification.config

import net.minecraft.resources.ResourceLocation

data class DisabledSegmentsPerSituations(
    val situations: List<String>,
    val segments: List<String>,
) {
    fun matches(activeSituations: List<ResourceLocation>, segment: ResourceLocation) =
        activeSituations.any { activeSituation ->
            situations.any { disabledSituation ->
                disabledSituation.replace("*", ".*").toRegex().matches(activeSituation.toString())
            }
        } && segments.any { it.replace("*", ".*").toRegex().matches(segment.toString()) }
}
