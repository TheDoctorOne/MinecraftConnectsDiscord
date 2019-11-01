package io.github.thedoctorone;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class CommandReload implements CommandExecutor {
    private DiscordCommunication dc;
    private Main main;
    private String fast = "fast";
    private String full = "full";
    private String helpMessage = "Minecraft Connects Discord by Mahmut H. Kocas\n" +
            "/discord : Commands\n" +
            "/discord fast : Changes everything according to config file except Discord Bot Token\n" +
            "/discord full : Changes everything according to config file\n" +
            "";

    CommandReload (Main main, DiscordCommunication dc) {
        this.dc = dc;
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /*
        * Args = "" -> Help
        * Args[0] -> Fast Reload - Only Reloads the messages
        * Args[0] -> Full Reload - Reloads whole bot
        * */

        try {
            if(args[0].equals(fast)) { //Fast
                fastReload(sender);
            } else if(args[0].equals(full)) { //Full
                fullReload(sender);
            }   else if(args[0] == "") { //Empty - Sends help
                sendHelp(sender);
            }
        } catch (IOException | LoginException e) {
            main.getLogger().warning("ERROR - CONFIG READ - IO EXCEPTION");
            sender.sendMessage("ERROR - CONFIG READ - IO EXCEPTION");
        }

        return false;
    }

    private void fastReload(CommandSender sender) throws IOException { //Command : Fast
        if(sender instanceof Player) { //Player
            Player player = (Player) sender;
            if(player.isOp()) { //Player perm control
                main.ConfigThingies();
                dc.setChannelId(main.getChannelID());
            }
        } else if (sender instanceof ConsoleCommandSender) { //Console
            try {
                main.ConfigThingies();
                dc.setChannelId(main.getChannelID());
            } catch (IOException e) {
                main.getLogger().warning("ERROR - CONFIG READ - IO EXCEPTION");
            }
        }
    }

    private void fullReload(CommandSender sender) throws IOException, LoginException { //Command : Full
        if(sender instanceof Player) { //Player
            Player player = (Player) sender;
            if(player.isOp()) { //Player perm control
                main.ConfigThingies();
                dc.reloadBot(main.getTOKEN());
                dc.setChannelId(main.getChannelID());
            }
        } else if (sender instanceof ConsoleCommandSender) { //Console
            main.ConfigThingies();
            dc.reloadBot(main.getTOKEN());
            dc.setChannelId(main.getChannelID());
        }
    }

    private void sendHelp(CommandSender sender) {
        if (sender instanceof Player) { //Player
            Player player = (Player) sender;
            if (player.isOp()) { //Player perm control
                sender.sendMessage(helpMessage);
            } else { //Sends discord invite link
            }
        } else if (sender instanceof ConsoleCommandSender) { //Console
            sender.sendMessage(helpMessage);
        }
    }
}
