package dev.laux.coins.mysql;

import java.sql.*;
import java.util.Calendar;
import java.util.UUID;

public class Database {

    private Connector mySQLConnector;

    public Database(Connector mySQLConnector) throws SQLException {
        this.mySQLConnector = mySQLConnector;
        initialize();
    }

    private void initialize() throws SQLException {
        String coins = "CREATE TABLE IF NOT EXISTS coins (UUID VARCHAR(36) NOT NULL, Coins INT NOT NULL, PRIMARY KEY (UUID))";
        try (Statement stmt = mySQLConnector.getConnection().createStatement()) {
            stmt.executeUpdate(coins);
        }
        String rewards = "CREATE TABLE IF NOT EXISTS rewards (UUID VARCHAR(36) NOT NULL, Rewarded TIMESTAMP NOT NULL, ConsecutiveDays INT NOT NULL, PRIMARY KEY (UUID))";
        try (Statement stmt = mySQLConnector.getConnection().createStatement()) {
            stmt.executeUpdate(rewards);
        }
    }

    public void updateRewards(UUID uuid, Timestamp timestamp) throws SQLException {
        if (getRewards(uuid) == null) {
            String sql = "INSERT INTO rewards (UUID, Rewarded, ConsecutiveDays) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE Rewarded = ?, ConsecutiveDays = ?";
            try (PreparedStatement stmt = mySQLConnector.getConnection().prepareStatement(sql)) {
                stmt.setString(1, uuid.toString());
                stmt.setTimestamp(2, timestamp);
                stmt.setInt(3, 0);
                stmt.setTimestamp(4, timestamp);
                stmt.setInt(5, 0);
                stmt.executeUpdate();
            }
        } else {
            String sql = "UPDATE rewards SET Rewarded = ? WHERE UUID = ?";
            try (PreparedStatement stmt = mySQLConnector.getConnection().prepareStatement(sql)) {
                stmt.setTimestamp(1, timestamp);
                stmt.setString(2, uuid.toString());
                stmt.executeUpdate();
            }
        }
    }

    public Timestamp getRewards(UUID uuid) throws SQLException {
        String sql = "SELECT Rewarded FROM rewards WHERE UUID = ?";
        try (PreparedStatement stmt = mySQLConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getTimestamp("Rewarded");
            }
        }
        return null;
    }

    public int getConsecutiveDays(UUID uuid) throws SQLException {
        String sql = "SELECT ConsecutiveDays FROM rewards WHERE UUID = ?";
        try (PreparedStatement stmt = mySQLConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ConsecutiveDays");
            }
        }
        return 0;
    }

    public void updateConsecutiveDays(UUID uuid) throws SQLException {
        String sql = "UPDATE rewards SET ConsecutiveDays = ? WHERE UUID = ?";
        try (PreparedStatement stmt = mySQLConnector.getConnection().prepareStatement(sql)) {
            // Increase ConsecutiveDays
            if (getConsecutiveDays(uuid) >= 3) {
                stmt.setInt(1, 1);
                stmt.setString(2, uuid.toString());
                stmt.executeUpdate();
                return;
            }
            stmt.setInt(1, getConsecutiveDays(uuid) + 1);
            stmt.setString(2, uuid.toString());
            stmt.executeUpdate();

        }
    }

    public void resetConsecutiveDays(UUID uuid) throws SQLException {
        String sql = "UPDATE rewards SET ConsecutiveDays = ? WHERE UUID = ?";
        try (PreparedStatement stmt = mySQLConnector.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, 1);
            stmt.setString(2, uuid.toString());
            stmt.executeUpdate();
        }
    }


    public void updateCoins(UUID uuid, int coins) throws SQLException {
        String sql = "INSERT INTO coins (UUID, Coins) VALUES (?, ?) ON DUPLICATE KEY UPDATE Coins = ?";
        try (PreparedStatement stmt = mySQLConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setInt(2, coins);
            stmt.setInt(3, coins);
            stmt.executeUpdate();
        }
    }

    public int getCoins(UUID uuid) throws SQLException {
        String sql = "SELECT Coins FROM coins WHERE UUID = ?";
        try (PreparedStatement stmt = mySQLConnector.getConnection().prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Coins");
            }
        }
        return 0; // Standardwert, falls der Spieler nicht gefunden wird
    }

}
