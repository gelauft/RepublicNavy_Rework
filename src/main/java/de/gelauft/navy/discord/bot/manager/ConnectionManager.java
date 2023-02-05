package de.gelauft.navy.discord.bot.manager;


import de.gelauft.navy.discord.bot.Bot;
import de.gelauft.navy.discord.bot.objects.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author |Eric|#0001
 * created on 13.10.2022
 * created for Sondereinsatzbot
 */

public class ConnectionManager {

    private Connection connection;

    public void connect(DatabaseConfig databaseConfig) {
        try {
            Properties properties = new Properties();
            properties.setProperty("user", databaseConfig.getUsername());
            properties.setProperty("password", databaseConfig.getPassword());
            properties.setProperty("useSSL", "false");
            properties.setProperty("autoReconnect", "true");
            connection = DriverManager.getConnection("jdbc:mysql://" + databaseConfig.getHost() + ":" +
                    databaseConfig.getPort() + "/" + databaseConfig.getDatabase(), properties);
            System.out.println("[MySQL] Verbindung hergestellt.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("[MySQL] Verbindung konnte nicht hergestellt werden.");
            Bot.getJda().shutdown();
            System.exit(0);
        }
    }

    public Connection getConnection() {
        this.checkConnection();
        return this.connection;
    }

    public void checkConnection() {
        try {
            if (this.connection.isClosed() || !this.connection.isValid(100000))
                this.connect(Bot.getInstance().getConfig().getDatabaseConfig());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (this.connection != null) {
                this.connection.close();
                System.out.println("[MySQL] Verbindung wurde getrennt.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void startTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkConnection();
            }
        }, 0, 1000 * 60 * 30);
    }
}

