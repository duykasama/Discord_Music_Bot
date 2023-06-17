package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.AbstractMessageBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ButtonListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().equals("test")){
            Button testButton = net.dv8tion.jda.api.interactions.components.buttons.Button.danger("test-button","Testing");
            Button tempButton = net.dv8tion.jda.api.interactions.components.buttons.Button.danger("temp-button","temporary");
            Button[] buttonList = {testButton,tempButton};

            MessageCreateData message = new MessageCreateBuilder()
                    .setActionRow(ActionRow.of(buttonList).getButtons())
                    .addEmbeds(getMessageEmbed())
                    .build();

            event.getChannel().sendMessage(message).queue();

        }

    }

    @NotNull
    private MessageEmbed getMessageEmbed(){

        final String[] commandList = {"/join|make me join a voice channel",
                "/leave|make me leave the current voice channel and clear the playlist",
                "/play|play a song or a playlist",
                "/skip|skip the current song or a number of next songs",
                "/list|list all tracks in queue",
                "/pause|pause the music",
                "/resume|play the paused track",
                "/song|show info of the current song",
                "/playnow|play a track immediately",
                "/skipto|skip to a specific track in queue",
                "/clear|clear the playlist"};
        HashMap<String,String> commands = new HashMap<>();
        for(String command: commandList){
            String[] temp = command.split("[|]");
            commands.put(temp[0],temp[1]);
        }


        String url = "";
        String title = "Help center";
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

        for(String key: commands.keySet()){
            MessageEmbed.Field field = new MessageEmbed.Field(key,commands.get(key),false);
            fields.add(field);
        }


        return new MessageEmbed(url,title,description,type,timeStamp,color,thumbnail,provider,author,videoInfo,footer,image,fields);
    }
}
