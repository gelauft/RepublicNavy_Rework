package de.gelauft.navy.discord.bot.listener;

import de.gelauft.navy.discord.bot.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

/**
 * @author |Eric|#0001
 * created on 25.01.2023
 * created for RepublicNavy_Rework
 */

public class GuildMemberListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        event.getGuild().getTextChannelById(Bot.getInstance().getChannelConfig().getChannelByName("lobby").getChannelId())
                .sendMessageEmbeds(new EmbedBuilder()
                        .addField("➡️ Discord betreten!", event.getMember().getAsMention() + " hat den " +
                                "Discord betreten", false)
                        .setColor(Color.green)
                        .build()).queue();
        Bot.getInstance().getMemberManager().handleMemberJoin(event.getMember());
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        event.getGuild().getTextChannelById(Bot.getInstance().getChannelConfig().getChannelByName("lobby").getChannelId())
                .sendMessageEmbeds(new EmbedBuilder()
                        .addField("⬅️ Discord verlassen!", event.getUser().getAsTag() + " ist nicht mehr " +
                                "Mitglied des Discords.", false)
                        .setColor(Color.red)
                        .build()).queue();

        Bot.getInstance().getPunishmentManager().checkLeave(event.getUser(), event.getGuild());
    }
}
