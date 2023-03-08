package de.gelauft.navy.discord.bot.objects.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

/**
 * @author |Eric|#0001
 * created on 08.03.2023
 * created for RepublicNavy_Rework
 */

@AllArgsConstructor
@Getter
public class CustomChannel {

    private final Member owner;
    private final VoiceChannel channel;

}
