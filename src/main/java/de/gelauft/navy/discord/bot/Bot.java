package de.gelauft.navy.discord.bot;

import de.gelauft.navy.discord.bot.commands.VoteCommand;
import de.gelauft.navy.discord.bot.listener.ButtonClickListener;
import de.gelauft.navy.discord.bot.listener.GuildMemberListener;
import de.gelauft.navy.discord.bot.listener.SelectionListener;
import de.gelauft.navy.discord.bot.manager.*;
import de.gelauft.navy.discord.bot.objects.config.ChannelConfigObject;
import de.gelauft.navy.discord.bot.objects.config.ConfigObject;
import de.gelauft.navy.discord.bot.objects.config.RoleConfigObject;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author |Eric|#0001
 * created on 21.01.2023
 * created for RepublicNavy_Rework
 */

@Getter
public class Bot extends ListenerAdapter {

    private static Bot instance;
    private static JDA jda;
    private final ConfigManager configManager;
    private final ConfigObject config;
    private final RoleConfigObject roleConfig;
    private final ChannelConfigObject channelConfig;
    private final ConnectionManager connectionManager;
    private final CommandManager commandManager;
    private final PermissionManager permissionManager;
    private final MemberManager memberManager;
    private final VoteManager voteManager;
    private final PunishmentManager punishmentManager;
    private final LogManager logManager;

    public Bot() throws LoginException, InterruptedException {
        instance = this;

        this.configManager = new ConfigManager();
        this.config = this.configManager.loadConfig();
        this.roleConfig = this.configManager.loadRoleConfig();
        this.channelConfig = this.configManager.loadChannelConfig();

        JDABuilder jdaBuilder = JDABuilder.create(this.config.getToken(), GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_WEBHOOKS,
                GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_BANS, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_INVITES,
                GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.MESSAGE_CONTENT).setMemberCachePolicy(MemberCachePolicy.ALL);
        jdaBuilder.setStatus(OnlineStatus.ONLINE);
        jdaBuilder.setActivity(Activity.playing("Age of Clones"));

        jda = jdaBuilder.build();
        jda.addEventListener(this);
        jda.awaitReady();

        this.connectionManager = new ConnectionManager();
        this.connectionManager.connect(config.getDatabaseConfig());
        this.connectionManager.startTimer();

//        TESTING
        this.commandManager = new CommandManager(jda.getGuildById(config.getGuildId()));
//        LIVE
//        this.commandManager = new CommandManager(jda);
        jda.addEventListener(commandManager);
        this.registerCommands();
        this.registerListener();

        this.permissionManager = new PermissionManager(jda);
        this.registerPermissionGroups();

        //initialize managers
        this.memberManager = new MemberManager();
        this.voteManager = new VoteManager();
        this.punishmentManager = new PunishmentManager();
        Guild guild = jda.getGuildById(this.config.getGuildId());
        this.logManager = new LogManager(guild.getTextChannelById(this.channelConfig.getChannelByName("internalLog").getChannelId()),
                guild.getTextChannelById(this.channelConfig.getChannelByName("publicLog").getChannelId()));

        System.out.println("[Sondereinsatzbot] Bot has been started.");
        this.shutdown();
    }

    public static Bot getInstance() {
        return instance;
    }

    public static JDA getJda() {
        return jda;
    }

    public static void main(String[] args) {
        try {
            new Bot();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void registerCommands() {
        this.commandManager.registerCommand(new VoteCommand());
    }

    private void registerListener() {
        jda.addEventListener(new GuildMemberListener());
        jda.addEventListener(new SelectionListener());
        jda.addEventListener(new ButtonClickListener());
    }

    private void registerPermissionGroups() {
        this.roleConfig.getPermissionGroups().forEach(permissionManager::addPermissionGroup);
    }

    private void shutdown() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            String input = br.readLine();
            if (input.matches("stop")) {
                this.connectionManager.disconnect();
                jda.shutdown();
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
