package de.gelauft.aoc.discord.bot.objects.punishment;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author |Eric|#0001
 * created on 07.03.2023
 * created for RepublicNavy_Rework
 */

@AllArgsConstructor
@Getter
public enum PunishmentType {

    BLACKLIST("blacklist", "Blacklist"),
    KICK("kick", "Verweis"),
    STRIKE("strike", "Schriftliche Verwarnung");

    private final String id;
    private final String name;

}
