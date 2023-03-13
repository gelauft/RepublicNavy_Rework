package de.gelauft.aoc.discord.bot.commands;

import de.gelauft.aoc.discord.bot.utils.EmbedCollection;
import de.gelauft.aoc.discord.bot.Bot;
import de.gelauft.aoc.discord.bot.objects.command.CommandContext;
import de.gelauft.aoc.discord.bot.objects.command.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author |Eric|#0001
 * created on 05.02.2023
 * created for RepublicNavy_Rework
 */

public class VoteCommand implements ICommand {

    @Override
    public CommandData getCommandData() {
        return new CommandDataImpl("vote", "Befehle für die Abstimmungen")
                .addSubcommands(new SubcommandData("create", "Erstelle eine Abstimmung")
                        .addOptions(new OptionData(OptionType.STRING, "title", "Titel der Abstimmung",
                                true))
                        .addOptions(new OptionData(OptionType.INTEGER, "duration", "Dauer der " +
                                "Abstimmung (in Minuten)", true))
                        .addOptions(new OptionData(OptionType.CHANNEL, "channel", "Kanal zur " +
                                "Veröffentlichung", true))
                        .addOptions(new OptionData(OptionType.BOOLEAN, "revision", "Stimmenwechsel " +
                                "erlauben", true))
                        .addOptions(new OptionData(OptionType.STRING, "option1", "Auswahlmöglichkeit 1",
                                true))
                        .addOptions(new OptionData(OptionType.STRING, "option2", "Auswahlmöglichkeit 2",
                                true))
                        .addOptions(new OptionData(OptionType.STRING, "option3", "Auswahlmöglichkeit 3",
                                false))
                        .addOptions(new OptionData(OptionType.STRING, "option4", "Auswahlmöglichkeit 4",
                                false))
                        .addOptions(new OptionData(OptionType.STRING, "option5", "Auswahlmöglichkeit 5",
                                false)))
                .addSubcommands(new SubcommandData("end", "Beende eine Abstimmung vorzeitig")
                        .addOptions(new OptionData(OptionType.STRING, "title", "Titel der Abstimmung,",
                                true)));
    }

    @Override
    public void execute(CommandContext ctx) {
        if (!Bot.getInstance().getPermissionManager().isAtLeast(ctx.getAuthor(), "sgt")) {
            ctx.getResponse().editOriginalEmbeds(EmbedCollection.getNoPermsEb().build()).queue();
            return;
        }

        switch (ctx.getEvent().getSubcommandName()) {
            case "create" -> create(ctx);
            case "end" -> end(ctx);
            default -> ctx.getResponse().editOriginalEmbeds(EmbedCollection.getSubcommandEb().build()).queue();
        }
    }

    private void create(CommandContext ctx) {
        String title = ctx.getEvent().getOption("title").getAsString();
        int minutes = ctx.getEvent().getOption("duration").getAsInt();
        boolean revisionAllowed = ctx.getEvent().getOption("revision").getAsBoolean();

        if (ctx.getEvent().getOption("channel").getAsChannel().getType() != ChannelType.TEXT) {
            ctx.getResponse().editOriginalEmbeds(EmbedCollection.getNoTextChannelEb().build()).queue();
            return;
        }

        TextChannel textChannel = ctx.getEvent().getOption("channel").getAsChannel().asTextChannel();
        List<String> options = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            if (ctx.getEvent().getOption("option" + (i + 1)) != null)
                options.add(ctx.getEvent().getOption("option" + (i + 1)).getAsString());
        }

        Bot.getInstance().getVoteManager().createVote(title, minutes, textChannel, revisionAllowed, options);
        ctx.getResponse().editOriginalEmbeds(new EmbedBuilder()
                .addField("Abstimmung erstellt!", "Die Abstimmung wurde erfolgreich gestartet," +
                        " du findest sie im von dir erwähnten Kanal.", false).setColor(Color.green)
                .build()).queue();
    }

    private void end(CommandContext ctx) {
        String id = ctx.getEvent().getOption("title").getAsString().toLowerCase().replaceAll(" ", "");
        if (!Bot.getInstance().getVoteManager().isActiveVote(id)) {
            ctx.getResponse().editOriginalEmbeds(new EmbedBuilder()
                    .addField("Keine laufende Abstimmung!", "Es läuft aktuell keine Abstimmung " +
                            "mit diesem Titel.", false).setColor(Color.red)
                    .build()).queue();
            return;
        }

        Bot.getInstance().getVoteManager().endVote(id, ctx.getGuild());
        ctx.getResponse().editOriginalEmbeds(new EmbedBuilder()
                .addField("Abstimmung beendet!", "Die Abstimmung wurde vorzeitig beendet.", false)
                        .setColor(Color.red)
                .build()).queue();
    }
}
