package io.github.thedoctorone;

import javax.security.auth.login.LoginException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class Main extends JavaPlugin implements Listener {
    private DiscordCommunication dc;
    private ChatCommands chatCommands;
    private SyncFileOperation sfo;
    private String VERSION = "0.85";
    private String playerJoin = "&p just joined to server!";
    private String playerLeft = "&p just leaved the server!";
    private String ServerStart = "Server Started!";
    private String ServerStop = "Server Stopped!";
    private String channelID = "ENTER YOUR CHANNEL ID HERE";
    private String discordInviteLink = "INVITE LINK OF YOUR DISCORD";
    private String discordPerm = "ENTER THE ADMIN DISCORD ROLE";
    private String TOKEN = "ENTER YOUR TOKEN HERE";
    private String syncBan = "true";
    private String syncRoleID = "ENTER YOUR SYNC ROLE ID";
    private String boldStart = "**";
    private String squareParOpen = "[";
    private String squareParClose = "]";
    private String boldEnd = "**"; //why the f* did i defined this again. wtf
    public static boolean DONT_BAN = false;
    public static boolean syncRoleID_GRANTED = false;

    @Override
    public void onEnable() {
        getLogger().info("Hello, Minecraft Connects to Discord is here! by Mahmut H. Kocas");
        try {
            ConfigThingies();
            sfo = new SyncFileOperation();
            getCommand("discord").setExecutor(chatCommands = new ChatCommands(this, dc = new DiscordCommunication(this, sfo), sfo)); //Adding discord command
            dc.setChatCommands(chatCommands);
            chatCommands.setSyncedPeopleList(sfo.readSyncFile()); //Reading the Synced People
        } catch (IOException e) {
            getLogger().warning("CAN'T INTERACT WITH 'discord/' PATH!");
        }
        if(!TOKEN.equals("ENTER YOUR TOKEN HERE"))
            try {
                dc.executeBot(getServer(), getLogger(), TOKEN, channelID, discordPerm, ServerStart);
                getServer().getPluginManager().registerEvents(this, this);
            } catch (LoginException e) {
                getLogger().warning("Couldn't join to discord! Check your token or Internet Connection!");
            }
    }

    @Override
    public void onDisable() {
        dc.sendMessageToDiscord(ServerStop);
        getLogger().info("MCD OUT!");
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if(!event.getMessage().startsWith("/")) {
            String user = event.getPlayer().getDisplayName();
            String message = event.getMessage();
            dc.sendMessageToDiscord(boldStart + squareParOpen + user + squareParClose + boldEnd + " " + message);
        }
    }

    @EventHandler
    public void playerJoinEvent(PlayerLoginEvent event) {
        String player = event.getPlayer().getDisplayName();
        dc.sendMessageToDiscord(boldStart + playerJoin.replace("&p", player) + boldEnd);
    }

    @EventHandler
    public void playerLeftEvent(PlayerQuitEvent event) {
        String player = event.getPlayer().getDisplayName();
        if(event.getPlayer().isBanned() && !DONT_BAN) {
            DONT_BAN = true;
            for(String args : chatCommands.getSyncedPeopleList()) {
                String UUID = args.split(":")[0].trim();
                String dcID = args.split(":")[1].trim();
                if(event.getPlayer().getUniqueId().toString().trim().equals(UUID)) {
                    DiscordCommunication.MCD.getTextChannelById(channelID).getGuild().ban(dcID, 0).queue();
                    dc.sendMessageToDiscord(player + " is banned. And his discord account named : " + DiscordCommunication.MCD.getUserById(dcID).getName() + " also banned from discord server");
                }
            }
            DONT_BAN = false;
        } else {
            dc.sendMessageToDiscord(boldStart + playerLeft.replace("&p", player) + boldEnd);
        }
    }

    @EventHandler
    public void ServerCommandEvent(ServerCommandEvent event) {
        String message = event.getCommand();
        String user = "Server";
        if(message.startsWith("say ")) {
            message = message.replace("say", " ").trim();
            dc.sendMessageToDiscord("*" + boldStart + squareParOpen + user + squareParClose + message + boldEnd + "* ");
        }
    }

    public void ConfigThingies () throws IOException {
        File folder = new File("discord");
        if(!folder.exists())
            folder.mkdir();

        File config = new File("discord/config.ini");
        if(!config.exists()) {
            config.createNewFile();
            createConfigFile(config);
            return;
        }
        FileReader fr = new FileReader(config); //Reading the config
        BufferedReader br = new BufferedReader(fr);
        String temp = "";
        boolean CFG_VER_EQ = true;
        while((temp = br.readLine()) != null) {
            if(temp.startsWith("Discord Bot Token=")) {
                TOKEN = temp.replaceFirst("Discord Bot Token="," ").trim();
            }
            if(temp.startsWith("Discord Channel ID=")) {
                channelID = temp.replaceFirst("Discord Channel ID="," ").trim();
            }
            if(temp.startsWith("Player Join Message=")) {
                playerJoin = temp.replaceFirst("Player Join Message="," ").trim();
            }
            if(temp.startsWith("Player Disconnect Message=")) {
                playerLeft = temp.replaceFirst("Player Disconnect Message="," ").trim();
            }
            if(temp.startsWith("Server Start Message=")) {
                ServerStart = temp.replaceFirst("Server Start Message="," ").trim();
            }
            if(temp.startsWith("Server Stop Message=")) {
                ServerStop = temp.replaceFirst("Server Stop Message="," ").trim();
            }
            if(temp.startsWith("Discord Invite Link=")) {
                if(!temp.replaceFirst("Discord Invite Link="," ").trim().equals(discordInviteLink)) {
                    discordInviteLink = temp.replaceFirst("Discord Invite Link="," ").trim();
                }
            }
            if(temp.startsWith("Discord Admin Role ID=")) {
                if(!temp.replaceFirst("Discord Admin Role ID="," ").trim().equals(discordPerm)) {
                    discordPerm = temp.replaceFirst("Discord Admin Role ID="," ").trim();
                }
            }
            if(temp.startsWith("Sync ban with Discord=")) {
                if(!temp.replaceFirst("Sync ban with Discord="," ").trim().equals(syncBan)) {
                    syncBan = temp.replaceFirst("Sync ban with Discord="," ").trim();
                    if(syncBan.equals("false")) {
                        DONT_BAN = true;
                    } else if(syncBan.equals("true")) {
                        DONT_BAN = false;
                    }
                }
            }
            if(temp.startsWith("Sync Role ID=")) {
                if(!temp.replaceFirst("Sync Role ID=", " ").trim().equals(syncRoleID)) {
                    syncRoleID = temp.replaceFirst("Sync Role ID=", " ").trim();
                    syncRoleID_GRANTED = true;
                }
            }
            if(temp.startsWith("CFG-VERSION=")) {
                if(!temp.replaceFirst("CFG-VERSION="," ").trim().equals(VERSION)) {
                    CFG_VER_EQ = false;
                }
            }
        }
        if (!CFG_VER_EQ) {
            createConfigFile(config);
        }
        br.close();
        fr.close();
    }

    private void createConfigFile (File config) throws IOException {
        FileWriter fw = new FileWriter(config); //Creating/Rewriting the config
        BufferedWriter bw = new BufferedWriter(fw);
        String configFile = "" +
                "#Token of your Bot\n" +
                "Discord Bot Token= " + TOKEN + "\n" +
                "#Discord Channel that our bot will mirror the Minecraft chat\n" +
                "Discord Channel ID= " + channelID + "\n" +
                "# At discord chat you can use '!exec <commands>' to run commands, enter the role id. \n" +
                "# To be able to obtain the Role ID, go server settings than Roles tab, right click the role you want to authorize and copy ID, than paste it here!\n" +
                "Discord Admin Role ID= " + discordPerm + "\n" +
                "# Invite Link of your Discord's\n" +
                "Discord Invite Link= " + discordInviteLink +"\n" +
                "Sync ban with Discord= " + syncBan + "\n" +
                "Player Join Message= " + playerJoin +"\n" +
                "Sync Role ID= " + syncRoleID + "\n" + 
                "Player Disconnect Message= "+ playerLeft + "\n" +
                "Server Start Message= "+ ServerStart + "\n" +
                "Server Stop Message= " + ServerStop + "\n" +
                "CFG-VERSION= " + VERSION;
        fw.write(configFile);
        bw.close();
        fw.close();
    }

    public String getTOKEN() {
        return TOKEN;
    }

    public String getChannelID() {
        return channelID;
    }

    public String getDiscordInviteLink() {
        return discordInviteLink;
    }

    public String getDiscordPerm () {
        return discordPerm;
    }

    public String getSyncRoleID() {
        return syncRoleID;
    }

    public ChatCommands getChatCommands() {
        return chatCommands;
    }

    public SyncFileOperation getSfo() {
        return sfo;
    }
}
