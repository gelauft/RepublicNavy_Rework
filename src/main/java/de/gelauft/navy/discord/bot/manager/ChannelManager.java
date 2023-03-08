package de.gelauft.navy.discord.bot.manager;

import de.gelauft.navy.discord.bot.Bot;
import de.gelauft.navy.discord.bot.objects.channel.CustomChannel;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author |Eric|#0001
 * created on 08.03.2023
 * created for RepublicNavy_Rework
 */

public class ChannelManager {

    private final List<CustomChannel> customChannels;

    public ChannelManager() {
        this.customChannels = new ArrayList<>();
    }

    public CustomChannel getCustomChannelByMember(Member member) {
        return this.customChannels.stream().filter(customChannel -> customChannel.getOwner() == member).toList().get(0);
    }

    public CustomChannel getCustomChannelByChannel(VoiceChannel voiceChannel) {
        return this.customChannels.stream().filter(customChannel -> customChannel.getChannel() == voiceChannel)
                .toList().get(0);
    }

    public boolean isCustomChannel(VoiceChannel channel) {
        return this.customChannels.stream().anyMatch(customChannel -> customChannel.getChannel() == channel);
    }

    public boolean hasCustomChannel(Member member) {
        return this.customChannels.stream().anyMatch(customChannel -> customChannel.getOwner() == member);
    }

    public void openCustomChannel(Member member) {
        member.getGuild().createVoiceChannel("Talk von " + member.getEffectiveName(), member.getGuild()
                .getCategoryById(Bot.getInstance().getChannelConfig().getCategoryByName("jfc").getCategoryId()))
                .queue(voiceChannel -> {
                    voiceChannel.upsertPermissionOverride(member).setAllowed(Arrays.asList(Permission.MANAGE_CHANNEL,
                            Permission.VOICE_SPEAK, Permission.VIEW_CHANNEL)).queue();
                    member.getGuild().moveVoiceMember(member, voiceChannel).queue();
                    this.customChannels.add(new CustomChannel(member, voiceChannel));
        });
    }

    public void closeCustomChannel(VoiceChannel channel) {
        this.customChannels.remove(this.getCustomChannelByChannel(channel));
        channel.delete().queue();
    }

    public void closeAllChannels() {
        for (CustomChannel channel : this.customChannels) {
            channel.getChannel().delete().queue();
            this.customChannels.remove(channel);
        }
    }
}
