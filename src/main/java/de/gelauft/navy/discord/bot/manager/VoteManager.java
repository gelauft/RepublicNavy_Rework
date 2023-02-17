package de.gelauft.navy.discord.bot.manager;

import de.gelauft.navy.discord.bot.Bot;
import de.gelauft.navy.discord.bot.objects.vote.Option;
import de.gelauft.navy.discord.bot.objects.vote.RunningVote;
import de.gelauft.navy.discord.bot.objects.vote.Voter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.Color;
import java.util.*;

/**
 * @author |Eric|#0001
 * created on 05.02.2023
 * created for RepublicNavy_Rework
 */

public class VoteManager {

    private final List<RunningVote> runningVotes;

    public VoteManager() {
        this.runningVotes = new ArrayList<>();
        this.startTimer();
    }

    public RunningVote getVoteById(String id) {
        return this.runningVotes.stream().filter(runningVote -> runningVote.getId().equals(id)).toList().get(0);
    }

    public RunningVote getVoteByMessage(long messageId) {
        return this.runningVotes.stream().filter(runningVote -> runningVote.getMessageId() == messageId).toList().get(0);
    }

    public Voter getVoterByMember(long memberId, String voteId) {
        return this.getVoteById(voteId).getVoters().stream().filter(voter -> voter.getMemberId() == memberId)
                .toList().get(0);
    }

    public void createVote(String title, int duration, TextChannel channel, boolean revisionAllowed, List<String> stringOptions) {
        String id = title.toLowerCase().replaceAll(" ", "");
        EmbedBuilder eb = new EmbedBuilder().setTitle("Abstimmung - " + title).setColor(Color.blue)
                .setDescription("Die Abstimmungsoptionen sind im folgenden aufgelistet, du hast " + duration +
                        " Minuten Zeit dich zu entscheiden");
        List<Button> buttons = new ArrayList<>();
        List<Option> options = new ArrayList<>();
        for (int i = 0; i <= stringOptions.size(); i++) {
            options.add(new Option(i, stringOptions.get(i)));
            eb.addField("Auswahlmöglichkeit " + (i + 1), stringOptions.get(i), false);
            buttons.add(Button.primary("vote:custom:" + id + ":" + i, stringOptions.get(i)));

        }

        channel.sendMessageEmbeds(eb.build()).setActionRow(buttons).queue(message -> {
            this.runningVotes.add(new RunningVote(id, title, message.getIdLong(), channel.getIdLong(), revisionAllowed,
                    options, System.currentTimeMillis() + ((long) duration * 60 * 1000)));
        });
    }

    public boolean isActiveVote(String id) {
        return this.runningVotes.stream().anyMatch(runningVote -> runningVote.getId().equals(id));
    }

    public boolean hasVoted(Member member, String id) {
        return this.getVoteById(id).getVoters().stream().anyMatch(voter -> voter.getMemberId() == member.getIdLong());
    }

    public boolean revisionAllowed(String id) {
        return this.getVoteById(id).isRevisionAllowed();
    }

    public void handleVote(Member member, String voteId, String choiceId) {
        RunningVote vote = this.getVoteById(voteId);
        this.runningVotes.remove(vote);

        Voter voter = new Voter(member.getIdLong(), Integer.parseInt(choiceId));
        List<Voter> voters = new ArrayList<>(vote.getVoters());
        voters.add(voter);
        vote.setVoters(voters);
        this.runningVotes.add(vote);
    }

    public void reviseVote(Member member, String voteId, String choiceId) {
        RunningVote vote = this.getVoteById(voteId);
        this.runningVotes.remove(vote);

        Voter voter = this.getVoterByMember(member.getIdLong(), voteId);
        List<Voter> voters = new ArrayList<>(vote.getVoters());
        voters.remove(voter);
        voter.setChosenOption(Integer.parseInt(choiceId));
        voters.add(voter);
        vote.setVoters(voters);
        this.runningVotes.add(vote);
    }

    public void endVote(String voteId, Guild guild) {
        RunningVote runningVote = this.getVoteById(voteId);
        this.runningVotes.remove(runningVote);

        List<Option> options = new ArrayList<>(runningVote.getOptions());
        Collections.sort(options);
        int totalVotes = runningVote.getVoters().size();
        for (Option option : options) {
            int votes = runningVote.getVoters().stream().filter(voter -> voter.getChosenOption() == option.getId())
                    .toList().size();
            option.setId(votes);
        }

        EmbedBuilder eb = new EmbedBuilder().setTitle("Abstimmung - " + runningVote.getTitle()).setColor(Color.blue)
                .setDescription("Die Zeit ist abgelaufen, die Stimmen wurden ausgewertet.");
        for (int i = 0; i <= options.size(); i++) {
            Option option = options.get(i);
            eb.addField(option.getLabel(), option.getVotes() + "(" + option.getVotes() / totalVotes + "%)", false);
        }

        guild.getTextChannelById(runningVote.getChannelId()).sendMessageEmbeds(eb.build())
                .setActionRow(Collections.emptyList()).queue();
    }

    private void startTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runningVotes.forEach(vote -> {
                    if (System.currentTimeMillis() >= vote.getExpiration()) endVote(vote.getId(), Bot.getJda()
                            .getGuildById(Bot.getInstance().getConfig().getGuildId()));
                });
            }
        }, 0, 1000 * 60);
    }
}
