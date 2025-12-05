package us.timinc.mc.cobblemon.spawnnotification

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.pokemon.FormData
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.Broadcast
import us.timinc.mc.cobblemon.spawnnotification.api.broadcast.BroadcastType
import us.timinc.mc.cobblemon.spawnnotification.api.condition.BroadcastCondition
import us.timinc.mc.cobblemon.spawnnotification.api.condition.BroadcastConditionType
import us.timinc.mc.cobblemon.spawnnotification.api.message.Part
import us.timinc.mc.cobblemon.spawnnotification.api.message.Segment
import us.timinc.mc.cobblemon.spawnnotification.api.message.SegmentType
import us.timinc.mc.cobblemon.spawnnotification.broadcast.ActionBarBroadcast
import us.timinc.mc.cobblemon.spawnnotification.broadcast.ChatBroadcast
import us.timinc.mc.cobblemon.spawnnotification.broadcast.SoundBroadcast
import us.timinc.mc.cobblemon.spawnnotification.condition.FlagCondition
import us.timinc.mc.cobblemon.spawnnotification.condition.MatcherCondition
import us.timinc.mc.cobblemon.spawnnotification.data.NotificationData
import us.timinc.mc.cobblemon.spawnnotification.data.SegmentData
import us.timinc.mc.cobblemon.spawnnotification.handler.*
import us.timinc.mc.cobblemon.spawnnotification.part.*
import us.timinc.mc.cobblemon.spawnnotification.segment.FullSegment
import us.timinc.mc.cobblemon.spawnnotification.segment.PartSegment
import us.timinc.mc.cobblemon.spawnnotification.segment.ReferenceSegment
import us.timinc.mc.cobblemon.timcore.AbstractConfig
import us.timinc.mc.cobblemon.timcore.AbstractMod
import us.timinc.mc.cobblemon.timcore.CustomBooleanProperty
import us.timinc.mc.cobblemon.timcore.TimCoreEvents

const val MOD_ID: String = "spawn_notification"

object SpawnNotification :
    AbstractMod<SpawnNotification.SpawnNotificationConfig>(MOD_ID, SpawnNotificationConfig::class.java) {
    class SpawnNotificationConfig : AbstractConfig() {
        val broadcastRange: Int = -1
        val playerLimit: Int = -1
        val broadcastAcrossDimensions: Boolean = false
        val coordinatePartKey: String = "spawn_notification.parts.coords"
        val disabledFlags: List<String> = listOf(
            "spawn_notification:waypoints/xaeros",
            "spawn_notification:waypoints/journeymap"
        )
        val baseColor: String = ChatFormatting.GRAY.name
        val baseColors: Map<String, String> = mapOf(
            "spawn_notification:pokemon_species" to ChatFormatting.WHITE.name,
            "spawn_notification:pokemon_form" to ChatFormatting.WHITE.name,
            "spawn_notification:player_name" to ChatFormatting.WHITE.name,
            "spawn_notification:coordinates" to ChatFormatting.WHITE.name,
            "spawn_notification:biome" to ChatFormatting.WHITE.name,
            "spawn_notification:dimension" to ChatFormatting.WHITE.name,
            "spawn_notification:bucket" to ChatFormatting.WHITE.name,
        )
    }

    object KEYS {
        object TRIGGERS {
            val SPAWNED = modResource("spawned")
            val FISHED = modResource("fished")
            val SNACKED = modResource("snacked")
            val CAPTURED = modResource("captured")
            val FAINTED = modResource("fainted")
            val DIED = modResource("died")
            val DESPAWNED = modResource("despawned")
            val RESURRECTED = modResource("resurrected")
        }

        object DESTINATIONS {
            val CHAT = modResource("chat")
            val ACTION_BAR = modResource("action_bar")
            val SOUND = modResource("sound")
        }

        object PARTS {
            val POKEMON_SPECIES = modResource("pokemon_species")
            val POKEMON_FORM = modResource("pokemon_form")
            val PLAYER_NAME = modResource("player_name")
            val COORDINATES = modResource("coordinates")
            val BIOME = modResource("biome")
            val DIMENSION = modResource("dimension")
            val BUCKET = modResource("bucket")
        }

        object CONDITIONS {
            val MATCHER = modResource("matcher")
            val FLAG = modResource("flag")
        }

        object SEGMENTS {
            val REFERENCE = modResource("reference")
            val PART = modResource("part")
            val FULL = modResource("full")
        }

        @Suppress("ClassName")
        object POKEMON_PROPERTIES {
            val SPAWN_BROADCASTED = modResource("spawn_broadcasted")
            val DESPAWN_BROADCASTED = modResource("despawn_broadcasted")
        }
    }

    @Suppress("ClassName")
    object CUSTOM_POKEMON_PROPERTIES {
        val BROADCASTED_SPAWN = CustomBooleanProperty(KEYS.POKEMON_PROPERTIES.SPAWN_BROADCASTED.toString())
        val BROADCASTED_DESPAWN = CustomBooleanProperty(KEYS.POKEMON_PROPERTIES.DESPAWN_BROADCASTED.toString())
    }

    object COMPONENTS {
        fun form(form: FormData): MutableComponent =
            Component.translatable("pokemon.form.${form.formOnlyShowdownId()}")

        fun dimension(dimension: ResourceKey<Level>): MutableComponent =
            Component.translatable("dimension.${dimension.location().toLanguageKey()}")

        fun biome(biome: Holder<net.minecraft.world.level.biome.Biome>): MutableComponent =
            Component.translatable("biome.${biome.unwrapKey().get().location().toLanguageKey()}")

        fun coordinates(blockPos: BlockPos): MutableComponent =
            Component.translatable(config.coordinatePartKey, blockPos.x, blockPos.y, blockPos.z)
    }

    object BroadcastTypes {
        val CHAT_BROADCAST = register(KEYS.DESTINATIONS.CHAT, ChatBroadcast.BROADCAST_TYPE)
        val ACTION_BAR_BROADCAST = register(KEYS.DESTINATIONS.ACTION_BAR, ActionBarBroadcast.BROADCAST_TYPE)
        val SOUND_BROADCAST = register(KEYS.DESTINATIONS.SOUND, SoundBroadcast.BROADCAST_TYPE)

        fun <P, C, T : Broadcast<P, C>> register(
            id: ResourceLocation,
            broadcastType: BroadcastType<P, C, T>,
        ): BroadcastType<P, C, T> {
            return Registry.register(BroadcastType.REGISTRY, id, broadcastType)
        }
    }

    object Parts {
        val BIOME = register(KEYS.PARTS.BIOME, Biome)
        val BUCKET = register(KEYS.PARTS.BUCKET, Bucket)
        val COORDINATES = register(KEYS.PARTS.COORDINATES, Coordinates)
        val DIMENSION = register(KEYS.PARTS.DIMENSION, Dimension)
        val PLAYER_NAME = register(KEYS.PARTS.PLAYER_NAME, PlayerName)
        val POKEMON_FORM = register(KEYS.PARTS.POKEMON_FORM, PokemonForm)
        val POKEMON_SPECIES = register(KEYS.PARTS.POKEMON_SPECIES, PokemonSpecies)

        fun <T : Part> register(
            id: ResourceLocation,
            part: T,
        ): T {
            return Registry.register(Part.REGISTRY, id, part)
        }
    }

    object ConditionTypes {
        val MATCHER = register(KEYS.CONDITIONS.MATCHER, MatcherCondition.CONDITION_TYPE)
        val FLAG = register(KEYS.CONDITIONS.FLAG, FlagCondition.CONDITION_TYPE)

        fun <T : BroadcastCondition> register(
            id: ResourceLocation,
            broadcastConditionType: BroadcastConditionType<T>,
        ): BroadcastConditionType<T> {
            return Registry.register(BroadcastConditionType.REGISTRY, id, broadcastConditionType)
        }
    }

    object SegmentTypes {
        val REFERENCE = register(KEYS.SEGMENTS.REFERENCE, ReferenceSegment.SEGMENT_TYPE)
        val PART = register(KEYS.SEGMENTS.PART, PartSegment.SEGMENT_TYPE)
        val FULL = register(KEYS.SEGMENTS.FULL, FullSegment.SEGMENT_TYPE)

        fun <T : Segment> register(
            id: ResourceLocation,
            segmentType: SegmentType<T>,
        ): SegmentType<T> {
            return Registry.register(SegmentType.REGISTRY, id, segmentType)
        }
    }

    init {
        BroadcastTypes
        Parts
        ConditionTypes
        SegmentTypes

        registerReloadListener(SegmentData)
        registerReloadListener(NotificationData.Manager)

        TimCoreEvents.POKEMON_ENTITY_DID_SPAWN.subscribe(Priority.NORMAL, SpawnTriggers::handle)
        TimCoreEvents.POKEMON_ENTITY_LOAD.subscribe(Priority.LOWEST, UnnaturalSpawnTrigger::handle)
        TimCoreEvents.POKEMON_ENTITY_UNLOAD.subscribe(Priority.NORMAL, DespawnTrigger::handle)
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.NORMAL, CaptureTrigger::handle)
        CobblemonEvents.POKEMON_FAINTED.subscribe(Priority.NORMAL, DieTrigger::handle)
        CobblemonEvents.BATTLE_FAINTED.subscribe(Priority.NORMAL, FaintTrigger::handle)
        CobblemonEvents.FOSSIL_REVIVED.subscribe(Priority.NORMAL, ResurrectTrigger::handle)
    }
}