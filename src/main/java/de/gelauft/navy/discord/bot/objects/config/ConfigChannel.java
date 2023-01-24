package de.gelauft.navy.discord.bot.objects.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * @author |Eric|#0001
 * created on 13.10.2022
 * created for Sondereinsatzbot
 */

@AllArgsConstructor
@Builder
@Getter
public class ConfigChannel {

    private String simplifiedName;
    private long channelId;

}
