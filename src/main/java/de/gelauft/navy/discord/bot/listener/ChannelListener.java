package de.gelauft.navy.discord.bot.listener;

import de.gelauft.navy.discord.bot.Bot;
import de.gelauft.navy.discord.bot.manager.ChannelManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

/**
 * @author |Eric|#0001
 * created on 08.03.2023
 * created for RepublicNavy_Rework
 */

public class ChannelListener extends ListenerAdapter {

    private final ChannelManager channelManager;

    public ChannelListener() {
        this.channelManager = Bot.getInstance().getChannelManager();
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (event.getChannelJoined() != null) {
            if (event.getChannelJoined().getType() == ChannelType.STAGE) return;

            if (event.getChannelJoined().getIdLong() == Bot.getInstance().getChannelConfig().getChannelByName("jfc")
                    .getChannelId()) {
                if (this.channelManager.hasCustomChannel(event.getMember())) {
                    event.getGuild().moveVoiceMember(event.getMember(), this.channelManager
                            .getCustomChannelByMember(event.getMember()).getChannel()).queue();
                    event.getMember().getUser().openPrivateChannel().queue(privateChannel -> {
                        privateChannel.sendMessageEmbeds(new EmbedBuilder()
                                .addField("Channel bereits erstellt!", "Du hast bereits einen " +
                                        "Custom Channel erstellt und wurdest dorthin verschoben.", false)
                                .setColor(Color.red)
                                .setFooter("Dies ist eine automatisierte Nachricht, bitte antworte nicht darauf.")
                                .build()).queue();
                    });
                    return;
                }

                this.channelManager.openCustomChannel(event.getMember());
                return;
            }
        }

        if (event.getChannelLeft() != null) {
            if (event.getChannelLeft().getType() == ChannelType.STAGE) return;

            if (this.channelManager.isCustomChannel(event.getChannelLeft().asVoiceChannel())) {
                if (event.getChannelLeft().getMembers().size() == 0) {
                    this.channelManager.closeCustomChannel(event.getChannelLeft().asVoiceChannel());
                    return;
                }
            }
        }
    }
}
