CREATE TABLE IF NOT EXISTS dropper_player_stats(
   uuid VARCHAR(40),
   username VARCHAR(40),
   best_time INT,
   total_fails INT,
   total_map_completed INT,
   total_wins INT,
   total_looses INT,
   PRIMARY KEY(uuid),
   UNIQUE(username)
);

CREATE TABLE IF NOT EXISTS dropper_map_best_time(
   map_name VARCHAR(150),
   best_time INT,
   uuid VARCHAR(40),
   PRIMARY KEY(map_name),
   FOREIGN KEY(uuid) REFERENCES dropper_player_stats(uuid)
);
