# Spawn Notification

v1.7.2-2.2

[Modrinth](https://modrinth.com/mod/cobblemon-spawn-notification)

[CurseForge](https://www.curseforge.com/minecraft/mc-mods/cobblemon-spawn-notification)

[GitHub](https://github.com/timinc-cobble/cobblemon-spawn-notification)

## What if…

…the game let you know about important spawns?

## Features

- Send chat and sound notifications for given situations.
- Situations consist of triggers and filters, so you can send a chat message for a shiny Pokémon spawning, or an ultra-rare Pokémon being captured, for example.
- Has the following triggers out of the box:
    - A Pokémon being captured by a player
    - A wild Pokémon having despawned
    - A wild Pokémon having died outside of battle
    - A wild Pokémon have been defeated in battle
    - A player fishing up a Pokémon
    - A player hatching a Pokémon
    - A player resurrecting a Pokémon
    - A wild Pokémon having spawned from eating a snack
    - A wild Pokémon having spawned (includes command and other “unnatural” spawning)
- Makes use of PokemonMatcher from Tim Core to match on labels, buckets, max IVs, and more!
- Highly configurable with addons to accommodate special Pokémon from addons and mods.

## Dependencies

- [Cobblemon](https://www.notion.so/Cobblemon-22157e0d4afd80a49896c70a775a3c7f?pvs=21)
- [Cobblemon Tim Core](https://www.notion.so/Tim-Core-22057e0d4afd809b9c02e78f26805376?pvs=21)

## Testing

As a quick test, jump into a world where you have OP powers, and run `/pokespawn mewtwo` to see the legendary spawned broadcasts go off. Run `/pokespawn mewtwo shiny` to see the shiny spawned chat and louder shiny sound broadcast go off.

## Player Help

[How it works](https://www.notion.so/How-it-works-2f357e0d4afd810f9196ddbce7bbb0f1?pvs=21)

[Config Options](https://www.notion.so/Config-Options-2f357e0d4afd81458d5dfeaaabde9b80?pvs=21)

## Addon Dev Help

### Data Pack Help

[Pokémon Custom Properties](https://www.notion.so/Pok-mon-Custom-Properties-2f357e0d4afd81119abbd444d2413cb8?pvs=21)

[Making an Addon](https://www.notion.so/Making-an-Addon-2f357e0d4afd8155b302fe5e15f24984?pvs=21)

### Resource Pack Help

[Translations](https://www.notion.so/Translations-2f357e0d4afd81e4af9ac34247f096dc?pvs=21)

## Known Issues

- v1.7.1-2.2.0 was missing the wildcard logic for `disabledSituations`. v1.7.1-2.2.1 remedied this.
- ≤v1.7.1-2.2.1 would break messages with fixed message templates if the full segment list wasn’t preserved, on NeoForge. v1.7.1-2.2.2 remedied this.
- ≤v1.7.1-2.2.1 wouldn’t update whether or not a situation was disabled unless you reloaded the data, even though you can change that in the config. v1.7.1-2.2.2 remedied this.

## Roadmap

If you’d like to keep up with the work being done on the mod, please join [the Discord](https://discord.com/invite/WKAR27SdSv) and subscribe to notifications on the channel for this content. You can also keep track of the to do list available on [the mod’s main page](https://www.notion.so/Spawn-Notification-21d57e0d4afd80c89c51f03271dfc8f9?pvs=21).

## Feedback

If you have any questions or requests concerning the mod, or just want to drop by and say hi, visit us over at [the Discord](https://discord.com/invite/WKAR27SdSv)!

## Support

If I've made something you enjoyed or helped you make something, please consider [dropping a tip in the cup](https://ko-fi.com/timsminecraftmods) and mention how I helped if you'd like!