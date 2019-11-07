package io.github.thedoctorone;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Server;
import org.bukkit.command.CommandException;
import org.bukkit.command.RemoteConsoleCommandSender;
import java.util.logging.Logger;

public class DiscordCommunication extends ListenerAdapter {
    static JDA MCD;
    private Main main;
    private DiscordCommandSender dcs;
    private RemoteConsoleCommandSender dcd;
    private boolean firstConnection = true;
    private boolean sendByDiscordFullReload = false;

    private String channelId;
    private Logger lg;
    private Server server;
    private String serverStartMessage;
    private String TOKEN;
    private String permId;

    DiscordCommunication (Main main) {
        this.main = main;
        dcs = new DiscordCommandSender(this, this.main);
    }

    public void executeBot (Server server, Logger lg, String TOKEN, String channelId, String permId, String serverStartMessage) throws LoginException {
        this.TOKEN = TOKEN;
        this.serverStartMessage = serverStartMessage;
        this.server = server;
        this.lg = lg;
        this.channelId = channelId;
        this.permId = permId;
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
    public void onMessageReceived(MessageReceivedEvent event) { //When message comes from discord
        try {
            if(!permId.equals("ENTER THE ADMIN DISCORD ROLE") && event.getChannel().getId().equals(channelId) && event.getMessage().getContentRaw().trim().startsWith("!exec ") && event.getAuthor().getJDA().getRoleById(permId) != null) {
                    if(event.getMessage().getContentRaw().contains("discord full"))
                        sendByDiscordFullReload = true;
                    else if(event.getMessage().getContentRaw().contains("discord") && !event.getMessage().getContentRaw().contains("discord fast")) {
                        lg.info("true");
                        sendMessageToDiscord("```css\n" +
                                "# [Minecraft Connects Discord by Mahmut H. Kocas]\n" +
                                "# [Youtube](https://www.youtube.com/mahmutkocas)\n" +
                                "/discord : Commands\n" +
                                "/discord fast : Changes everything according to config file except Discord Bot Token\n" +
                                "/discord full : Changes everything according to config file```");
                        return;
                    }
                    server.dispatchCommand(dcs,event.getMessage().getContentRaw().replace("!exec "," ").trim());
            } else if(event.getChannel().getId().equals(channelId) && !event.getAuthor().isBot()) {
                server.broadcastMessage("[Discord] " + event.getAuthor().getName() + " : " + event.getMessage().getContentRaw()); //Mirroring Discord Chat to In-Game Chat
            }
            if(event.getMessage().getContentRaw().trim().contains("!verify")) {

            }
        } catch (CommandException ex) {
            server.dispatchCommand(dcd, event.getMessage().getContentRaw().replace("!exec "," ").trim());
            sendMessageToDiscord("Command run, but output is out of reach.");
        } catch (NumberFormatException ex) {
            lg.warning("PERM ID IS NOT RIGHT!");
        }
    }

    @Override
    public void onReady(ReadyEvent event) { //First Connection
        if(firstConnection) {
            MCD.getTextChannelById(channelId).sendMessage(serverStartMessage).queue();
            firstConnection = false;
            dcd = new DiscordConsoleDummy(this, this.main);
        } else if (sendByDiscordFullReload) {
            sendByDiscordFullReload = false;
            sendMessageToDiscord("Full Reload Successful!");
        }
    }

    public void sendMessageToDiscord(String message) {
        String[] MineCraftLanguageFilter = {"§a","§b","§c","§d","§e", "§f", "§k","§l", "§m","§n", "§o","§r", "§0","§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9"}; //Weird color thingies & next-back selections
        for(String f : MineCraftLanguageFilter)
            message = message.replaceAll(f, "");
        if(!message.isEmpty())
            MCD.getTextChannelById(channelId).sendMessage(message).queue();
    }

    public void returnLogFromConsole(String message) {
        String[] MineCraftLanguageFilter = {"§a","§b","§c","§d","§e", "§f", "§k","§l", "§m","§n", "§o","§r", "§0","§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9"}; //Weird color thingies & next-back selections
        for(String f : MineCraftLanguageFilter)
            message = message.replaceAll(f, "");
        if(!message.isEmpty())
            MCD.getTextChannelById(channelId).sendMessage(message).queue();
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setPermId(String permId) {
        this.permId = permId;
    }
}