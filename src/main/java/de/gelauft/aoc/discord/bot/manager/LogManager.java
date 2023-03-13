package de.gelauft.aoc.discord.bot.manager;

import de.gelauft.aoc.discord.bot.objects.punishment.Punishment;
import de.gelauft.aoc.discord.bot.objects.punishment.PunishmentType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;

/**
 * @author |Eric|#0001
 * created on 03.03.2023
 * created for RepublicNavy_Rework
 */

public class LogManager {

    private final TextChannel internalLogChannel;
    private final TextChannel publicLogChannel;

    public LogManager(TextChannel internalLogChannel, TextChannel publicLogChannel) {
        this.internalLogChannel = internalLogChannel;
        this.publicLogChannel = publicLogChannel;
    }

    public void kicked(Member operator, Member target, String reason, int id) {
        this.internalLogChannel.sendMessageEmbeds(new EmbedBuilder()
                .setTitle("Einheitsverweis! #" + id)
                .addField("Nutzer:", target.getAsMention(), false)
                .addField("Verwiesen durch:", operator.getAsMention(), false)
                .addField("Grund:", reason, false)
                .setColor(Color.orange)
                .build()).queue();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Aus der Einheit verwiesen!").setDescription(target.getAsMention() + " wurde von " +
                        operator.getAsMention() + " aus der Einheit verwiesen.")
                .setColor(Color.orange);

        if (reason != null) eb.addField("Grund:", reason, false);

        this.publicLogChannel.sendMessageEmbeds(eb.build()).queue();
    }

    public void blacklisted(Member operator, Member target, String reason, int durationDays, boolean permanent, boolean silent, int id) {
        EmbedBuilder logEb = new EmbedBuilder()
                .setTitle("Blacklist! #" + id)
                .addField("Nutzer:", target.getAsMention(), false)
                .addField("Gesperrt durch:", operator.getAsMention(), true)
                .addField("Grund:", reason, false)
                .setColor(Color.red);

        if (!permanent) logEb.addField("Dauer:", durationDays + " Tage", true);
        else logEb.addField("Dauer:", "Unbegrenzt", true);

        this.internalLogChannel.sendMessageEmbeds(logEb.build()).queue();

        EmbedBuilder changelogEb = new EmbedBuilder();
        changelogEb.setTitle("Für die Einheit gesperrt!").setDescription(target.getAsMention() + " wurde von " +
                        operator.getAsMention() + " aus der Einheit verwiesen und für zukünftige Beitritte gesperrt.")
                .setColor(Color.red);

        if (!silent) {
            changelogEb.addField("Grund:", reason, false);
            if (!permanent) changelogEb.addField("Dauer:", durationDays + " Tage", false);
            else changelogEb.addField("Dauer:", "Unbegrenzt", true);
        }

        this.publicLogChannel.sendMessageEmbeds(changelogEb.build()).queue();
    }

    public void striked(Member operator, Member target, String reason, int durationDays, boolean permanent, boolean silent, int id) {
        EmbedBuilder logEb = new EmbedBuilder()
                .setTitle("Strike! #" + id)
                .addField("Nutzer:", target.getAsMention(), false)
                .addField("Verwarnt durch:", operator.getAsMention(), true)
                .addField("Grund:", reason, false)
                .setColor(Color.yellow);

        if (!permanent) logEb.addField("Dauer:", durationDays + " Tage", true);
        else logEb.addField("Dauer:", "Unbegrenzt", true);

        this.internalLogChannel.sendMessageEmbeds(logEb.build()).queue();

        EmbedBuilder changelogEb = new EmbedBuilder();
        changelogEb.setTitle("Strike!").setDescription(target.getAsMention() + " wurde von " + operator.getAsMention() +
                        " für ein schriftlich verwarnt.")
                .setColor(Color.yellow);

        if (!silent) {
            changelogEb.addField("Grund:", reason, false);
            if (!permanent) changelogEb.addField("Dauer:", durationDays + " Tage", false);
            else changelogEb.addField("Dauer:", "Unbegrenzt", true);
        }

        this.publicLogChannel.sendMessageEmbeds(changelogEb.build()).queue();
    }

    public void punRevoked(User target, Member operator, PunishmentType punishmentType, String reason, int id) {
        this.internalLogChannel.sendMessageEmbeds(new EmbedBuilder()
                .setTitle("Sanktion aufgehoben! #" + id)
                .addField("Sanktion:", punishmentType.getName(), false)
                .addField("Nutzer:", target.getAsTag(), false)
                .addField("Aufgehoben durch:", operator.getAsMention(), true)
                .addField("Grund der Aufhebung:", reason, false)
                .setColor(Color.blue)
                .build()).queue();

        this.publicLogChannel.sendMessageEmbeds(new EmbedBuilder()
                .setTitle("Sanktion aufgehoben!")
                .addField("Sanktion:", punishmentType.getName(), false)
                .addField("Nutzer:", target.getAsTag(), false)
                .setColor(Color.green)
                .build()).queue();
    }

    public void punDeleted(Punishment punishment, String reason, Member operator) {
        this.internalLogChannel.sendMessageEmbeds(new EmbedBuilder()
                .setTitle("Sanktion gelöscht! #" + punishment.getId())
                .addField("Gelöscht von:", operator.getAsMention(), false)
                .addField("Betroffenes Mitglied:", operator.getJDA().getUserById(punishment.getTargetId()).getAsTag(),
                        false)
                .addField("Grund der Löschung:", reason, false)
                .setColor(Color.red)
                .build()).queue();
    }
}
