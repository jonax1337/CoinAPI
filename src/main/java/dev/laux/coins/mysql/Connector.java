package dev.laux.coins.mysql;

import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connector {
    private Connection connection;

    public Connector(FileConfiguration config) throws ClassNotFoundException, SQLException {
        // Stelle sicher, dass der JDBC-Treiber geladen ist
        Class.forName("com.mysql.cj.jdbc.Driver");

        String host = config.getString("mysql.host");
        int port = config.getInt("mysql.port");
        String database = config.getString("mysql.database");
        String username = config.getString("mysql.username");
        String password = config.getString("mysql.password");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true";
        this.connection = DriverManager.getConnection(url, username, password);
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() throws SQLException {
        if (this.connection != null) {
            this.connection.close();
        }
    }
}