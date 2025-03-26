# DropperReloaded

DropperReloaded is a plugin that recreates the famous mini-game "dropper". It's compatible from 1.13 to 1.21.4.

# Extension (Optional)
- [Citizens2](https://www.spigotmc.org/resources/citizens.13811/): To be able to create a join game NPC.


  ![2025-03-26_14 06 54](https://github.com/user-attachments/assets/77889e1c-7503-4fd2-9ad8-5d1a295fa7ef)


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
- **`map tp [name]`** : Teleport to a map.
- **`map remlastspawn [name]`** : Remove the last added spawn of your map.
- **`map wand`** : Get a stick to set the doors.
- **`map setdoors [name]`** : Set the doors of the map.
- **`map enable [name]`** : Make your map playable.
- **`map disable [name]`** : Make your map closed for players.
- **`map list`** : List your maps.

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

# Permission

**`dropper-reloaded.admin`** : Permission to use "/dropperadmin"

