package run;

import commands.BotCommands;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import javax.security.auth.login.LoginException;

public class Bot {
    public static void main(String[] args) throws InterruptedException {

        final String TOKEN = System.getenv("BOT_TOKEN");

        JDA bot = JDABuilder.createDefault(TOKEN)
                .setActivity(Activity.playing("music for my team"))
                .addEventListeners(new BotCommands())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build().awaitReady();

//        Guild guild = bot.getGuildById("937704197187600395");
        Guild guild = bot.getGuildById("907226857177493525");

        //add commands
        if(guild != null){

            guild.upsertCommand("join","make me join a voice channel")
                    .addOption(OptionType.STRING,"channel-name","a voice channel's name")
                    .queue();

            guild.upsertCommand("leave","make me leave a voice channel")
                    .queue();

            guild.upsertCommand("play","play a song or a playlist")
                    .addOption(OptionType.STRING,"link-or-query","url to a song, a playlist or enter a keyword",true)
                    .queue();

            guild.upsertCommand("pause","pause the current song")
                    .queue();

            guild.upsertCommand("resume","keep playing")
                    .queue();

            guild.upsertCommand("stop","stop playing")
                    .queue();

            guild.upsertCommand("skip","skip the current song")
                    .queue();
        }
    }

}
