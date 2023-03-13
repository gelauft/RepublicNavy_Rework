package de.gelauft.aoc.discord.bot.objects.command;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

/**
 * @author |Eric|#0001
 * created on 13.10.2022
 * created for Sondereinsatzbot
 */

@Getter
public class CommandContext {

    private final SlashCommandInteractionEvent event;
    private final InteractionHook response;
    private final Guild guild;
    private final MessageChannel channel;
    private final Member author;

    public CommandContext(SlashCommandInteractionEvent event, InteractionHook response) {
        this.event = event;
        this.response = response;
        this.guild = event.getGuild();
        this.channel = event.getMessageChannel();
        this.author = event.getMember();
    }
}
