package commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceStreamEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class GetEmbed extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        for (MessageEmbed embed : event.getMessage().getEmbeds()){
            try{
                String message = "[" + embed.getSiteProvider().getName() + "]" + "(" + embed.getSiteProvider().getUrl() + ")";
//                event.getChannel().sendMessage(message);
                break;
            }catch (Exception ignored){

            }
        }

    }
}
