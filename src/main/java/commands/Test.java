package commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavaplayer.GuildMusicManager;
import lavaplayer.LavaPlayer;
import utils.Util;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class Test extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        final LavaPlayer lavaPlayer = new LavaPlayer().getINSTANCE();
        final GuildMusicManager musicManager = lavaPlayer.getGuildAudioPlayer(event.getGuild());

        if(event.getMessage().getContentRaw().equalsIgnoreCase("testlist")){
            if(musicManager.trackScheduler.getQueue().isEmpty()){
                String message = "There's no tracks in queue";
                Util.sendMessageEmbeds(event,message);
                return;
            }
            Object[] tracks = musicManager.trackScheduler.getQueue().toArray();
            List<String> trackList = new ArrayList<>();
            for (int i = 0; i < tracks.length; i++) {
                AudioTrack track = ((AudioTrack) tracks[i]);
                String name = track.getInfo().title;
                String url = track.getInfo().uri;
                String message = "**#" + String.valueOf(i+1) + "** " + Util.toMaskedLink(name,url) + " by **"
                        + track.getInfo().author + "** - time: " + Util.getTrackDuration(track.getDuration());
                trackList.add(message);
            }
            List<Button> buttonList = new ArrayList<>();
            for(int i = 0; i < Math.min(5,trackList.size()); i++){
                buttonList.add(net.dv8tion.jda.api.interactions.components.buttons.Button
                        .primary("button-"+String.valueOf(i+1),String.valueOf(i+1)));
            }
            MessageCreateData message = new MessageCreateBuilder()
                    .addEmbeds(getMessageEmbed(trackList))
                    .addActionRow(ActionRow.of(buttonList).getButtons())
                    .build();
            event.getChannel().sendMessage(message).queue();
        }
    }
    @Contract("_ -> new")
    @NotNull
    private MessageEmbed getMessageEmbed(@NotNull List<String> trackList){


        String url = "";
        String title = "Upcoming tracks:";
        String description = "";
        EmbedType type = EmbedType.UNKNOWN;
        OffsetDateTime timeStamp = OffsetDateTime.now();
        int color = 255;
        MessageEmbed.Thumbnail thumbnail = new MessageEmbed.Thumbnail("","",0,0);
        MessageEmbed.Provider provider = new MessageEmbed.Provider("","");
        MessageEmbed.AuthorInfo author = new MessageEmbed.AuthorInfo("","","","");
        MessageEmbed.VideoInfo videoInfo = new MessageEmbed.VideoInfo("",0, 0);
        MessageEmbed.Footer footer = new MessageEmbed.Footer("","","");
        MessageEmbed.ImageInfo image = new MessageEmbed.ImageInfo("","",0,0);
        List<MessageEmbed.Field> fields = new ArrayList<>();
        for(String track: trackList){
            fields.add(new MessageEmbed.Field("",track,false));
        }




        return new MessageEmbed(url,title,description,type,timeStamp,color,thumbnail,provider,author,videoInfo,footer,image,fields);
    }

}
