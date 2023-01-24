package de.gelauft.navy.discord.bot.objects.command;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/**
 * @author |Eric|#0001
 * created on 13.10.2022
 * created for Sondereinsatzbot
 */

public interface ICommand {

    CommandData getCommandData();

    void execute(CommandContext ctx);

}
