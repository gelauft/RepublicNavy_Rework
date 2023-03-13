package de.gelauft.aoc.discord.bot.objects.permission;

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
public class PermissionGroup {

    private String groupName;
    private long roleId;

}
