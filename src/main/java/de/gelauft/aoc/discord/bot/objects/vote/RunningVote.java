package de.gelauft.aoc.discord.bot.objects.vote;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author |Eric|#0001
 * created on 05.02.2023
 * created for RepublicNavy_Rework
 */

@Getter
@Setter
public class RunningVote {

    private String id;
    private String title;
    private long messageId;
    private long channelId;
    private boolean revisionAllowed;
    private List<Voter> voters;
        private List<Option> options;
    private long expiration;

    public RunningVote(String id, String title, long messageId, long channelId, boolean revisionAllowed,
                       List<Option> options, long expiration) {
        this.id = id;
        this.title = title;
        this.messageId = messageId;
        this.channelId = channelId;
        this.revisionAllowed = revisionAllowed;
        this.options = options;
        this.voters = new ArrayList<>();
        this.expiration = expiration;
    }
}
