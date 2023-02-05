package de.gelauft.navy.discord.bot.listener;

import de.gelauft.navy.discord.bot.Bot;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * @author |Eric|#0001
 * created on 25.01.2023
 * created for RepublicNavy_Rework
 */

public class GuildMemberListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Bot.getInstance().getMemberManager().handleMemberJoin(event.getMember());
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        super.onGuildMemberRemove(event);
    }
}
