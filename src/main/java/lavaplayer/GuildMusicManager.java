package lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import org.jetbrains.annotations.NotNull;

public class GuildMusicManager {
    private final AudioPlayer audioPlayer;
    public final TrackScheduler trackScheduler;

    public GuildMusicManager(@NotNull AudioPlayerManager manager){
        this.audioPlayer = manager.createPlayer();
        this.trackScheduler = new TrackScheduler(audioPlayer);
        audioPlayer.addListener(trackScheduler);
    }
    public AudioPlayerSendHandler getSendHandler(){
        return new AudioPlayerSendHandler(audioPlayer);
    }
}
