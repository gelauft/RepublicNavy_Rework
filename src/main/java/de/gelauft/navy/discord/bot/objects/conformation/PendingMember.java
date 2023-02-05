package de.gelauft.navy.discord.bot.objects.conformation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author |Eric|#0001
 * created on 25.01.2023
 * created for RepublicNavy_Rework
 */

@Getter
@Setter
public class PendingMember {

    private long memberId;
    private long conformationMessageId;
    private long selectionMessageId;

    public PendingMember(long memberId, long conformationMessageId) {
        this.memberId = memberId;
        this.conformationMessageId = conformationMessageId;
    }

}
