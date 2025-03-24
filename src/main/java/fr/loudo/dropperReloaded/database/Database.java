package fr.loudo.dropperReloaded.database;

import com.google.common.io.ByteStreams;
import fr.loudo.dropperReloaded.DropperReloaded;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private final DropperReloaded instance;
    private Connection connection;
    private final String type;

    public Database(DropperReloaded instance, String type) {
        this.instance = instance;
        this.type = type.toLowerCase();
    }

    public void connect() {
        try {
            if (type.equals("mysql")) {
                String host = instance.getConfig().getString("database.host");
                String port = instance.getConfig().getString("database.port");
                String database = instance.getConfig().getString("database.name");
                String username = instance.getConfig().getString("database.username");
                String password = instance.getConfig().getString("database.password");

                String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";
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

    public Connection getConnection() {
        return connection;
    }

    public void initialize() {
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
