CREATE TABLE IF NOT EXISTS dropper_player_stats(
   uuid VARCHAR(40),
   username VARCHAR(40),
   best_time BIGINT DEFAULT 0,
   total_fails INT DEFAULT 0,
   total_map_completed INT DEFAULT 0,
   total_wins INT DEFAULT 0,
   total_losses INT DEFAULT 0,
   PRIMARY KEY(uuid),
   UNIQUE(username)
);
