package commands;

import mytool.Tool;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Botcommands extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equals("join")){
            if(event.getOption("channel-name") != null){
                String providedChannelName = event.getOption("channel-name").getAsString();
                List<VoiceChannel> voiceChannelList = event.getGuild().getVoiceChannelsByName(providedChannelName, true);
                if (voiceChannelList.isEmpty()){
                    String message = "**" + providedChannelName + "** is not found";
                    Tool.sendMessageEmbeds(event, message);
                    return;
                }
                VoiceChannel voiceChannel = voiceChannelList.get(0);
                event.getGuild().getAudioManager().openAudioConnection(voiceChannel);
                String message = "Connecting to **" + voiceChannel.getName() + "**";
                Tool.sendMessageEmbeds(event, message);
            }else {
                VoiceChannel voiceChannel = null;
                try {
                    voiceChannel = event.getMember().getVoiceState().getChannel().asVoiceChannel();
                }catch (NullPointerException e){
                    System.out.println(e);
                }
                if(voiceChannel == null){
                    String message = "You're not in a voice channel";
                    Tool.sendMessageEmbeds(event, message);
                }else {
                    String message = "Connecting to **" + voiceChannel.getName() + "**";
                    event.getGuild().getAudioManager().openAudioConnection(voiceChannel);
                    Tool.sendMessageEmbeds(event, message);
                }

            }
        }
        else if(event.getName().equals("leave")){
            String message = "testing";
            Tool.sendMessageEmbeds(event, message);
        }
    }
}
