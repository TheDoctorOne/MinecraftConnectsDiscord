package io.github.thedoctorone;

import javax.security.auth.login.LoginException;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Hello world!
 *
 */
public class App extends JavaPlugin implements Listener {
    private DiscordCommunication dc;
    private String playerJoin = "&p just joined to server!";
    private String playerLeft = "&p just leaved the server!";
    String boldStart = "**";
    String squareParOpen = "[";
    String squareParClose = "]";
    String boldEnd = "**";

    @Override
    public void onEnable() {
        getLogger().info("Hello, SpigotMC!");
        getServer().getPluginManager().registerEvents(this, this);
        try {
            dc = new DiscordCommunication(getServer(), getLogger(), "639345173108883458", "Server Started!");
            new Thread(dc).run();
        } catch (LoginException | InterruptedException e) {
            getLogger().warning("Couldn't join to discord!");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("See you again, SpigotMC!");
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
        dc.sendMessageToDiscord(boldStart + playerLeft.replace("&p", player) + boldEnd);
    }
}
