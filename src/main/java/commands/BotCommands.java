package commands;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavaplayer.GuildMusicManager;
import lavaplayer.LavaPlayer;
import utils.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BotCommands extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        final LavaPlayer lavaPlayer = new LavaPlayer().getINSTANCE();
        final GuildMusicManager musicManager = lavaPlayer.getGuildAudioPlayer(event.getGuild());

        String requestedCommand = event.getName();
        String url;
        String message;
        AudioTrack track;
        VoiceChannel voiceChannel = null;

        switch (requestedCommand){
            case "join":
                if(event.getOption("channel-name") != null){
                    String providedChannelName = event.getOption("channel-name").getAsString();
                    List<VoiceChannel> voiceChannelList = event.getGuild().getVoiceChannelsByName(providedChannelName, true);
                    if (voiceChannelList.isEmpty()){
                        message = "**" + providedChannelName + "** is not found";
                        Util.sendMessageEmbeds(event, message);
                        return;
                    }
                    voiceChannel = voiceChannelList.get(0);
                    event.getGuild().getAudioManager().openAudioConnection(voiceChannel);
                    message = "Connecting to **" + voiceChannel.getName() + "**";
                    Util.sendMessageEmbeds(event, message);
                }else {
                    voiceChannel = null;
                    try {
                        voiceChannel = event.getMember().getVoiceState().getChannel().asVoiceChannel();
                    }catch (NullPointerException e){
                        System.out.println(e + " at join command");
                    }
                    if(voiceChannel == null){
                        message = "You're not in a voice channel";
                        Util.sendMessageEmbeds(event, message);
                    }else {
                        message = "Connecting to **" + voiceChannel.getName() + "**";
                        event.getGuild().getAudioManager().openAudioConnection(voiceChannel);
                        Util.sendMessageEmbeds(event, message);
                    }
                }
                break;
            case "leave":
                voiceChannel = null;
                try{
                    voiceChannel = event.getGuild().getAudioManager().getConnectedChannel().asVoiceChannel();
                }catch (NullPointerException e){
                    System.out.println(e + " at leave command");
                }
                if (voiceChannel == null){
                    message = "I'm not currently in a voice channel";
                    Util.sendMessageEmbeds(event,message);
                    return;
                }
                event.getGuild().getAudioManager().closeAudioConnection();
                musicManager.trackScheduler.getQueue().clear();
                musicManager.trackScheduler.getAudioPlayer().stopTrack();
                message = "Left **" + voiceChannel.getName() + "** and cleared the playlist";
                Util.sendMessageEmbeds(event, message);
                break;
            case "play":
                if(event.getGuild().getAudioManager().isConnected()){
                    voiceChannel = event.getGuild().getAudioManager().getConnectedChannel().asVoiceChannel();
                }else if(event.getMember().getVoiceState().inAudioChannel()){
                    voiceChannel = event.getMember().getVoiceState().getChannel().asVoiceChannel();
                }else {
                    message = "You need to join a voice channel first";
                    Util.sendMessageEmbeds(event,message);
                    return;
                }
                event.getGuild().getAudioManager().openAudioConnection(voiceChannel);
                url = event.getOption("link-or-query").getAsString();
                if(!Util.isUrl(url)){
                    url = "ytsearch: " + url;
                }
                event.getGuild().getAudioManager().setSelfDeafened(true);
                lavaPlayer.loadAndPlay(event,url);
                break;
            case "skip":
                if(musicManager.trackScheduler.getQueue().isEmpty()){
                    message = "There's no tracks in queue";
                    Util.sendMessageEmbeds(event,message);
                    return;
                }
                int noOfTracksToSkip = 1;
                message = "";
                try{
                    noOfTracksToSkip = event.getOption("quantity").getAsInt();
                } catch (NullPointerException e){
                    System.out.println(e + " at skip command");
                }
                noOfTracksToSkip = Math.min(noOfTracksToSkip,musicManager.trackScheduler.getQueue().size());
                for(int i = 0; i < noOfTracksToSkip; i++){
                    musicManager.trackScheduler.nextTrack();
                }
                if(noOfTracksToSkip == 1){
                    message = "Skipped the current track";
                }
                else{
                    message = "Skipped " + noOfTracksToSkip + " tracks";
                }
                Util.sendMessageEmbeds(event,message);
                break;
            case "list":
                if(musicManager.trackScheduler.getQueue().isEmpty()){
                    message = "There's no tracks in queue";
                    Util.sendMessageEmbeds(event,message);
                    return;
                }
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("Upcoming tracks: ")
                        .setColor(Color.blue)
                        .setFooter(Util.getFooter(event.getMember().getEffectiveName()));
                Object[] tracks = musicManager.trackScheduler.getQueue().toArray();
                for (int i = 0; i < tracks.length; i++) {
                    track = ((AudioTrack) tracks[i]);
                    String name = track.getInfo().title;
                    url = track.getInfo().uri;
                    message = "**#" + (i + 1) + "** " + Util.toMaskedLink(name,url) + " by **"
                            + track.getInfo().author + "** - time: " + Util.getTrackDuration(track.getDuration());
                    embedBuilder.addField("",message,false);
                }
                try{
                    event.deferReply().queue();
                    event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
                    return;
                }catch (Exception e){
                    e.printStackTrace();
                }
                event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                break;
            case "pause":
                if(musicManager.trackScheduler.getAudioPlayer().getPlayingTrack() == null){
                    message = "Currently not playing any tracks";
                    Util.sendMessageEmbeds(event,message);
                    return;
                }
                musicManager.trackScheduler.getAudioPlayer().setPaused(true);
                message = "Paused ⏸";
                Util.sendMessageEmbeds(event,message);
                break;
            case "resume":
                if(!musicManager.trackScheduler.getAudioPlayer().isPaused()){
                    message = "Currently not pausing";
                    Util.sendMessageEmbeds(event,message);
                    return;
                }
                musicManager.trackScheduler.getAudioPlayer().setPaused(false);
                message = "Resumed ▶";
                Util.sendMessageEmbeds(event,message);
                break;
            case "playing":
                track = musicManager.trackScheduler.getCurrentTrack();
                message = Util.toMaskedLink(track.getInfo().title,track.getInfo().uri) + " by " + track.getInfo().author
                        + " - time: " + Util.getTrackDuration(track.getDuration());
                embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("Now playing: ")
                        .addField("",message,false);
                event.deferReply().queue();
                event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
                break;
            case "playnow":
                AudioPlayerManager audioPlayerManager = lavaPlayer.getAudioPlayerManager();
                url = event.getOption("url-or-query").getAsString();
                if(!Util.isUrl(url)){
                    url = "ytsearch: " + url;
                }
                final String trackUrl = url;
                audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack audioTrack) {
                        musicManager.trackScheduler.getAudioPlayer().stopTrack();
                        musicManager.trackScheduler.getAudioPlayer().startTrack(audioTrack,true);
                        String message = "Now playing: **" + audioTrack.getInfo().title + "** by **" + audioTrack.getInfo().author
                                + "** - time: " + Util.getTrackDuration(audioTrack.getDuration());
                        Util.sendMessageEmbeds(event,message);
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist audioPlaylist) {
                        AudioTrack track = audioPlaylist.getTracks().get(0);
                        musicManager.trackScheduler.getAudioPlayer().startTrack(track,true);
                        String message = "Now playing: **" + track.getInfo().title + "** by **" + track.getInfo().author
                                + "** - time: " + Util.getTrackDuration(track.getDuration());
                        Util.sendMessageEmbeds(event, message);
                    }

                    @Override
                    public void noMatches() {
                        String message = "Found no results for **" + trackUrl + "**";
                        Util.sendMessageEmbeds(event,message);
                    }

                    @Override
                    public void loadFailed(FriendlyException e) {
                        String message = "For some reason, loading tracks failed";
                        Util.sendMessageEmbeds(event, message);
                    }
                });
                break;
            case "skipto":
                int index = event.getOption("index").getAsInt() - 1;
                List trackList = new ArrayList<Object>(Arrays.asList(musicManager.trackScheduler.getQueue().toArray()));
                if(index > trackList.size()){
                    message = "Index out of bound";
                    Util.sendMessageEmbeds(event,message);
                    return;
                }
                track = null;
                for(int i = 0; i < trackList.size(); i++){
                    if(i == index){
                        track = (AudioTrack) trackList.get(i);
                        trackList.remove(trackList.get(i));
                        break;
                    }
                }
                musicManager.trackScheduler.getQueue().clear();
                for (Object obj : trackList) {
                    musicManager.trackScheduler.queue((AudioTrack) obj);
                }
                musicManager.trackScheduler.getAudioPlayer().stopTrack();
                musicManager.trackScheduler.getAudioPlayer().startTrack(track,true);
                message = "Now playing: **" + track.getInfo().title + "** by **" + track.getInfo().author
                        + "** - time: " + Util.getTrackDuration(track.getDuration());
                Util.sendMessageEmbeds(event,message);
                break;
            case "test":
                //make a vote command for a specific reason

                event.getChannel().sendMessage("test running").queue();

                event.getMember().getUser().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage("hello").queue();
                });
                break;
            case "clear":
                musicManager.trackScheduler.getQueue().clear();
                message = "Cleared the playlist";
                Util.sendMessageEmbeds(event,message);
                break;
            case "help":
                break;
            case "test1":

                url = "https://open.spotify.com/embed/track/3vv9phIu6Y1vX3jcqaGz5Z";
                System.out.println(url);
                event.getGuild().getAudioManager().setSelfDeafened(true);
                lavaPlayer.loadAndPlay(event,url);
            default:

        }
    }
}

















