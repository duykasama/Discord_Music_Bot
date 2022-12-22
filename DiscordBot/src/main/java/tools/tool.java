package tools;

import com.sun.jndi.toolkit.url.Uri;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class tool extends ListenerAdapter {
    public EmbedBuilder getEmbedMessage(String message, String authorName){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(message);
        embedBuilder.setColor(Color.blue);
        embedBuilder.setFooter("Request by " + authorName + " at: " + dateFormat.format(date));
        return embedBuilder;
    }

    public void reply(@NotNull SlashCommandInteractionEvent event, String reply){
        String authorName = event.getMember().getEffectiveName();
        event.deferReply().queue();
        event.getHook().sendMessageEmbeds(getEmbedMessage(reply, authorName).build()).queue();
    }

    public boolean isUrl(String url){
        try{
            new URI(url);
            return true;
        }catch (URISyntaxException e){
            return false;
        }
    }

}
