package de.gelauft.aoc.discord.bot.commands;

import de.gelauft.aoc.discord.bot.Bot;
import de.gelauft.aoc.discord.bot.objects.punishment.Punishment;
import de.gelauft.aoc.discord.bot.objects.punishment.PunishmentType;
import de.gelauft.aoc.discord.bot.utils.EmbedCollection;
import de.gelauft.aoc.discord.bot.objects.command.CommandContext;
import de.gelauft.aoc.discord.bot.objects.command.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author |Eric|#0001
 * created on 07.03.2023
 * created for RepublicNavy_Rework
 */

public class PunishmentCommand implements ICommand {

    @Override
    public CommandData getCommandData() {
        return new CommandDataImpl("punishment", "Bestrafe ein Einheitsmitglied")
                .addSubcommands(new SubcommandData("blacklist", "Verweise einen Nutzer und sperre ihn " +
                        "für zukünftige Beitritte")
                        .addOptions(new OptionData(OptionType.USER, "user", "Wer soll gesperrt werden?",
                                true))
                        .addOptions(new OptionData(OptionType.STRING, "reason", "Warum soll er gesperrt" +
                                " werden?", true))
                        .addOptions(new OptionData(OptionType.BOOLEAN, "silent", "Sollen Details " +
                                "öffentlich sein?", true))
                        .addOptions(new OptionData(OptionType.INTEGER, "duration", "Wie lange soll die" +
                                " Sperre anhalten (in Tagen)?", false)))
                .addSubcommands(new SubcommandData("kick", "Verweise einen Nutzer")
                        .addOptions(new OptionData(OptionType.USER, "user", "Wer soll verwiesen werden?",
                                true))
                        .addOptions(new OptionData(OptionType.STRING, "reason", "Warum soll er " +
                                "verwiesen werden?", true))
                        .addOptions(new OptionData(OptionType.BOOLEAN, "silent", "Sollen Details " +
                                "öffentlich sein?", true)))
                .addSubcommands(new SubcommandData("strike", "Verwarne einen Nutzer")
                        .addOptions(new OptionData(OptionType.USER, "user", "Wer soll verwarnt werden?",
                                true))
                        .addOptions(new OptionData(OptionType.STRING, "reason", "Warum soll er " +
                                "verwarnt werden?", true))
                        .addOptions(new OptionData(OptionType.BOOLEAN, "silent", "Sollen Details " +
                                "öffentlich sein?", true))
                        .addOptions(new OptionData(OptionType.INTEGER, "duration", "Wie lange soll die " +
                                "Verwarnung halten (in Tagen)?", false)))
                .addSubcommands(new SubcommandData("history", "Rufe die Sanktionen eines Nutzers auf")
                        .addOptions(new OptionData(OptionType.USER, "user", "Wessen Sanktionen möchtest " +
                                "du sehen?", true)))
                .addSubcommands(new SubcommandData("repeal", "Hebe eine Sanktion auf")
                        .addOptions(new OptionData(OptionType.INTEGER, "id", "Welche Sanktion soll " +
                                "aufgehoben werden?", true))
                        .addOptions(new OptionData(OptionType.STRING, "reason", "Warum soll die " +
                                "Sanktion aufgehoben werden?", true)))
                .addSubcommands(new SubcommandData("delete", "Lösche eine Sanktion")
                        .addOptions(new OptionData(OptionType.INTEGER, "id", "Welche Sanktion soll " +
                                "gelöscht werden?", true))
                        .addOptions(new OptionData(OptionType.STRING, "reason", "Warum soll die " +
                                "Sanktion gelöscht werden?", true)))
                .setGuildOnly(true);
    }

    @Override
    public void execute(CommandContext ctx) {
        if (!Bot.getInstance().getPermissionManager().isAtLeast(ctx.getAuthor(), "sgt")) {
            ctx.getResponse().editOriginalEmbeds(EmbedCollection.getNoPermsEb().build()).queue();
            return;
        }

        if (ctx.getEvent().getSubcommandName() == null) {
            ctx.getResponse().editOriginalEmbeds(EmbedCollection.getSubcommandEb().build()).queue();
            return;
        }

        switch (ctx.getEvent().getSubcommandName()) {
            case "blacklist" -> this.blacklist(ctx);
            case "kick" -> this.kick(ctx);
            case "strike" -> this.strike(ctx);
            case "history" -> this.history(ctx);
            case "repeal" -> this.repeal(ctx);
            case "delete" -> this.delete(ctx);
            default -> {
                ctx.getResponse().editOriginalEmbeds(EmbedCollection.getCommandNotFoundEb().build()).queue();
                return;
            }
        }

        ctx.getResponse().deleteOriginal().queue();
    }

    private void blacklist(CommandContext ctx) {
        Member target = ctx.getEvent().getOption("user").getAsMember();
        String reason = ctx.getEvent().getOption("reason").getAsString();
        boolean permanent;
        int durationDays;
        if (ctx.getEvent().getOption("duration") != null) {
            durationDays = ctx.getEvent().getOption("duration").getAsInt();
            permanent = false;
        } else {
            durationDays = 0;
            permanent = true;
        }
        boolean silent = ctx.getEvent().getOption("silent").getAsBoolean();

        Bot.getInstance().getPunishmentManager().blacklist(target, ctx.getAuthor(), reason, silent, durationDays,
                permanent, ctx.getGuild());
    }

    private void kick(CommandContext ctx) {
        Member target = ctx.getEvent().getOption("user").getAsMember();
        String reason = ctx.getEvent().getOption("reason").getAsString();
        boolean silent = ctx.getEvent().getOption("silent").getAsBoolean();

        Bot.getInstance().getPunishmentManager().kick(target, ctx.getAuthor(), reason, silent, true, ctx.getGuild());
    }

    private void strike(CommandContext ctx) {
        Member target = ctx.getEvent().getOption("user").getAsMember();
        String reason = ctx.getEvent().getOption("reason").getAsString();
        boolean permanent;
        int durationDays;
        if (ctx.getEvent().getOption("duration") != null) {
            durationDays = ctx.getEvent().getOption("duration").getAsInt();
            permanent = false;
        } else {
            durationDays = 0;
            permanent = true;
        }
        boolean silent = ctx.getEvent().getOption("silent").getAsBoolean();

        Bot.getInstance().getPunishmentManager().strike(target, ctx.getAuthor(), reason, silent, durationDays, permanent,
                ctx.getGuild());
    }

    private void history(CommandContext ctx) {
        User target = ctx.getEvent().getOption("user").getAsUser();
        List<Punishment> punishments = Bot.getInstance().getPunishmentManager().getPunishments().stream()
                .filter(punishment -> punishment.getTargetId() == ctx.getEvent().getOption("user").getAsUser()
                        .getIdLong()).toList();
        List<Punishment> blacklists = punishments.stream().filter(punishment -> punishment.getPunishmentType()
                == PunishmentType.BLACKLIST).toList();
        List<Punishment> kicks = punishments.stream().filter(punishment -> punishment.getPunishmentType()
                == PunishmentType.KICK).toList();
        List<Punishment> strikes = punishments.stream().filter(punishment -> punishment.getPunishmentType()
                == PunishmentType.STRIKE).toList();

        if (!blacklists.isEmpty()) {
            if (blacklists.stream().anyMatch(punishment -> !punishment.isRepealed())) {
                Punishment punishment = blacklists.stream().filter(pun -> !pun.isRepealed()).toList().get(0);
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Nutzer gesperrt!")
                        .setDescription("Der Nutzer ist für weitere Einheitsbeitritte gesperrt worden.")
                        .addField("Grund:", punishment.getReason(), false)
                        .addField("Gesperrt von:", Bot.getJda().getUserById(punishment.getOperatorId())
                                .getAsTag(), false);
                if (!punishment.isPermanent()) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm, dd.MM.yyyy");
                    String date = simpleDateFormat.format(new Date(punishment.getExpirationDate()));
                    eb.addField("Ablaufdatum:", date, false);
                } else eb.addField("Ablaufdatum:", "Nie", false);
                ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
            }

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Blacklist Einträge")
                    .setAuthor(target.getAsTag());
            blacklists.forEach(blacklist -> {
                eb.addField("Blacklist #" + blacklist.getId(), "", false)
                        .addField("Grund:", blacklist.getReason(), false)
                        .addField("Operator:", ctx.getEvent().getJDA().getUserById(blacklist.getOperatorId())
                                .getAsTag(), false);
                ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
            });
        }

        if (!kicks.isEmpty()) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Verweise").setAuthor(target.getAsTag());
            kicks.forEach(kick -> {
                eb.addField("Verweis #" + kick.getId(), "", false)
                        .addField("Grund:", kick.getReason(), false)
                        .addField("Operator:", ctx.getEvent().getJDA().getUserById(kick.getOperatorId())
                                .getAsTag(), false);
                ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
            });
        }

        if (!strikes.isEmpty()) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Strikes").setAuthor(target.getAsTag());
            strikes.forEach(strike -> {
                eb.addField("Strike #" + strike.getId(), "", false)
                        .addField("Grund:", strike.getReason(), false)
                        .addField("Operator:", ctx.getEvent().getJDA().getUserById(strike.getOperatorId())
                                .getAsTag(), false);
                if (!strike.isPermanent()) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm, dd.MM.yyyy");
                    String date = simpleDateFormat.format(new Date(strike.getExpirationDate()));
                    eb.addField("Ablaufdatum:", date, false);
                } else eb.addField("Ablaufdatum:", "Nie", false);
                ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
            });
        }
    }

    private void repeal(CommandContext ctx) {
        int punId = ctx.getEvent().getOption("id").getAsInt();
        Punishment punishment = Bot.getInstance().getPunishmentManager().getPunishmentById(punId);
        String reason = ctx.getEvent().getOption("reason").getAsString();

        Bot.getInstance().getPunishmentManager().revokePunishment(punishment, ctx.getAuthor(), reason, ctx.getGuild());
    }

    private void delete(CommandContext ctx) {
        if (!Bot.getInstance().getPermissionManager().isAtLeast(ctx.getAuthor(), "lt")) {
            ctx.getResponse().editOriginalEmbeds(EmbedCollection.getNoPermsEb().build()).queue();
            return;
        }

        Punishment punishment = Bot.getInstance().getPunishmentManager().getPunishmentById(ctx.getEvent()
                .getOption("id").getAsInt());

        Bot.getInstance().getPunishmentManager().deletePun(punishment, ctx.getEvent().getOption("reason").getAsString(),
                ctx.getAuthor());
    }
}
