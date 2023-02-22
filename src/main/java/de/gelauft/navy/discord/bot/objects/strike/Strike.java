package de.gelauft.navy.discord.bot.objects.strike;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author |Eric|#0001
 * created on 17.02.2023
 * created for RepublicNavy_Rework
 */

@AllArgsConstructor
@Getter
@Setter
public class Strike {

    private int id;
    private long targetId;
    private long operatorId;
    private String reason;
    private long expirationDate;
    private boolean expired;

}
