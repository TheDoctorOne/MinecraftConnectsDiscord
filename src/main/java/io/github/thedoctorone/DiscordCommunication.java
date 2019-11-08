package io.github.thedoctorone;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Server;
import org.bukkit.command.CommandException;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.logging.Logger;

public class DiscordCommunication extends ListenerAdapter {
    static JDA MCD;
    private Main main;
    private ChatCommands chatCommands;
    private SyncFileOperation sfo;
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

    DiscordCommunication (Main main, SyncFileOperation sfo) {
        this.main = main;
        this.sfo = sfo;
        dcs = new DiscordCommandSender(this, this.main);
    }

    public void setChatCommands(ChatCommands chatCommands) {
        this.chatCommands = chatCommands;
    }

    public void executeBot (Server server, Logger lg, String TOKEN, String channelId, String permId, String serverStartMessage) throws LoginException {
        this.TOKEN = TOKEN;
        this.serverStartMessage = serverStartMessage;
        this.server = server;
        this.lg = lg;
        this.channelId = channelId;
        this.permId = permId;
        runBot(this.TOKEN);
    }

    public void reloadBot(String TOKEN) throws LoginException {
        MCD.shutdownNow();
        this.TOKEN = TOKEN;
        runBot(this.TOKEN);
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
                else if (event.getMessage().getContentRaw().contains ("discord sync")) {
                    String toSend = "";
                    for(String s : chatCommands.getSyncedPeopleList()) {
                        toSend += s;
                    }
                    event.getAuthor().openPrivateChannel().complete().sendMessage("To be able to see who is the user of the UUID : `<@DiscordID>`\n```css\nUUID:DiscordID\n" + toSend + "\n```").queue();
                    return;
                } else if(event.getMessage().getContentRaw().equals("discord")) {
                    sendMessageToDiscord("```css\n" +
                            "# [Minecraft Connects Discord by Mahmut H. Kocas]\n" +
                            "# [Youtube](https://www.youtube.com/mahmutkocas)\n" +
                            "/discord : Commands\n" +
                            "/discord fast : Changes everything according to config file except Discord Bot Token\n" +
                            "/discord full : Changes everything according to config file\n" +
                            "/discord sync : Sends the current synced list\n" +
                            "/discord sync remove : Remove the Sync```");
                    return;
                }
                server.dispatchCommand(dcs,event.getMessage().getContentRaw().replace("!exec "," ").trim());
            } // END IF FOR ADMINS
            if(event.getMessage().getContentRaw().trim().startsWith("!verify ") && !event.getAuthor().isBot()) { //Handling the Verify Request
                for(ArrayList<String> args : chatCommands.getCurrentSyncingMemberList()) {
                    String UUID = args.get(0);
                    String rnd = args.get(1);
                    if(event.getMessage().getContentRaw().replace("!verify ","").trim().equals(rnd)){
                        ArrayList<ArrayList<String>> arr = new ArrayList<>();
                        arr.add(args);
                        chatCommands.removeFromRequestList(arr); //Removing the request from queue
                        String toAdd = UUID + ":" + event.getAuthor().getId();
                        ArrayList<String> temp = chatCommands.getSyncedPeopleList();
                        temp.add(toAdd);
                        chatCommands.setSyncedPeopleList(temp);
                        try {
                            if (event.getGuild().getSelfMember().hasPermission(Permission.NICKNAME_MANAGE) && event.getGuild().getSelfMember().canInteract(event.getMember()))
                                MCD.getTextChannelById(channelId).getGuild().getMemberById(event.getAuthor().getId()).modifyNickname(args.get(2)).queue();
                            else
                                lg.warning("Bot's Permission is not enough to modify!");
                        } catch (Exception e) {
                            lg.warning("Bot's Permission is not enough to modify!");
                        }
                        sendMessageToDiscord(event.getAuthor().getName() + " successfully synced!");
                    }
                }

            } else if(event.getChannel().getId().equals(channelId) && !event.getAuthor().isBot()) {
                server.broadcastMessage("[Discord] " + event.getAuthor().getName() + " : " + event.getMessage().getContentRaw()); //Mirroring Discord Chat to In-Game Chat
            }


        } catch (CommandException ex) {
            server.dispatchCommand(dcd, event.getMessage().getContentRaw().replace("!exec "," ").trim());
            sendMessageToDiscord("Command run, but output is out of reach. Check console.");
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

    @Override
    public void onGuildBan(GuildBanEvent event){
        if(!Main.DONT_BAN) {
            Main.DONT_BAN = true;
            String username = event.getUser().getName();
            String userID = event.getUser().getId();
            for (String arg : chatCommands.getSyncedPeopleList()) {
                String[] args = arg.split(":");
                String UUID = args[0];
                String DiscordID = args[1];
                if (DiscordID.trim().equals(userID)) {
                    Player player = (Player) main.getServer().getOfflinePlayer(java.util.UUID.fromString(UUID));
                    String playerName = player.getName();
                    if (player.isBanned()) {
                        sendMessageToDiscord(username + " is banned from the discord. His account at Minecraft named " + playerName + " is already been banned.");
                    } else {
                        server.dispatchCommand(dcd, "ban " + playerName);
                        sendMessageToDiscord(username + " is banned from the discord. And the minecraft account of his called " + playerName + " also banned.");
                    }
                    return;
                }
            }
            Main.DONT_BAN = false;
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