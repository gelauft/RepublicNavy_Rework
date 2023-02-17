package de.gelauft.navy.discord.bot.objects.vote;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author |Eric|#0001
 * created on 05.02.2023
 * created for RepublicNavy_Rework
 */

@AllArgsConstructor
@Getter
@Setter
public class Voter {

    private long memberId;
    private int chosenOption;

}
