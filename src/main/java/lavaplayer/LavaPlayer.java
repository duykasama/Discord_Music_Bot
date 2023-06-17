package lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import utils.Util;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;


import java.util.*;


public class LavaPlayer extends ListenerAdapter {
    private final AudioPlayerManager audioPlayerManager;
    private final Map<Long,GuildMusicManager> musicmanagers;
    private static LavaPlayer INSTANCE;
    public LavaPlayer(){
        this.musicmanagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
    }

    @NotNull
    public synchronized GuildMusicManager getGuildAudioPlayer(@NotNull Guild guild){
        long guildId = guild.getIdLong();
        GuildMusicManager musicManager = musicmanagers.get(guildId);
        if(musicManager == null){
            musicManager = new GuildMusicManager(audioPlayerManager);
            musicmanagers.put(guildId,musicManager);
        }
        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
        return musicManager;
    }
    public void loadAndPlay(@NotNull SlashCommandInteractionEvent event, final String trackUrl){
        Guild guild = event.getGuild();
        GuildMusicManager musicManager = getGuildAudioPlayer(guild);

        audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                musicManager.trackScheduler.queue(audioTrack);
                String message = "Add to queue: **" + audioTrack.getInfo().title + "** by **" + audioTrack.getInfo().author
                        + "**";
                Util.sendMessageEmbeds(event,message);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                int trackSize = Math.min(audioPlaylist.getTracks().size(),20);
                if(audioPlaylist.isSearchResult()){
                    for(int i = 0; i < trackSize; i++){
                        musicManager.trackScheduler.queue(audioPlaylist.getTracks().get(i));
                    }
                    String message = "Adding to queue: " + trackSize + " tracks for search result **" + trackUrl
                            .replace("ytsearch: ","") + "**";
                    Util.sendMessageEmbeds(event, message);
                }else {
                    for(int i = 0; i < trackSize; i++){
                        musicManager.trackScheduler.queue(audioPlaylist.getTracks().get(i));
                    }
                    String message = "Added " + trackSize + " tracks from " + audioPlaylist.getName();
                    Util.sendMessageEmbeds(event,message);
                }
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
                System.out.println(e.getCause().getMessage());

            }
        });
    }

//    public void playSpotifyTrack (SlashCommandInteractionEvent event, Track track){
//        Guild guild = event.getGuild();
//        GuildMusicManager musicManager = getGuildAudioPlayer(guild);
//
//        audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
//            @Override
//            public void trackLoaded(AudioTrack audioTrack) {
//                musicManager.trackScheduler.queue(audioTrack);
//                String message = "Add to queue: **" + audioTrack.getInfo().title + "** by **" + audioTrack.getInfo().author
//                        + "**";
//                Util.sendMessageEmbeds(event,message);
//            }
//
//            @Override
//            public void playlistLoaded(AudioPlaylist audioPlaylist) {
//                int trackSize = Math.min(audioPlaylist.getTracks().size(),20);
//                if(audioPlaylist.isSearchResult()){
//                    for(int i = 0; i < trackSize; i++){
//                        musicManager.trackScheduler.queue(audioPlaylist.getTracks().get(i));
//                    }
//                    String message = "Adding to queue: " + trackSize + " tracks for search result **" + trackUrl
//                            .replace("ytsearch: ","") + "**";
//                    Util.sendMessageEmbeds(event, message);
//                }else {
//                    for(int i = 0; i < trackSize; i++){
//                        musicManager.trackScheduler.queue(audioPlaylist.getTracks().get(i));
//                    }
//                    String message = "Added " + trackSize + " tracks from " + audioPlaylist.getName();
//                    Util.sendMessageEmbeds(event,message);
//                }
//            }
//
//            @Override
//            public void noMatches() {
//                String message = "Found no results for **" + trackUrl + "**";
//                Util.sendMessageEmbeds(event,message);
//            }
//
//            @Override
//            public void loadFailed(FriendlyException e) {
//                String message = "For some reason, loading tracks failed";
//                Util.sendMessageEmbeds(event, message);
//            }
//        });
//
//    }
    public LavaPlayer getINSTANCE(){
        if(INSTANCE == null){
            INSTANCE = new LavaPlayer();
        }
        return INSTANCE;
    }
    public AudioPlayerManager getAudioPlayerManager(){
        return this.audioPlayerManager;
    }
}
