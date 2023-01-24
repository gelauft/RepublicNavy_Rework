package de.gelauft.navy.discord.bot.manager;

import de.gelauft.navy.discord.bot.objects.command.CommandContext;
import de.gelauft.navy.discord.bot.objects.command.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author |Eric|#0001
 * created on 13.10.2022
 * created for Sondereinsatzbot
 */

public class CommandManager extends ListenerAdapter {

    private final CommandListUpdateAction commands;
    private final List<ICommand> commandList;

    public CommandManager(JDA jda) {
        commands = jda.updateCommands();
        commandList = new ArrayList<>();
        System.out.println("[RepublicNavy_Bot] Command-Manager initialisiert.");
    }

    public CommandManager(Guild guild) {
        commands = guild.updateCommands();
        commandList = new ArrayList<>();
        System.out.println("[RepublicNavy_Bot] Command Manager initialisiert.");
    }

    public void registerCommand(ICommand command) {
        if (isRegistered(command.getCommandData().getName()))
            throw new IllegalArgumentException("Ein Befehl mit diesem Namen existiert bereits.");

        commands.addCommands(command.getCommandData()).queue();
        commandList.add(command);

        System.out.println("[RepublicNavy_Bot] " + command.getCommandData().getName() + " as Befehl registriert.");
    }

    private boolean isRegistered(String name) {
        return commandList.stream().anyMatch(command -> command.getCommandData().getName().equals(name));
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        event.replyEmbeds(new EmbedBuilder().addField("", "Anfrage wird verarbeitet...", false)
                .setColor(Color.orange).build()).queue(response -> {
            commandList.forEach(command -> {
                if (command.getCommandData().getName().matches(event.getName())) {
                    CommandContext ctx = new CommandContext(event, response);
                    command.execute(ctx);
                }
            });
        });
    }

}
