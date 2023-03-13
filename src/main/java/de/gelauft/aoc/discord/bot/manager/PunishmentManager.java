package de.gelauft.aoc.discord.bot.manager;

import de.gelauft.aoc.discord.bot.Bot;
import de.gelauft.aoc.discord.bot.objects.punishment.Punishment;
import de.gelauft.aoc.discord.bot.objects.punishment.ArchiveStrike;
import de.gelauft.aoc.discord.bot.objects.punishment.PunishmentType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author |Eric|#0001
 * created on 18.02.2023
 * created for RepublicNavy_Rework
 */

public class PunishmentManager {

    private final Connection connection;
    private final LogManager logManager;
    private final List<Punishment> punishments;
    private final List<ArchiveStrike> archiveStrikes;

    public PunishmentManager() {
        this.connection = Bot.getInstance().getConnectionManager().getConnection();
        this.logManager = Bot.getInstance().getLogManager();
        this.punishments = new ArrayList<>();
        this.archiveStrikes = new ArrayList<>();
        this.checkTables();
        this.loadFromDatabase();
        this.checkPunishments();
    }

    private void checkTables() {
        CompletableFuture.runAsync(() -> {
            try {
                connection.prepareStatement("CREATE TABLE IF NOT EXISTS punishments (`id` INT UNIQUE AUTO_INCREMENT," +
                        " `target_id` BIGINT, `operator_id` BIGINT, `reason` TEXT, `type_id` VARCHAR(10), `permanent`" +
                        " BOOLEAN, `expiration_date` BIGINT, `repealed` BOOLEAN);").execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadFromDatabase() {
        CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM punishments;");
                ps.execute();
                ResultSet rs = ps.getResultSet();
                while (rs.next()) {
                    this.punishments.add(new Punishment(rs.getInt("id"), rs.getLong("target_id"),
                            rs.getLong("opertator_id"), rs.getString("reason"),
                            this.getPunishmentTypeById(rs.getString("type_id")),
                            rs.getBoolean("permanent"), rs.getLong("expiration_date"),
                            rs.getBoolean("repealed")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public List<Punishment> getPunishments() {
        return this.punishments;
    }

    public PunishmentType getPunishmentTypeById(String id) {
        return Arrays.stream(PunishmentType.values()).filter(punishmentType -> Objects.equals(punishmentType.getId(), id))
                .toList().get(0);
    }

    public Punishment getPunishmentById(int id) {
        return this.punishments.stream().filter(punishment -> punishment.getId() == id).toList().get(0);
    }

    public List<Punishment> getPunishmentsByMemberId(long memberId) {
        return this.punishments.stream().filter(punishment -> punishment.getTargetId() == memberId).toList();
    }

    public boolean isBlacklisted(long memberId) {
        return punishments.stream().filter(punishment -> punishment.getTargetId() == memberId).toList()
                .stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.BLACKLIST).toList()
                .stream().anyMatch(punishment -> !punishment.isRepealed());
    }

    public ArchiveStrike getArchiveStrikeByMessage(Message message) {
        return this.archiveStrikes.stream().filter(archiveStrike -> archiveStrike.getMessage() == message).toList().get(0);
    }

    private CompletableFuture<Integer> insertPunishment(Member target, Member operator, String reason, PunishmentType punishmentType,
                                                       boolean permanent, long expirationDate) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO punishments (target_id, operator_id, " +
                        "reason, type_id, permanent, expiration_date, repealed) VALUES (?, ?, ?, ?, ?, ?, ?);");
                ps.setLong(1, target.getIdLong());
                ps.setLong(2, operator.getIdLong());
                ps.setString(3, reason);
                ps.setString(4, punishmentType.getId());
                ps.setBoolean(5, permanent);
                ps.setLong(6, expirationDate);
                ps.setBoolean(7, false);
                ps.execute();
                ps.close();

                PreparedStatement statement = connection.prepareStatement("SELECT LAST_INSERT_ID() FROM punishments;");
                statement.execute();
                ResultSet rs = statement.getResultSet();
                int id = 0;

                if (rs.next()) {
                    id = rs.getInt(1);
                }

                rs.close();
                statement.close();
                return id;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    private void updatePeal(int id) {
        CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement ps = connection.prepareStatement("UPDATE punishments SET repealed = ? WHERE id = ?;");
                ps.setBoolean(1, true);
                ps.setInt(2, id);
                ps.execute();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void deletePun(int id) {
        CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement ps = connection.prepareStatement("DELETE FROM punishments WHERE id = ?;");
                ps.setInt(1, id);
                ps.execute();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void blacklist(Member target, Member operator, String reason, boolean silent, int durationDays,
                          boolean permanent, Guild guild) {
        long duration = (long) durationDays * 24 * 60 * 60 * 1000;

        target.getUser().openPrivateChannel().queue(privateChannel -> {
            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("Blacklist!")
                    .setDescription("Du wurdest aus der Sondereinsatzbrigade verwiesen und für weitere Beitritte gesperrt.")
                    .setColor(Color.red);

            if (!silent) {
                eb.addField("Grund:", reason, false)
                        .addField("Dauer:", durationDays + " Tage", true);
            }
            privateChannel.sendMessageEmbeds(eb.build()).queue();
        });

        this.insertPunishment(target, operator, reason, PunishmentType.BLACKLIST, permanent, duration)
                .thenAccept(integer -> {
                    this.punishments.add(new Punishment(integer, target.getIdLong(), operator.getIdLong(),
                            reason, PunishmentType.BLACKLIST, permanent, duration, false));
                    this.logManager.blacklisted(operator, target, reason, durationDays, permanent, silent, integer);
                });

        guild.kick(target).queue();
    }

    public void kick(Member target, Member operator, String reason, boolean silent, boolean permanent, Guild guild) {
        target.getUser().openPrivateChannel().queue(privateChannel -> {
            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("Einheitsverweis!")
                    .setDescription("Du wurdest aus der Sondereinsatzbrigade verwiesen.")
                    .setColor(Color.red);

            if (!silent) eb.addField("Grund:", reason, false);
            privateChannel.sendMessageEmbeds(eb.build()).queue();
        });

        this.insertPunishment(target, operator, reason, PunishmentType.KICK, permanent, 0L)
                .thenAccept(integer -> {
                    this.punishments.add(new Punishment(integer, target.getIdLong(), operator.getIdLong(),
                            reason, PunishmentType.KICK, permanent, 0L, false));
                    this.logManager.kicked(operator, target, reason, integer);
                });

        guild.kick(target).queue();
    }

    public void strike(Member target, Member operator, String reason, boolean silent, int durationDays, boolean permanent,
                       Guild guild) {
        long duration = (long) durationDays * 24 * 60 * 60 * 1000;

        target.getUser().openPrivateChannel().queue(privateChannel -> {
            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("Strike!")
                    .setDescription("Du hast einen Strike in der Sondereinsatzbrigade erhalten.")
                    .setColor(Color.red);

            if (!silent) {
                eb.addField("Grund:", reason, false)
                        .addField("Dauer:", durationDays + " Tage", true);
            }
            privateChannel.sendMessageEmbeds(eb.build()).queue();
        });

        this.insertPunishment(target, operator, reason, PunishmentType.STRIKE, permanent, duration)
                .thenAccept(integer -> {
                    this.punishments.add(new Punishment(integer, target.getIdLong(), operator.getIdLong(),
                            reason, PunishmentType.STRIKE, permanent, duration, false));
                    this.logManager.striked(operator, target, reason, durationDays, permanent, silent, integer);
                });
    }

    public void revokePunishment(Punishment punishment, Member operator, String reason, Guild guild) {
        this.logManager.punRevoked(Bot.getJda().getUserById(punishment.getTargetId()), operator,
                punishment.getPunishmentType(), reason, punishment.getId());

        punishments.remove(punishment);
        this.updatePeal(punishment.getId());
    }

    public void deletePun(Punishment punishment, String reason, Member operator) {
        this.punishments.remove(punishment);
        this.deletePun(punishment.getId());

        this.logManager.punDeleted(punishment, reason, operator);
    }

    public void checkLeave(User memberLeft, Guild guild) {
        guild.getTextChannelById(Bot.getInstance().getChannelConfig().getChannelByName("conformation").getChannelId())
                .sendMessageEmbeds(new EmbedBuilder()
                        .setTitle("Discord verlassen!")
                        .setDescription(memberLeft.getAsTag() + " hat den Discord verlassen. Wie soll mit seinen " +
                                "Strikes verfahren werden?")
                        .setColor(Color.orange)
                        .build()).setActionRow(
                        Button.success("member:leave:remain", "Strikes erhalten"),
                        Button.primary("member:leave:archive", "Strikes archivieren"),
                        Button.danger("member:leave:delete", "Strikes löschen")
                ).queue(message -> {
                    this.archiveStrikes.add(new ArchiveStrike(message, memberLeft));
                });
    }

    public void remainStrikes(Message message, Member operator) {
        ArchiveStrike archiveStrike = this.getArchiveStrikeByMessage(message);
        this.archiveStrikes.remove(archiveStrike);

        message.editMessageEmbeds(new EmbedBuilder()
                .setTitle("Discord verlassen!")
                .setDescription(archiveStrike.getTarget().getAsTag() + " hat den Discord verlassen. Seine Strikes " +
                        "bleiben erhalten.")
                .addField("Veranlasst durch:", operator.getAsMention(), false)
                .setColor(Color.green)
                .build()).queue();
    }

    public void archiveStrikes(Message message, Member operator) {
        ArchiveStrike archiveStrike = this.getArchiveStrikeByMessage(message);
        this.archiveStrikes.remove(archiveStrike);

        List<Punishment> punishments = this.getPunishmentsByMemberId(archiveStrike.getTarget().getIdLong());
        punishments.forEach(punishment -> {
            if (punishment.getPunishmentType() == PunishmentType.STRIKE) {
                this.revokePunishment(punishment, operator, "Kein Einheitsmitglied mehr (archiviert).", operator.getGuild());
            }
        });

        message.editMessageEmbeds(new EmbedBuilder()
                .setTitle("Discord verlassen!")
                .setDescription(archiveStrike.getTarget().getAsTag() + " hat den Discord verlassen. Seine Strikes " +
                        "wurden archiviert.")
                .addField("Veranlasst durch:", operator.getAsMention(), false)
                .setColor(Color.blue)
                .build()).queue();
    }

    public void deleteStrikes(Message message, Member operator) {
        ArchiveStrike archiveStrike = this.getArchiveStrikeByMessage(message);
        this.archiveStrikes.remove(archiveStrike);

        List<Punishment> punishments = new ArrayList<>(this.getPunishmentsByMemberId(archiveStrike.getTarget().getIdLong()));

        punishments.forEach(punishment -> {
            if (punishment.getPunishmentType() == PunishmentType.STRIKE) {
                this.punishments.remove(punishment);
                this.deletePun(punishment.getId());
            }
        });

        message.editMessageEmbeds(new EmbedBuilder()
                .setTitle("Discord verlassen!")
                .setDescription(archiveStrike.getTarget().getAsTag() + " hat den Discord verlassen. Seine Strikes " +
                        "wurden gelöscht.")
                .addField("Veranlasst durch:", operator.getAsMention(), false)
                .setColor(Color.red)
                .build()).queue();
    }

    private void checkPunishments() {
        Guild guild = Bot.getJda().getGuildById(Bot.getInstance().getConfig().getGuildId());

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                punishments.forEach(punishment -> {
                    if (!punishment.isPermanent() && !punishment.isRepealed()) {
                        if (punishment.getExpirationDate() <= System.currentTimeMillis())
                            revokePunishment(punishment, guild.getMember(Bot.getJda().getSelfUser()),
                                    "Abgelaufen", guild);
                    }
                });
            }
        }, 0, 1000 * 60);
    }
}
