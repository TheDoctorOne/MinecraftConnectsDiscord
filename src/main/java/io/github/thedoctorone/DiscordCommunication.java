package io.github.thedoctorone;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Server;

import java.util.logging.Logger;

public class DiscordCommunication extends ListenerAdapter {
    static JDA MCD;
    private String channelId;
    private Logger lg;
    private Server server;
    private String serverStartMessage;
    private String TOKEN;

    DiscordCommunication () {

    }

    public void executeBot (Server server, Logger lg, String TOKEN, String channelId, String serverStartMessage) throws LoginException {
        this.TOKEN = TOKEN;
        this.serverStartMessage = serverStartMessage;
        this.server = server;
        this.lg = lg;
        this.channelId = channelId;
        runBot(TOKEN);
    }

    public void reloadBot(String TOKEN) throws LoginException {
        MCD.shutdownNow();
        this.TOKEN = TOKEN;
        runBot(TOKEN);
    }

    public void runBot(String TOKEN) throws LoginException {
        MCD = new JDABuilder(AccountType.BOT).setToken(TOKEN).setStatus(OnlineStatus.DO_NOT_DISTURB).build();
        MCD.addEventListener(this);
        MCD.getPresence().setActivity(Activity.of(ActivityType.WATCHING, "Minecraft Chat"));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getChannel().getId().equals(channelId) && !event.getAuthor().isBot())
            server.broadcastMessage("[Discord] " + event.getAuthor().getName() + " : " + event.getMessage().getContentRaw());
    }
    private boolean first = true;
    @Override
    public void onReady(ReadyEvent event) {
        if(first) {
            MCD.getTextChannelById(channelId).sendMessage(serverStartMessage).queue();
            first = false;
        }
    }

    public void sendMessageToDiscord(String message) {
        MCD.getTextChannelById(channelId).sendMessage(message).queue();
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}