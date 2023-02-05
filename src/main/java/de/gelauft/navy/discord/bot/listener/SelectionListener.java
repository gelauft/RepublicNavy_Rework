package de.gelauft.navy.discord.bot.listener;

import de.gelauft.navy.discord.bot.Bot;
import de.gelauft.navy.discord.bot.manager.MemberManager;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * @author |Eric|#0001
 * created on 05.02.2023
 * created for RepublicNavy_Rework
 */

public class SelectionListener extends ListenerAdapter {

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        String[] menuId = event.getComponent().getId().split(":");
        String[] interactionId = event.getInteraction().getId().split(":");

        if (menuId[0].equals("member")) {
            MemberManager memberManager = Bot.getInstance().getMemberManager();
            if (menuId[1].equals("confirm")) {
                if (menuId[2].equals("select")) {
                    memberManager.handleSelection(event.getMessage(), event.getMember(), interactionId);
                }
            }
        }
    }
}
