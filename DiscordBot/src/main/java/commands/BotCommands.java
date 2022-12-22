package commands;

import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import tools.tool;

import java.util.List;

public class BotCommands extends ListenerAdapter {

    CommandList commands = new CommandList();
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if(event.getName().equals("join")){
            commands.join(event);
        }
        else if(event.getName().equals("leave")){
            commands.leave(event);
        }
        else if(event.getName().equals("play")){
            commands.play(event);
        }
        else if(event.getName().equals("pause")){
            commands.pause(event);
        }
        else if(event.getName().equals("resume")){
            commands.resume(event);
        }
        else if(event.getName().equals("stop")){
            commands.stop(event);
        }
        else if(event.getName().equals("skip")){
            commands.skip(event);
        }
    }

}
