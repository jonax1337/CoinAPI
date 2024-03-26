package dev.laux.coins;

import dev.laux.coins.api.CoinAPI;
import dev.laux.coins.commands.CoinsCommand;
import dev.laux.coins.events.JoinListener;
import dev.laux.coins.mysql.Database;
import dev.laux.coins.mysql.MySQLConnector;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import java.sql.SQLException;
public final class Coins extends JavaPlugin {

    private static Coins instance;
    private MySQLConnector mySQLConnector;
    private CoinAPI coinAPI;

    @Override
    public void onEnable() {
        instance = this;
        // Get Config
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        // Register Events
        registerEvents();
        // Register Commands
        registerCommands();
        // Start MySQL Connection
        try {
            mySQLConnector = new MySQLConnector(getConfig());
            Database db = new Database(mySQLConnector);
            this.coinAPI = new CoinAPI(db);
            getLogger().info("CoinAPI wurde erfolgreich geladen!");
        } catch (Exception e) {
            getLogger().severe("Konnte keine Verbindung zur Datenbank herstellen: " + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        try {
            if (mySQLConnector != null) {
                mySQLConnector.closeConnection();
            }
        } catch (SQLException e) {
            getLogger().severe("Fehler beim Schließen der Datenbankverbindung: " + e.getMessage());
        }
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
    }

    private void registerCommands() {
        this.getCommand("coins").setExecutor(new CoinsCommand());
    }

    public CoinAPI getCoinAPI() {
        return coinAPI;
    }

    public static Coins getInstance() {
        return instance;
    }

}
