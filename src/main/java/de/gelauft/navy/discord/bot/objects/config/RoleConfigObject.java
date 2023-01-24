package de.gelauft.navy.discord.bot.objects.config;

import de.gelauft.navy.discord.bot.objects.permission.MasterRole;
import de.gelauft.navy.discord.bot.objects.permission.PermissionGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * @author |Eric|#0001
 * created on 13.10.2022
 * created for Sondereinsatzbot
 */

@AllArgsConstructor
@Builder
@Getter
public class RoleConfigObject {

    private List<PermissionGroup> permissionGroups;
    private List<PermissionGroup> savedRoles;
    private List<MasterRole> masterRoles;

    public PermissionGroup getPermissionGroupByName(String name) {
        return this.permissionGroups.stream().filter(configPermissionRole -> configPermissionRole.getGroupName()
                .equals(name)).findFirst().get();
    }

    public PermissionGroup getSavedRoleByName(String name) {
        return this.savedRoles.stream().filter(configPermissionRole -> configPermissionRole.getGroupName()
                .equals(name)).findFirst().get();
    }
}
