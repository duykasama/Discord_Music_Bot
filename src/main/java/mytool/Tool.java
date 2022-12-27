package mytool;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;


import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tool {

    private static EmbedBuilder getEmbedFormat(String authorName, String message){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(message)
                .setColor(Color.blue)
                .setFooter(getFooter(authorName));
        return embedBuilder;
    }
    private static String getFooter(String authorName){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String time = simpleDateFormat.format(date);
        return "Request by " + authorName + " at " + time;
    }
    public static void sendMessageEmbeds(SlashCommandInteractionEvent event, String message){
        String authorName = event.getMember().getEffectiveName();
        try{
            event.deferReply().queue();
            event.getHook().sendMessageEmbeds(getEmbedFormat(authorName, message).build()).queue();
            return;
        }catch (ErrorResponseException e){
            System.out.println(e);
        }
        event.getChannel().sendMessageEmbeds(getEmbedFormat(authorName,message).build()).queue();
    }
}
