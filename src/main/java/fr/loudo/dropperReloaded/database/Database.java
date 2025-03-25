package fr.loudo.dropperReloaded.database;

import com.google.common.io.ByteStreams;
import fr.loudo.dropperReloaded.DropperReloaded;
import fr.loudo.dropperReloaded.stats.DropperStats;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.InputStream;
import java.sql.*;

public class Database {

    private final DropperReloaded instance;
    private Connection connection;
    private final String type;

    public Database(DropperReloaded instance, String type) {
        this.instance = instance;
        this.type = type.toLowerCase();
    }

    public void connect() {
        FileConfiguration config = DropperReloaded.getInstance().getConfig();
        try {
            if (type.equals("mysql") || type.equals("postgresql")) {
                String host = config.getString("database.host");
                String port = config.getString("database.port");
                String database = config.getString("database.name");
                String username = config.getString("database.username");
                String password = config.getString("database.password");

                String url = "jdbc:" + type + "://" + host + ":" + port + "/" + database + "?useSSL=false";
                connection = DriverManager.getConnection(url, username, password);
            } else {
                initDBFile();
                String url = "jdbc:sqlite:" + instance.getDataFolder() + "/dropper_database.db";
                connection = DriverManager.getConnection(url);
            }

            instance.getLogger().info("Database connected. TYPE: " + type);
        } catch (SQLException e) {
            instance.getLogger().severe("Couldn't connect to database: " + e);
        }
    }

    public void initialize() {
        connect();
        try(Statement statement = connection.createStatement()) {
            InputStream stream = getClass().getResourceAsStream("/database/init.sql");
            String sql = new String(ByteStreams.toByteArray(stream));
            String[] queries = sql.split(";");
            for (String query : queries) {
                if (!query.trim().isEmpty()) {
                    statement.executeUpdate(query.trim() + ";");
                }
            }
            instance.getLogger().info("Database loaded successfully.");
        } catch (Exception e) {
            instance.getLogger().severe("Couldn't load database: " + e);
        }
    }

    public DropperStats getPlayerStats(Player player) {

        String sql = "SELECT * FROM dropper_player_stats WHERE uuid = ?";
        if(!playerInDatabase(player)) {
            insertPlayer(player);
        }
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return new DropperStats(
                        rs.getString("uuid"),
                        rs.getString("username"),
                        rs.getInt("best_time"),
                        rs.getInt("total_fails"),
                        rs.getInt("total_map_completed"),
                        rs.getInt("total_wins"),
                        rs.getInt("total_losses")
                );
            }
        } catch(SQLException e) {
            instance.getLogger().severe("Impossible to retrieve player " + player.getDisplayName() + " stats: " + e);
        }

        return new DropperStats(
                player.getUniqueId().toString(),
                player.getDisplayName(),
                0,
                0,
                0,
                0,
                0
        );

    }

    public void updatePlayerStats(Player player, DropperStats dropperStats) {
        if(!playerInDatabase(player)) return;

        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE dropper_player_stats\n");
        sql.append("SET username = ?,\n");
        sql.append("total_fails = ?,\n");
        sql.append("total_map_completed = ?,\n");
        sql.append("total_wins = ?,\n");
        sql.append("total_losses = ?\n");
        sql.append("WHERE uuid = ?;");

        try(PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ps.setString(1, player.getDisplayName());
            ps.setInt(2, dropperStats.getTotalFails());
            ps.setInt(3, dropperStats.getTotalMapCompleted());
            ps.setInt(4, dropperStats.getTotalWins());
            ps.setInt(5, dropperStats.getTotalLosses());
            ps.setString(6, player.getUniqueId().toString());

            ps.executeUpdate();
        } catch(SQLException e) {
            instance.getLogger().severe("Impossible to update player " + player.getDisplayName() + " stats: " + e);
        }
    }

    public void setNewBestTime(Player player, long newBestTime) {
        String sql = "UPDATE dropper_player_stats SET best_time = ? WHERE uuid = ?";

        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, newBestTime);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch(SQLException e) {
            instance.getLogger().severe("Impossible to update player " + player.getDisplayName() + " new best time: " + e);
        }
    }

    public void insertPlayer(Player player) {
        String sql = "INSERT INTO dropper_player_stats (uuid, username, best_time, total_fails, total_map_completed, total_wins, total_losses) VALUES (?, ?, 0, 0, 0, 0, 0)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, player.getDisplayName());
            ps.executeUpdate();
        } catch (SQLException e) {
            instance.getLogger().severe("Couldn't insert new player to database: " + e);
        }
    }

    public boolean playerInDatabase(Player player) {
        String sql = "SELECT * FROM dropper_player_stats WHERE uuid = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return true;
            }
        } catch(SQLException e) {
            instance.getLogger().severe("Impossible to check if " + player.getDisplayName() + " exists in database: " + e);
        }

        return false;
    }

    private void initDBFile() {
        File dbFile = new File(instance.getDataFolder(), "dropper_database.db");
        if(dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (Exception e) {
                instance.getLogger().severe("Couldn't create database file: " + e);
            }
        }
    }
}
