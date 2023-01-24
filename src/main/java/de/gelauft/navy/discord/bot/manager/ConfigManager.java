package de.gelauft.navy.discord.bot.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.gelauft.navy.discord.bot.objects.config.*;
import de.gelauft.navy.discord.bot.objects.permission.MasterRole;
import de.gelauft.navy.discord.bot.objects.permission.PermissionGroup;
import lombok.SneakyThrows;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author |Eric|#0001
 * created on 13.10.2022
 * created for Sondereinsatzbot
 */

public class ConfigManager {

    private final Gson gson;

    public ConfigManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    }

    @SneakyThrows
    public ConfigObject loadConfig() {
        File file = new File("config/", "rn-config.json");
        ConfigObject configObject;
        if (!file.exists()) {
            configObject = ConfigObject.builder().token("token").guildId(1234L)
                    .databaseConfig(DatabaseConfig.builder().host("localhost").port(3306).database("database")
                            .username("username").password("password").build()).build();

            file.getCanonicalFile().getParentFile().mkdirs();
            file.createNewFile();
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            writer.write(gson.toJson(configObject));
            writer.flush();
            writer.close();
        } else {
            configObject = gson.fromJson(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8),
                    ConfigObject.class);
        }

        return configObject;
    }

    @SneakyThrows
    public RoleConfigObject loadRoleConfig() {
        File file = new File("config/", "rn-roles.json");
        RoleConfigObject roleConfigObject;
        if (!file.exists()) {
            roleConfigObject = RoleConfigObject.builder()
                    .permissionGroups(Arrays.asList(
                            PermissionGroup.builder().groupName("owner").roleId(1234L).build(),
                            PermissionGroup.builder().groupName("admin").roleId(654654L).build()))
                    .savedRoles(Arrays.asList(
                            PermissionGroup.builder().groupName("rnMember").roleId(1234L).build(),
                            PermissionGroup.builder().groupName("fcMember").roleId(1324L).build(),
                            PermissionGroup.builder().groupName("avpMember").roleId(1234L).build()))
                    .masterRoles(Arrays.asList(
                            MasterRole.builder().roleId(1234L)
                                    .rolesToAssign(Arrays.asList(1234L, 4567L, 6789L)).giveAllRoles(false).build(),
                            MasterRole.builder().roleId(1234L)
                                    .rolesToAssign(Arrays.asList(1324L, 3456L, 6789L)).giveAllRoles(true).build()))
                    .build();

            file.getCanonicalFile().getParentFile().mkdirs();
            file.createNewFile();
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            writer.write(gson.toJson(roleConfigObject));
            writer.flush();
            writer.close();
        } else {
            roleConfigObject = gson.fromJson(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8),
                    RoleConfigObject.class);
        }

        return roleConfigObject;
    }

    @SneakyThrows
    public ChannelConfigObject loadChannelConfig() {
        File file = new File("config/", "rn-channels.json");
        ChannelConfigObject channelConfigObject;
        if (!file.exists()) {
            channelConfigObject = ChannelConfigObject.builder().configChannels(Arrays.asList(
                            ConfigChannel.builder().simplifiedName("log").channelId(1234L).build(),
                            ConfigChannel.builder().simplifiedName("lobby").channelId(1234L).build(),
                            ConfigChannel.builder().simplifiedName("conformation").channelId(1234L).build()))
                    .configCategories(Arrays.asList(
                            ConfigCategory.builder().simplifiedName("jfc").categoryId(1324L).build()
                    )).build();

            file.getCanonicalFile().getParentFile().mkdirs();
            file.createNewFile();
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            writer.write(gson.toJson(channelConfigObject));
            writer.flush();
            writer.close();
        } else {
            channelConfigObject = gson.fromJson(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8),
                    ChannelConfigObject.class);
        }

        return channelConfigObject;
    }

}
