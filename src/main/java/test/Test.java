package test;

import lavaplayer.LavaPlayer;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Test extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().equals("test message")){
            LavaPlayer lavaPlayer = new LavaPlayer().getINSTANCE();
//            lavaPlayer.loadAndPlay(event, "https://www.youtube.com/watch?v=DpcJP-wQWGI");
        }
    }
}
