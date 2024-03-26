package dev.laux.coins.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class Database {

    private MySQLConnector mySQLConnector;

    public Database(MySQLConnector mySQLConnector) throws SQLException {
        this.mySQLConnector = mySQLConnector;
        initialize();
    }

    private void initialize() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS coins (UUID VARCHAR(36) NOT NULL, Coins INT NOT NULL, PRIMARY KEY (UUID))";
        try (Statement stmt = mySQLConnector.getConnection().createStatement()) {
            stmt.executeUpdate(sql);
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
