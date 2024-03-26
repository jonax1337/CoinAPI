package dev.laux.coins.api;

import dev.laux.coins.mysql.Database;

import java.sql.SQLException;
import java.util.UUID;

public class CoinAPI {

    private final Database database;

    public CoinAPI(Database database) {
        this.database = database;
    }

    public void addCoins(UUID uuid, int amount) throws SQLException {
        int currentCoins = database.getCoins(uuid);
        database.updateCoins(uuid, currentCoins + amount);
    }

    public void removeCoins(UUID uuid, int amount) throws SQLException {
        int currentCoins = database.getCoins(uuid);
        int newAmount = Math.max(currentCoins - amount, 0); // Verhindere negative Werte
        database.updateCoins(uuid, newAmount);
    }

    public int getCoins(UUID uuid) throws SQLException {
        return database.getCoins(uuid);
    }

    public void ensurePlayer(UUID uuid) throws SQLException {
        if (database.getCoins(uuid) == 0) {
            database.updateCoins(uuid, 1000); // Legt den Spieler mit 0 Coins an, falls noch nicht vorhanden
        }
    }

}
