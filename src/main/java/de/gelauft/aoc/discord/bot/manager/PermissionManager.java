package de.gelauft.aoc.discord.bot.manager;

import de.gelauft.aoc.discord.bot.objects.permission.PermissionGroup;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;

/**
 * @author |Eric|#0001
 * created on 13.10.2022
 * created for Sondereinsatzbot
 */

public class PermissionManager {

    private final JDA jda;
    private final List<PermissionGroup> permissionGroups;

    public PermissionManager(JDA jda) {
        this.jda = jda;
        permissionGroups = new ArrayList<>();
    }

    public List<PermissionGroup> getPermissionGroups() {
        return permissionGroups;
    }

    public PermissionGroup getPermissionGroupByName(String name) {
        for (PermissionGroup permissionGroup : permissionGroups){
            if (permissionGroup.getGroupName().matches(name)) return permissionGroup;
        }

        throw new IllegalArgumentException("Es gibt keine Permission-Group mit diesem Namen");
    }

    public void addPermissionGroup (PermissionGroup permissionGroup) {
        System.out.println("[AoC Discord Bot] Permission-Group " + permissionGroup.getGroupName() + " wurde registriert.");
        permissionGroups.add(permissionGroup);
    }

    public boolean isAtLeast(Member sender, String groupName) {
        for (PermissionGroup permissionGroup : permissionGroups) {
            if (permissionGroup.getGroupName().equals(groupName)) return isAtLeast(sender, permissionGroup);
        }
        throw new IllegalArgumentException("Es gibt keine Permission-Group mit diesem Namen");
    }

    private boolean isAtLeast(Member sender, PermissionGroup permissionGroup) {
        Role role = jda.getRoleById(permissionGroup.getRoleId());

        for (Role temp : sender.getRoles()) {
            if (temp.getPosition() >= role.getPosition()) {
                return true;
            }
        }
        return false;
    }

    public boolean isExplicit(Member sender, String groupName) {
        for (PermissionGroup permissionGroup : permissionGroups) {
            if (permissionGroup.getGroupName().equals(groupName)) return isExplicit(sender, permissionGroup);
        }
        throw new IllegalArgumentException("Es gibt keine Permission-Group mit diesem Namen");
    }

    private boolean isExplicit(Member sender, PermissionGroup permissionGroup) {
        return sender.getRoles()
                .contains(jda.getRoleById(permissionGroup.getRoleId()));
    }

    public boolean isHigherOrEqual(PermissionGroup group, PermissionGroup groupToCompare) {
        Role groupRole = jda.getRoleById(group.getRoleId());
        Role groupToCompareRole = jda.getRoleById(groupToCompare.getRoleId());

        return groupRole.getPosition() >= groupToCompareRole.getPosition();
    }

    public Role getRole(String groupName) {
        for (PermissionGroup permissionGroup : permissionGroups) {
            if (permissionGroup.getGroupName().equals(groupName)) return getRole(permissionGroup);
        }
        throw new IllegalArgumentException("Es gibt keine Permission-Group mit diesem Namen");
    }

    private Role getRole(PermissionGroup permissionGroup) {
        return jda.getRoleById(permissionGroup.getRoleId());
    }
}
