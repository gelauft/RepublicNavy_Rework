package de.gelauft.navy.discord.bot.listener;

import de.gelauft.navy.discord.bot.Bot;
import de.gelauft.navy.discord.bot.manager.MemberManager;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import okhttp3.internal.http2.Http2Connection;

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
        if (buttonId[0].equals("member")) {
            MemberManager memberManager = Bot.getInstance().getMemberManager();

            if (buttonId[1].equals("confirm"))
                memberManager.handleConfirmation(event.getMessage());

            if (buttonId[1].equals("deny"))
                memberManager.handleDenial(event.getMessage());
        }
    }
}
