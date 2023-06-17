package utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;


import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

    @NotNull
    private static EmbedBuilder getEmbedFormat(String authorName, String message){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(message)
                .setColor(Color.blue)
                .setFooter(getFooter(authorName));
        return embedBuilder;
    }
    @NotNull
    public static String getFooter(String authorName){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String time = simpleDateFormat.format(date);
        return "Request by " + authorName + " at " + time;
    }
    public static void sendMessageEmbeds(@NotNull SlashCommandInteractionEvent event, String message){
        String authorName = event.getMember().getEffectiveName();
        try{
            event.deferReply().queue();
            event.getHook().sendMessageEmbeds(getEmbedFormat(authorName, message).build()).queue();
            return;
        }catch (Exception e){
            System.out.println(e);
        }
        event.getChannel().sendMessageEmbeds(getEmbedFormat(authorName,message).build()).queue();
    }
    public static void sendMessageEmbeds(@NotNull MessageReceivedEvent event, String message){
        String authorName = event.getMember().getEffectiveName();
        event.getChannel().sendMessageEmbeds(getEmbedFormat(authorName,message).build()).queue();
    }
    public static boolean isUrl(String url){
        try{
            new URI(url);
            return true;
        }catch(URISyntaxException e){
            return false;
        }
    }
    @NotNull
    @Contract(pure = true)
    public static String toMaskedLink(String text, String url){
        return "[" + text + "]" + "(" + url + ")";
    }
    @NotNull
    @Contract(pure = true)
    public static String getTrackDuration(long milisecond){
        long SECONDS = (milisecond/1000) % 60;
        long MINUTES = (milisecond/1000) / 60;
        if(MINUTES >= 60){
            MINUTES %= 60;
        }
        long HOURS = ((milisecond/1000) / 60) / 60;
        if(HOURS != 0){
            return ((HOURS<10)?"0"+HOURS:HOURS) + ":" + ((MINUTES<10)?"0"+MINUTES:MINUTES) + ":" + ((SECONDS<10)?"0"+SECONDS:SECONDS);
        }else{
            return ((MINUTES<10)?"0"+MINUTES:MINUTES) + ":" + ((SECONDS<10)?"0"+SECONDS:SECONDS);
        }

    }
    public static String getDurationBar(){
        return "";
    }
}
