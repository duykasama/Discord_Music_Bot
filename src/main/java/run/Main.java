package run;

import commands.Botcommands;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {
    public static void main(String[] args) throws Exception {
        final String TOKEN = System.getenv("BOT_TOKEN");
        JDA bot = JDABuilder.createDefault(TOKEN)
                .addEventListeners(new Botcommands())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build().awaitReady();

        Guild guild = bot.getGuildById(937704197187600395L);

        if(guild != null){

            guild.upsertCommand("join","make me join a voice channel")
                    .addOption(OptionType.STRING,"channel-name","name of channel to join", false)
                    .queue();

            guild.upsertCommand("leave","make me leave the current voice channel")
                    .queue();
        }


    }
}