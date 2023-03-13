package de.gelauft.aoc.discord.bot.manager;

import de.gelauft.aoc.discord.bot.Bot;
import de.gelauft.aoc.discord.bot.objects.conformation.PendingMember;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author |Eric|#0001
 * created on 25.01.2023
 * created for RepublicNavy_Rework
 */

public class MemberManager {

    private List<PendingMember> pendingMembers;
    private final Connection connection;

    public MemberManager() {
        this.pendingMembers = new ArrayList<>();
        this.connection = Bot.getInstance().getConnectionManager().getConnection();
    }

    private void checkTable() {
        CompletableFuture.runAsync(() -> {
            try {
                this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS pending_conformations " +
                        "(`member_id` BIGINT, `conformation_id` BIGINT, `selection_id` BIGINT);").execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadFromDatabase() {
        CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM pending_conformations;");
                ps.execute();
                ResultSet rs = ps.getResultSet();
                while (rs.next()) {
                    this.pendingMembers.add(new PendingMember(rs.getLong("message_id"),
                            rs.getLong("member_id")));
                }
                rs.close();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public PendingMember getPendingMemberByMessageId(long messageId) {
        return this.pendingMembers.stream().filter(pendingMember -> pendingMember.getConformationMessageId() ==
                messageId || pendingMember.getSelectionMessageId() == messageId).toList().get(0);
    }

    public void handleMemberJoin(Member member) {
        member.getGuild().getTextChannelById(Bot.getInstance().getChannelConfig().getChannelByName("conformation")
                        .getChannelId()).sendMessageEmbeds(new EmbedBuilder()
                        .addField("➡️ Betreten!", member.getAsMention() + " hat den Discord betreten. " +
                                "Bestätigung ausstehend.", false).setColor(Color.orange)
                        .build())
                .setActionRow(Button.success("member:confirm", "Bestätigen"),
                        Button.danger("member:deny", "Ausweisen"))
                .queue(message -> {
                    PendingMember pendingMember = new PendingMember(member.getIdLong(), message.getIdLong());
                    this.pendingMembers.add(pendingMember);
                    this.insertInDatabase(pendingMember);
                });
    }

    private void insertInDatabase(PendingMember pendingMember) {
        CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO pending_conformations " +
                        "(member_id, conformation_id, selection_id) VALUES (?, ?, ?)");
                ps.setLong(1, pendingMember.getMemberId());
                ps.setLong(2, pendingMember.getConformationMessageId());
                ps.setLong(3, pendingMember.getSelectionMessageId());
                ps.execute();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void handleConfirmation(Message message) {
        PendingMember pendingMember = this.getPendingMemberByMessageId(message.getIdLong());

        StringSelectMenu selectMenu = StringSelectMenu.create("member:confirm:select").setPlaceholder("Wähle die" +
                        " Untereinheit aus").setRequiredRange(1, 1)
                .addOption("Flottencrew", "member:confirm:select:fc")
                .addOption("Amored Vehicle Platoon", "member:confirm:select:avp").build();
        message.getChannel().sendMessage("").setActionRow(selectMenu).queue(m -> {
            pendingMember.setSelectionMessageId(m.getIdLong());
            this.insertInDatabase(pendingMember);
        });
    }

    public void handleDenial(Message message) {
        PendingMember pendingMember = this.getPendingMemberByMessageId(message.getIdLong());

        User user = Bot.getJda().getUserById(pendingMember.getMemberId());

        message.getGuild().kick(user).queue();

        message.getChannel().retrieveMessageById(pendingMember.getConformationMessageId()).queue(msg -> {
            msg.editMessageEmbeds(new EmbedBuilder()
                    .addField("➡️ Betreten!", user.getAsTag() + " hat den Discord betreten. " +
                            "Ausweisung erteilt.", false).setColor(Color.red)
                    .build()).setActionRow(Collections.emptyList()).queue();
        });
    }

    public void handleSelection(Message message, Member member, String[] interactionId) {
        PendingMember pendingMember = this.getPendingMemberByMessageId(message.getIdLong());
        Guild guild = member.getGuild();

        guild.addRoleToMember(member,
                        guild.getRoleById(Bot.getInstance().getRoleConfig().getSavedRoleByName("rnMember").getRoleId()))
                .queue();

        guild.addRoleToMember(member,
                        guild.getRoleById(Bot.getInstance().getRoleConfig()
                                .getSavedRoleByName(interactionId[3].equals("fc") ? "fcMember" : "avpMember").getRoleId()))
                .queue();

        message.getChannel().retrieveMessageById(pendingMember.getSelectionMessageId()).queue(msg -> {
            msg.delete().queue();
        });
        message.getChannel().retrieveMessageById(pendingMember.getConformationMessageId()).queue(msg -> {
            msg.editMessageEmbeds(new EmbedBuilder()
                    .addField("➡️ Betreten!", member.getAsMention() + " hat den Discord betreten. " +
                            "Bestätigung erteilt.", false).setColor(Color.green)
                    .build()).setActionRow(Collections.emptyList()).queue();
        });

        pendingMembers.remove(pendingMember);
        this.removeFromDatabase(pendingMember);
    }

    private void removeFromDatabase(PendingMember pendingMember) {
        CompletableFuture.runAsync(() -> {
            try {
                PreparedStatement ps = connection.prepareStatement("DELETE FROM pending_conformations " +
                        "WHERE member_id = ?;");
                ps.setLong(1, pendingMember.getMemberId());
                ps.execute();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
