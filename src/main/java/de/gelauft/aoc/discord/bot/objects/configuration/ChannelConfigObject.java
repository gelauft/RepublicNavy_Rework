package de.gelauft.aoc.discord.bot.objects.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * @author |Eric|#0001
 * created on 13.10.2022
 * created for Sondereinsatzbot
 */

@AllArgsConstructor
@Builder
@Getter
public class ChannelConfigObject {

    private List<ConfigChannel> configChannels;
    private List<ConfigCategory> configCategories;

    public ConfigChannel getChannelByName(String name) {
        return this.configChannels.stream().filter(configChannel -> configChannel.getSimplifiedName().equals(name))
                .toList().get(0);
    }

    public ConfigCategory getCategoryByName(String name) {
        return this.configCategories.stream().filter(configCategory -> configCategory.getSimplifiedName().equals(name))
                .toList().get(0);
    }

}
