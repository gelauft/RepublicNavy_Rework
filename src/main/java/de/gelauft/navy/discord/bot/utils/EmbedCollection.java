package de.gelauft.navy.discord.bot.utils;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

/**
 * @author |Eric|#0001
 * created on 06.02.2023
 * created for RepublicNavy_Rework
 */

public class EmbedCollection {

    public static EmbedBuilder getNoPermsEb() {
        return new EmbedBuilder().setTitle("Unzureichende Berechtigung!")
                .setDescription("Du hast nicht die notwendigen Berechtigungen, um diese Funktion zu nutzen.")
                .setColor(Color.red);
    }

    public static EmbedBuilder getNoTextChannelEb() {
        return new EmbedBuilder().setTitle("Kein Textkanal!")
                .setDescription("Diese Funktion kann nur in Textkanälen angewendet werden.")
                .setColor(Color.red);
    }

    public static EmbedBuilder getSubcommandEb() {
        return new EmbedBuilder().setTitle("Subcommand benötigt!")
                .setDescription("Du musst einen Subcommand angeben, den du nutzen willst.").setColor(Color.red);
    }

    public static EmbedBuilder getCommandNotFoundEb() {
        return new EmbedBuilder().addField("Befehl nicht gefunden!", "Der Befehl mit diesem Namen wurde nicht gefunden,",
                false).setColor(Color.red);
    }
}
