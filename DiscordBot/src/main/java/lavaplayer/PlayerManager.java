package lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.Manager;
import org.jetbrains.annotations.NotNull;
import tools.tool;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PlayerManager {

    tool tool = new tool();
    private static PlayerManager INSTANCE;
    private final Map<Long,GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;
    public PlayerManager(){
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(@NotNull Guild guild){
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), new Function<Long, GuildMusicManager>() {
            @Override
            public GuildMusicManager apply(Long guildId) {
                final GuildMusicManager guildMusicManager = new GuildMusicManager(PlayerManager.this.audioPlayerManager);
                guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
                return guildMusicManager;
            }
        });
    }

    public void loadAndPlay(@NotNull SlashCommandInteractionEvent event, String trackUrl){
        final GuildMusicManager musicManager = this.getMusicManager(event.getGuild());
        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                musicManager.scheduler.queue(audioTrack);
                String message = "Now playing: "+audioTrack.getInfo().title+" by "+audioTrack.getInfo().author;
                tool.reply(event,message);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                final List<AudioTrack> tracks = audioPlaylist.getTracks();
                String message = "Adding to queue: " + tracks.size() + " tracks from playlist " + audioPlaylist.getName();
                tool.reply(event,message);
                for(final AudioTrack track: tracks){
                    musicManager.scheduler.queue(track);
                }
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException e) {

            }
        });
    }

    public void pause(@NotNull SlashCommandInteractionEvent event){
        final GuildMusicManager musicManager = this.getMusicManager(event.getGuild());
        musicManager.audioPlayer.setPaused(true);
    }
    public void resume(@NotNull SlashCommandInteractionEvent event){
        final GuildMusicManager musicManager = this.getMusicManager(event.getGuild());
        musicManager.scheduler.onPlayerResume(musicManager.audioPlayer);
        String resume = musicManager.audioPlayer.getPlayingTrack().toString();
        event.reply(resume).queue();
    }

    public static PlayerManager getINSTANCE(){
        if(INSTANCE == null){
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }

}
