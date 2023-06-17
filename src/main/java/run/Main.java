package run;

import commands.BotCommands;
import commands.ButtonListener;
import commands.GetEmbed;
import commands.Test;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {
    public static void main(String[] args) throws Exception {
        final String TOKEN = System.getenv("BOT_TOKEN");
        JDA bot = JDABuilder.createDefault(TOKEN)
                .addEventListeners(new BotCommands())
                .addEventListeners(new GetEmbed())
                .addEventListeners(new ButtonListener(), new Test())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build().awaitReady();

        Guild guild = bot.getGuildById(937704197187600395L);

        if(guild != null){

            guild.upsertCommand("join","make me join a voice channel")
                    .addOption(OptionType.STRING,"channel-name","name of channel to join", false)
                    .queue();

            guild.upsertCommand("leave","make me leave the current voice channel")
                    .queue();

            guild.upsertCommand("play","play a track, a playlist or search for a keyword")
                    .addOption(OptionType.STRING,"link-or-query","link to a track or a query",true)
                    .queue();

            guild.upsertCommand("skip","skip the current song")
                    .addOptions(new OptionData(OptionType.INTEGER,"quantity","number of tracks to skip")
                            .setRequiredRange(1,20))
                    .queue();

            guild.upsertCommand("list","list all songs in queue")
                    .queue();

            guild.upsertCommand("pause","temporarily stop playing")
                    .queue();

            guild.upsertCommand("resume","resume playing")
                    .queue();

            guild.upsertCommand("playing","show the current song's info")
//                    setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                    .queue();


            guild.upsertCommand("playnow","play a track immediately")
                    .addOption(OptionType.STRING,"url-or-query","url to a song, playlist or a keyword", true)
                    .queue();

            guild.upsertCommand("clear","clear playlist")
                    .queue();

            guild.upsertCommand("skipto","skip to a specific track")
                    .addOptions(new OptionData(OptionType.INTEGER,"index","position of track",true)
                            .setRequiredRange(1,20))
                    .queue();

            guild.upsertCommand("test","for testing")
                    .queue();

            guild.upsertCommand("test1","for testing spotify api")
                    .queue();
        }


    }
}