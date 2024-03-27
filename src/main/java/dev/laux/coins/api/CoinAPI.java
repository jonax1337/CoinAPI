package dev.laux.coins.api;

import dev.laux.coins.Coins;
import dev.laux.coins.mysql.Database;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
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

    public void setCoins(UUID uuid, int amount) throws SQLException {
        database.updateCoins(uuid, amount);
    }


    public Timestamp getRewards(UUID uuid) throws SQLException {
        return database.getRewards(uuid);
    }

    public boolean isConsecutiveDay(UUID playerUUID) throws SQLException {
        Timestamp lastRewardedTimestamp = database.getRewards(playerUUID);
        // If last Rewarded is not set, return false
        if (lastRewardedTimestamp == null) {
            return false;
        }
        // Convert Timestamp to java.sql.Date
        java.sql.Date lastRewardedDate = new java.sql.Date(lastRewardedTimestamp.getTime());
        // Get today's date
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        // Get yesterday's date
        cal.add(Calendar.DATE, -1);
        java.sql.Date yesterday = new java.sql.Date(cal.getTimeInMillis());
        // Check if lastRewardedDate is yesterday
        return ((lastRewardedDate.toString().equals(yesterday.toString())));
    }

    public int getConsecutiveDays (UUID playerUUID) throws SQLException {
        return database.getConsecutiveDays(playerUUID);
    }

    public void updateRewards(UUID uuid, Timestamp timestamp) throws SQLException {
        updateConsecutiveDays(uuid);
        database.updateRewards(uuid, timestamp);
    }

    public void updateConsecutiveDays(UUID uuid) throws SQLException {
        if (isConsecutiveDay(uuid)) {
            database.updateConsecutiveDays(uuid);
        } else {
            database.resetConsecutiveDays(uuid);
        }
    }

    public void ensurePlayerCoins(UUID uuid) throws SQLException {
        if (database.getCoins(uuid) == 0) {
            database.updateCoins(uuid, 1000); // Legt den Spieler mit 0 Coins an, falls noch nicht vorhanden
        }
    }

    public void ensurePlayerRewards(UUID uuid) throws SQLException {
        if (database.getRewards(uuid) == null) {
            database.updateRewards(uuid, new Timestamp(System.currentTimeMillis()));
        }
    }

}
