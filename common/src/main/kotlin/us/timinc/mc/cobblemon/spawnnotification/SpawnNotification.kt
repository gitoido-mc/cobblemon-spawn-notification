package us.timinc.mc.cobblemon.spawnnotification

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.pokemon.FormData
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import us.timinc.mc.cobblemon.spawnnotification.data.AbstractBroadcastData
import us.timinc.mc.cobblemon.spawnnotification.destination.ActionBar
import us.timinc.mc.cobblemon.spawnnotification.destination.Chat
import us.timinc.mc.cobblemon.spawnnotification.destination.Destination
import us.timinc.mc.cobblemon.spawnnotification.destination.Sound
import us.timinc.mc.cobblemon.spawnnotification.handler.*
import us.timinc.mc.cobblemon.spawnnotification.part.*
import us.timinc.mc.cobblemon.spawnnotification.registry.LazyRegistry
import us.timinc.mc.cobblemon.timcore.AbstractConfig
import us.timinc.mc.cobblemon.timcore.AbstractMod
import us.timinc.mc.cobblemon.timcore.CustomBooleanProperty
import us.timinc.mc.cobblemon.timcore.TimCoreEvents

const val MOD_ID: String = "spawn_notification"

object SpawnNotification :
    AbstractMod<SpawnNotification.SpawnNotificationsConfig>(MOD_ID, SpawnNotificationsConfig::class.java) {
    class SpawnNotificationsConfig : AbstractConfig() {
        val broadcastRange: Int = -1
        val playerLimit: Int = -1
        val broadcastAcrossDimensions: Boolean = false
        val coordinatesPartKey: String = "spawn_notifications.parts.coords"
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

        @Suppress("ClassName")
        object POKEMON_PROPERTIES {
            val SPAWN_BROADCASTED = modResource("spawn_broadcasted")
            val DESPAWN_BROADCASTED = modResource("despawn_broadcasted")
        }
    }

    object REGISTRIES {
        val PARTS = LazyRegistry<Part>(modResource("parts"))
        val DESTINATIONS = LazyRegistry<Destination<out AbstractBroadcastData<*>>>(modResource("destinations"))
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
            Component.translatable(config.coordinatesPartKey, blockPos.x, blockPos.y, blockPos.z)
    }

    init {
        registerReloadListener(AbstractBroadcastData.Manager)

        REGISTRIES.PARTS.register(KEYS.PARTS.POKEMON_SPECIES, PokemonSpecies)
        REGISTRIES.PARTS.register(KEYS.PARTS.POKEMON_FORM, PokemonForm)
        REGISTRIES.PARTS.register(KEYS.PARTS.PLAYER_NAME, PlayerName)
        REGISTRIES.PARTS.register(KEYS.PARTS.COORDINATES, Coordinates)
        REGISTRIES.PARTS.register(KEYS.PARTS.BIOME, Biome)
        REGISTRIES.PARTS.register(KEYS.PARTS.DIMENSION, Dimension)
        REGISTRIES.PARTS.register(KEYS.PARTS.BUCKET, Bucket)

        REGISTRIES.DESTINATIONS.register(KEYS.DESTINATIONS.CHAT, Chat)
        REGISTRIES.DESTINATIONS.register(KEYS.DESTINATIONS.ACTION_BAR, ActionBar)
        REGISTRIES.DESTINATIONS.register(KEYS.DESTINATIONS.SOUND, Sound)

        TimCoreEvents.POKEMON_ENTITY_DID_SPAWN.subscribe(Priority.NORMAL, SpawnTriggers::handle)
        TimCoreEvents.POKEMON_ENTITY_LOAD.subscribe(Priority.LOWEST, UnnaturalSpawnTrigger::handle)
        TimCoreEvents.POKEMON_ENTITY_UNLOAD.subscribe(Priority.NORMAL, DespawnTrigger::handle)
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.NORMAL, CaptureTrigger::handle)
        CobblemonEvents.POKEMON_FAINTED.subscribe(Priority.NORMAL, DieTrigger::handle)
        CobblemonEvents.BATTLE_FAINTED.subscribe(Priority.NORMAL, FaintTrigger::handle)
        CobblemonEvents.FOSSIL_REVIVED.subscribe(Priority.NORMAL, ResurrectTrigger::handle)
    }
}