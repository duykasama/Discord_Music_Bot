package commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import lavaplayer.GuildMusicManager;
import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import tools.tool;

import java.util.List;

public class CommandList {
    private tool tool = new tool();
    public void join(@NotNull SlashCommandInteractionEvent event){

        OptionMapping option = event.getOption("channel-name");

        if(option != null){

            String channelName = option.getAsString();
            List<VoiceChannel> voiceChannelList = event.getGuild().getVoiceChannelsByName(channelName, true);

            if(!voiceChannelList.toString().equals("[]")){
                VoiceChannel voiceChannel = voiceChannelList.get(0);
                String message = "Connected to " + voiceChannel.getName();
                event.getGuild().getAudioManager().openAudioConnection(voiceChannel);
                event.getGuild().getAudioManager().setSelfDeafened(true);
                tool.reply(event,message);
            }else {
                String message = "Can not find " + channelName;
                tool.reply(event,message);
            }
        }
        else{
            String ID = event.getMember().getId();
            boolean voiceConnected = event.getMember().getVoiceState().inAudioChannel();
            String authorName = event.getMember().getNickname();
            if(voiceConnected){
                VoiceChannel voiceChannel = event.getMember().getVoiceState().getChannel().asVoiceChannel();
                String voiceChannelName = voiceChannel.getName();
                String reply = "Connected to " + voiceChannelName;
                event.getGuild().getAudioManager().openAudioConnection(voiceChannel);
                event.getGuild().getAudioManager().setSelfDeafened(true);
                tool.reply(event,reply);
            }else{
                String reply = "You need to join a voice channel first";
                tool.reply(event,reply);
            }
        }
    }

    public void leave(@NotNull SlashCommandInteractionEvent event){
        boolean connected = event.getGuild().getAudioManager().isConnected();
        if(connected){
            event.getGuild().getAudioManager().closeAudioConnection();
            String channelName = event.getGuild().getAudioManager().getConnectedChannel().getName();
            String message = "Left " + channelName;
            tool.reply(event,message);
        }else{
            String message = "I'm not in a voice channel";
            tool.reply(event,message);
        }
    }

    public void play(@NotNull SlashCommandInteractionEvent event){
        boolean connected = event.getGuild().getAudioManager().isConnected() || event.getMember().getVoiceState().inAudioChannel();
        if(!connected){
            String message = "I don't know where to play the music \uD83D\uDE15 \nYou need to join a voice channel first";
            tool.reply(event,message);
        }else{
            boolean selfConnected = event.getGuild().getAudioManager().isConnected();
            if(!selfConnected){
                VoiceChannel voiceChannel = event.getMember().getVoiceState().getChannel().asVoiceChannel();
                event.getGuild().getAudioManager().openAudioConnection(voiceChannel);
            }

            String url = event.getOption("link-or-query").getAsString();
            if(!tool.isUrl(url)){
                url = "ytsearch:" + url;
            }
            PlayerManager playerManager = new PlayerManager();
            playerManager.getINSTANCE().loadAndPlay(event,url);
        }
    }

    public void stop(@NotNull SlashCommandInteractionEvent event){
        final GuildMusicManager musicManager =  PlayerManager.getINSTANCE().getMusicManager(event.getGuild());
        musicManager.audioPlayer.stopTrack();
        musicManager.scheduler.queue.clear();
        String message = "Stopped the music and cleared the playlist";
        tool.reply(event,message);
    }

    public void skip(@NotNull SlashCommandInteractionEvent event){
        GuildMusicManager musicManager = PlayerManager.getINSTANCE().getMusicManager(event.getGuild());
        AudioPlayer audioPlayer = musicManager.audioPlayer;
        if(audioPlayer.getPlayingTrack() == null){
            String message = "There's no song being played";
            tool.reply(event,message);
        }
        else {
            musicManager.scheduler.nextTrack();
            String message = "Skipped the current song";
            tool.reply(event,message);
        }
    }

    public void pause(@NotNull SlashCommandInteractionEvent event){
        PlayerManager playerManager = new PlayerManager();
        playerManager.pause(event);
        String message = "Paused ⏸️";
        tool.reply(event,message);
    }
    public void resume(@NotNull SlashCommandInteractionEvent event){
        PlayerManager playerManager = new PlayerManager();
        playerManager.resume(event);
        String message = "Resumed ▶️";
        tool.reply(event,message);
    }
}

