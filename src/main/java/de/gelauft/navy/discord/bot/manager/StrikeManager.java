package de.gelauft.navy.discord.bot.manager;

import de.gelauft.navy.discord.bot.Bot;
import de.gelauft.navy.discord.bot.objects.strike.Strike;
import net.dv8tion.jda.api.entities.Member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author |Eric|#0001
 * created on 18.02.2023
 * created for RepublicNavy_Rework
 */

public class StrikeManager {

    private final List<Strike> activeStrikes;
    private final List<Strike> oldStrikes;
    private final Connection connection;

    public StrikeManager() {
        this.activeStrikes = new ArrayList<>();
        this.oldStrikes = new ArrayList<>();
        this.connection = Bot.getInstance().getConnectionManager().getConnection();
        this.checkTables();
        this.loadFromDatabase();
    }

    private void checkTables() {
        CompletableFuture.runAsync(() -> {
            try {
                connection.prepareStatement("CREATE TABLE IF NOT EXISTS strikes (`id` INT AUTO_INCREMENT UNIQUE," +
                        " `target_id` LONG, `operator_id` LONG, `reason` TEXT, `expiration_date` LONG);").execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadFromDatabase() {
        CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM strikes;");
                ps.execute();
                ResultSet rs = ps.getResultSet();
                while (rs.next()) {
                    long expirationDate = rs.getLong("expiration_date");
                    if (expirationDate <= System.currentTimeMillis()) {
                        this.oldStrikes.add(new Strike(rs.getInt("id"), rs.getLong("target_id"),
                                rs.getLong("operator_id"), rs.getString("reason"), expirationDate,
                                true));
                    } else {
                        this.activeStrikes.add(new Strike(rs.getInt("id"), rs.getLong("target_id"),
                                rs.getLong("operator_id"), rs.getString("reason"), expirationDate,
                                false));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void handleStrike(Member operator, Member target, String reason, long duration) {

    }

}
