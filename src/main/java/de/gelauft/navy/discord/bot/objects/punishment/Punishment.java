package de.gelauft.navy.discord.bot.objects.punishment;

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
public class Punishment {

    private int id;
    private long targetId;
    private long operatorId;
    private String reason;
    private PunishmentType punishmentType;
    private boolean permanent;
    private long expirationDate;
    private boolean repealed;

}
