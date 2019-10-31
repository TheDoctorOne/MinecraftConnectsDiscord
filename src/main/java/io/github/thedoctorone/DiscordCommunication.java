package io.github.thedoctorone;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Server;

import java.util.logging.Logger;

public class DiscordCommunication extends ListenerAdapter implements Runnable {
    static JDA MCD;
    private String channelId;
    private Logger lg;
    private Server server;
    private String serverStartMessage;
    DiscordCommunication(Server server, Logger lg, String channelId, String serverStartMessage) throws LoginException, InterruptedException {
        this.serverStartMessage = serverStartMessage;
        this.server = server;
        this.lg = lg;
        this.channelId = channelId;
        MCD = new JDABuilder(AccountType.BOT).setToken("NjM5MzQ0NTIyNDI1NTMyNDM3.Xbp6GQ.MlLb3w7QVMkwsQIte2h4AVc-bQc").setStatus(OnlineStatus.DO_NOT_DISTURB).build();
        MCD.addEventListener(this);
        MCD.getPresence().setActivity(Activity.of(ActivityType.WATCHING, "Minecraft Chat"));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getChannel().getId().equals(channelId) && !event.getAuthor().isBot())
            server.broadcastMessage("[Discord] " + event.getAuthor().getName() + " : " + event.getMessage().getContentRaw());
    }

    public void sendMessageToDiscord(String message) {
        MCD.getTextChannelById(channelId).sendMessage(message).queue();
    }

    @Override
    public void run() {
        while (true) {
            try {
                MCD.getTextChannelById(channelId).sendMessage("```css\n" + serverStartMessage + "```").queue();
                MCD.getTextChannelById(channelId).sendMessage("```css\n'Minecraft Connects Discord'\n Made by Mahmut H. Kocas \n https://www.thedoctorone.github.io```").queue();
                break;
            } catch (NullPointerException | IllegalMonitorStateException e) {
                continue;
            }
        }
    }

}