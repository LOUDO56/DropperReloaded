# DropperReloaded

DropperReloaded is a plugin that recreates the famous minigame "dropper". It's compatible from 1.8.8 to 1.21.4.

# Extension
- [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/): To make title and action bar working for every version.
- [Citizens2](https://www.spigotmc.org/resources/citizens.13811/): To be able to create a join game NPC.

# Features
- Configurable
- Easy to setup
- Database for player stats
- Auto matchmaking (no need to create arenas)
- NPC Game Join (Citizens2 required)

# Commands
### /dropperadmin (/dropadm)

#### Map manager
- **`map create [name]`** : Create a map. (Use "_" to add a space)
- **`map delete [name]`** : Delete a map.
- **`map rename [old_name] [new_name]`** : Rename your map.
- **`map setdifficulty [name] {easy, medium, hard}`** : Set the difficulty of the map to easy, medium, or hard.
- **`map addspawn [name]`** : Add a spawn to your map.
- **`map remlastspawn [name]`** : Remove the last added spawn of your map.
- **`map wand`** : Get a stick to set the doors.
- **`map setdoors [name]`** : Set the doors of the map.
- **`map enable [name]`** : Make your map playable.
- **`map disable [name]`** : Make your map closed for players.
- **`map list`** : List your map.

#### Wait Lobby
- **`waitlobby setmaxplayer [number]`** : Set the maximum number of players for each game.
- **`waitlobby setminplayer [number]`** : Set the minimum number of players to start a game.
- **`waitlobby setspawn`** : Set the location of the wait lobby.

#### Main Lobby
- **`mainlobby setnpc`** : Create an NPC that lets you join a game. *(Available if Citizens is installed.)*
- **`mainlobby delnpc`** : Remove the join game NPC.
- **`mainlobby setspawn`** : Set the spawn of the Main Lobby.
  
#### Other
- **`reload`** : Reload the plugin configuration.

### /dropper (/drop)

- **`play`** : Join a game.
- **`leave`** : Leave your current game.
- **`stats`** : Show your stats.

