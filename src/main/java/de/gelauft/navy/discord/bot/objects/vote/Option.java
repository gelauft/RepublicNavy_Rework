package de.gelauft.navy.discord.bot.objects.vote;

import lombok.Getter;
import lombok.Setter;

/**
 * @author |Eric|#0001
 * created on 06.02.2023
 * created for RepublicNavy_Rework
 */

@Getter
@Setter
public class Option implements Comparable<Option> {
    private int id;
    private String label;
    private int votes;

    public Option(int id, String label) {
        this.id = id;
        this.label = label;
        this.votes = 0;
    }

    @Override
    public int compareTo(Option o) {
        return Integer.compare(this.votes, o.votes);
    }
}
