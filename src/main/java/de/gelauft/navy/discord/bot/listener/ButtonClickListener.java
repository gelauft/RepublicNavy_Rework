package de.gelauft.navy.discord.bot.listener;

import de.gelauft.navy.discord.bot.Bot;
import de.gelauft.navy.discord.bot.manager.MemberManager;
import de.gelauft.navy.discord.bot.manager.PermissionManager;
import de.gelauft.navy.discord.bot.manager.PunishmentManager;
import de.gelauft.navy.discord.bot.manager.VoteManager;
import de.gelauft.navy.discord.bot.utils.EmbedCollection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

/**
 * @author |Eric|#0001
 * created on 05.02.2023
 * created for RepublicNavy_Rework
 */

public class ButtonClickListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.isFromGuild()) return;

        String[] buttonId = event.getComponent().getId().split(":");
        PermissionManager permissionManager = Bot.getInstance().getPermissionManager();
        if (buttonId[0].equals("member")) {
            MemberManager memberManager = Bot.getInstance().getMemberManager();

            if (buttonId[1].equals("confirm")) {
                memberManager.handleConfirmation(event.getMessage());
                return;
            }

            if (buttonId[1].equals("deny")) {
                memberManager.handleDenial(event.getMessage());
                return;
            }

            if (buttonId[1].equals("leave")) {
                PunishmentManager punishmentManager = Bot.getInstance().getPunishmentManager();

                if (buttonId[2].equals("remain")) {
                    if (!permissionManager.isAtLeast(event.getMember(), "sgt")) {
                        event.deferReply(true).queue(response -> {
                            response.editOriginalEmbeds(EmbedCollection.getNoPermsEb().build()).queue();
                        });
                        return;
                    }

                    punishmentManager.remainStrikes(event.getMessage(), event.getMember());
                    return;
                }

                if (buttonId[2].equals("archive")) {
                    if (!permissionManager.isAtLeast(event.getMember(), "sgt")) {
                        event.deferReply(true).queue(response -> {
                            response.editOriginalEmbeds(EmbedCollection.getNoPermsEb().build()).queue();
                        });
                        return;
                    }

                    punishmentManager.archiveStrikes(event.getMessage(), event.getMember());
                    return;
                }

                if (buttonId[2].equals("delete")) {
                    if (!permissionManager.isAtLeast(event.getMember(), "lt")) {
                        event.deferReply(true).queue(response -> {
                            response.editOriginalEmbeds(EmbedCollection.getNoPermsEb().build()).queue();
                        });
                        return;
                    }

                    punishmentManager.deleteStrikes(event.getMessage(), event.getMember());
                    return;
                }
            }

            return;
        }

        if (buttonId[0].equals("vote")) {
            VoteManager voteManager = Bot.getInstance().getVoteManager();
            String voteId = buttonId[2];

            if (!voteManager.isActiveVote(voteId)) {
                event.replyEmbeds(new EmbedBuilder()
                        .addField("Keine laufende Abstimmung!", "Es läuft aktuell keine Abstimmung " +
                                "mit diesem Titel.", false).setColor(Color.red)
                        .build()).queue();
                return;
            }

            if (voteManager.hasVoted(event.getMember(), voteId)) {
                if (!voteManager.revisionAllowed(voteId)) {
                    event.replyEmbeds(new EmbedBuilder()
                                    .addField("Änderung nicht erlaubt!", "Du kannst deine Stimme in " +
                                            "dieser Abstimmung nicht mehr nachgträglich ändern.", false)
                            .setColor(Color.red)
                            .build()).queue();
                    return;
                }

                voteManager.reviseVote(event.getMember(), voteId, buttonId[3]);
                event.replyEmbeds(new EmbedBuilder()
                                .addField("Stimme geändert!", "Deine Stimme wurde erfolgreich geändert.",
                                        false).setColor(Color.red)
                        .build()).queue();
            } else {
                voteManager.handleVote(event.getMember(), voteId, buttonId[3]);
                event.replyEmbeds(new EmbedBuilder()
                                .addField("Stimme abgegeben!", "Du hast erfolgreich abgestimmt.", false)
                                .setColor(Color.red)
                        .build()).queue();
                return;
            }

            return;
        }
    }
}
