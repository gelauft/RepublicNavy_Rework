package de.gelauft.navy.discord.bot.objects.punishment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

/**
 * @author |Eric|#0001
 * created on 07.03.2023
 * created for RepublicNavy_Rework
 */

@AllArgsConstructor
@Getter
public class ArchiveStrike {

    private Message message;
    private User target;

}
