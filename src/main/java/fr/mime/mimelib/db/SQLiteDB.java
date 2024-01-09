package fr.mime.mimelib.db;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public abstract class SQLiteDB {
    private final HikariDataSource dataSource;
    private final Plugin plugin;

    public SQLiteDB(Plugin plugin, String fileName) {
        this.plugin = plugin;
        try {
            final File dbFile = new File(plugin.getDataFolder().getParentFile(), fileName);

            if (!dbFile.exists()) {
                if (dbFile.createNewFile()) {
                    plugin.getLogger().info("Created database file");
                } else {
                    plugin.getLogger().severe("Could not create database file");
                }
            }

        } catch (IOException e) {
            plugin.getLogger().severe("Exception while creating database file");
        }
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:sqlite:" + new File(plugin.getDataFolder().getParentFile(), fileName).getAbsolutePath());
        dataSource.setConnectionTestQuery("SELECT 1");
        dataSource.addDataSourceProperty("cachePrepStmts", "true");
        dataSource.addDataSourceProperty("prepStmtCacheSize", "250");
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        this.dataSource = dataSource;

        initDatabase();
    }

    public void initDatabase() {
        throw new UnsupportedOperationException("initDatabase() not implemented");
    }

    public void close() {
        dataSource.close();
    }

    public void execute(String query) {
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(query);
        } catch (Exception e) {
            plugin.getLogger().severe("Unable to execute query: "+e.getMessage());
        }
    }

    public void execute(String query, Object... args) {
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(query)) {
            for (int i = 0; i < args.length; i++) {
                statement.setObject(i+1, args[i]);
            }
            statement.execute();
        } catch (Exception e) {
            plugin.getLogger().severe("Unable to execute query: "+e.getMessage());
        }
    }
    public HikariDataSource getDataSource() {
        return dataSource;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
