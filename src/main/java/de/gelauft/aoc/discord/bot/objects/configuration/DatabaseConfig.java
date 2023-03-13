package de.gelauft.aoc.discord.bot.objects.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * @author |Eric|#0001
 * created on 13.10.2022
 * created for Sondereinsatzbot
 */

@AllArgsConstructor
@Getter
@Builder
public class DatabaseConfig {

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

}
