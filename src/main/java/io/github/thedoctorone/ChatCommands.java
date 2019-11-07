package io.github.thedoctorone;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class ChatCommands implements CommandExecutor {
    private ArrayList<ArrayList<String>> CurrentSyncingMemberList; //Arg.get(0) = uuid, Arg.get(1) = randomInt
    private ArrayList<String> SyncedPeopleList;
    private DiscordCommunication dc;
    private Main main;
    private SyncFileOperation sfo;
    private String fast = "fast";
    private String full = "full";
    private String sync = "sync";
    private String syncMessage = "Enter the number to discord chat. Example:\n!verify yourcode\nSync Code : &code";
    private String helpMessage = "\n**************************************************************\n" +
            "Minecraft Connects Discord by Mahmut H. Kocas\n" +
            "https://www.youtube.com/mahmutkocas\n" +
            "/discord : Commands\n" +
            "/discord fast : Changes everything according to config file except Discord Bot Token\n" +
            "/discord full : Changes everything according to config file\n" +
            "/discord sync : Sync your minecraft account to your discord account!\n" +
            "/discord sync remove : Remove the Sync\n" +
            "**************************************************************";

    ChatCommands(Main main, DiscordCommunication dc, SyncFileOperation sfo) {
        this.dc = dc;
        this.main = main;
        this.sfo = sfo;
        CurrentSyncingMemberList = new ArrayList<>();
    }

    public ArrayList<String> getSyncedPeopleList(){
        return SyncedPeopleList;
    }


    public void setSyncedPeopleList(ArrayList<String> syncedPeopleList) {
        SyncedPeopleList = syncedPeopleList;
        sfo.writeSyncFile(SyncedPeopleList);
    }

    public ArrayList<ArrayList<String>> getCurrentSyncingMemberList() {
        return CurrentSyncingMemberList;
    }

    public void removeFromRequestList(ArrayList<ArrayList<String>> toRemove){
        CurrentSyncingMemberList.removeAll(toRemove);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /*
        * Args[0] = null -> Help
        * Args[0] -> Fast Reload - Only Reloads the messages
        * Args[0] -> Full Reload - Reloads whole bot
        * Args[0] -> sync - Sends random 4 digit integer for verifying
        * */
        try {
             if(args.length == 0) { //Empty - Sends help
                sendHelp(sender);
                return true;
            } else if(args[0].equals(full)) { //Full
                fullReload(sender);
                return true;
            } else if(args[0].equals(fast)) { //Fast
                 fastReload(sender);
                return true;
            } else if(args[0].equals(sync)) { //Sync
                 syncDiscord(sender, args);
             } else {
                 sender.sendMessage("Wrong usage.");
             }
        } catch (IOException | LoginException e) {
            main.getLogger().warning("ERROR - CONFIG READ - IO EXCEPTION");
            sender.sendMessage("ERROR - CONFIG READ - IO EXCEPTION");
            return false;
        }
        return false;
    }

    private void fastReload(CommandSender sender) throws IOException { //Command : Fast
        if(sender instanceof Player) { //Player
            Player player = (Player) sender;
            if(player.isOp()) { //Player perm control
                sender.sendMessage("Fast Reload Starting...");
                main.ConfigThingies();
                dc.setChannelId(main.getChannelID());
                dc.setPermId(main.getDiscordPerm());
                sender.sendMessage("Fast Reload Successful!");
            } else {
                sender.sendMessage("Wrong usage.\n/discord");
            }
        } else if (sender instanceof ConsoleCommandSender) { //Console
            try {
                sender.sendMessage("Fast Reload Starting...");
                main.ConfigThingies();
                dc.setChannelId(main.getChannelID());
                dc.setPermId(main.getDiscordPerm());
                sender.sendMessage("Fast Reload Successful!");
            } catch (IOException e) {
                main.getLogger().warning("ERROR - CONFIG READ - IO EXCEPTION");
            }
        }
    }

    private void fullReload(CommandSender sender) throws IOException, LoginException { //Command : Full
        if(sender instanceof Player) { //Player
            Player player = (Player) sender;
            if(player.isOp()) { //Player perm control
                sender.sendMessage("Full Reload Starting...");
                main.ConfigThingies();
                dc.reloadBot(main.getTOKEN());
                dc.setChannelId(main.getChannelID());
                dc.setPermId(main.getDiscordPerm());
                sender.sendMessage("Full Reload Successful!");
            } else {
                sender.sendMessage("Wrong usage.\n/discord");
            }
        } else if (sender instanceof ConsoleCommandSender) { //Console
            sender.sendMessage("Full Reload Starting...");
            main.ConfigThingies();
            dc.reloadBot(main.getTOKEN());
            dc.setChannelId(main.getChannelID());
            dc.setPermId(main.getDiscordPerm());
            sender.sendMessage("Full Reload Successful!");
        }
    }

    private void sendHelp(CommandSender sender) {
        if (sender instanceof Player) { //Player
            Player player = (Player) sender;
            if (player.isOp()) { //Player perm control
                sender.sendMessage(helpMessage + "\nINVITE LINK : " + main.getDiscordInviteLink());
            } else { //Sends discord invite link
                if(!main.getDiscordInviteLink().equals("INVITE LINK OF YOUR DISCORD")) {
                    sender.sendMessage("Discord : " + main.getDiscordInviteLink());
                }
            }
        } else if (sender instanceof ConsoleCommandSender) { //Console
            sender.sendMessage(helpMessage);
        }
    }

    private void syncDiscord(CommandSender sender, String args[]) { //File(SyncedPeopleList) uuid:userDiscordId
        try {
            if (args[1] != null)
                if ((sender.isOp() || sender instanceof ConsoleCommandSender) && args[1].equals("remove")) {
                    try {
                        String playerName = args[2];
                        String UUID = main.getServer().getPlayerExact(playerName).getUniqueId().toString();
                        ArrayList<String> toRemove = new ArrayList<>();
                        for (String s : SyncedPeopleList) {
                            if (s.trim().split(":")[0].trim().equals(UUID)) {
                                toRemove.add(s);
                                break;
                            }
                        }
                        SyncedPeopleList.removeAll(toRemove);
                        sfo.writeSyncFile(SyncedPeopleList);
                        sender.sendMessage("Sync removed. Sync Information = " + toRemove.get(0));
                        return;
                    } catch (IndexOutOfBoundsException ex) {
                        sender.sendMessage("Enter the username you want to broke the sync of.\n/discord sync remove <playername>");
                        return;
                    }
                }
        } catch (IndexOutOfBoundsException ex) {

        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String uuid  = player.getUniqueId().toString().trim();
            for(String s : SyncedPeopleList) { // Already Synced
                if(s.split(":")[0].trim().equals(uuid)) {
                    player.sendMessage("You already synced your account!");
                    return;
                }
            } //END FOR - ALREADY SYNC
            for(ArrayList<String> arg : CurrentSyncingMemberList) { // 0 = uuid - 1 = random | Already asked for a code
                if(arg.get(0).trim().equals(uuid)) {
                    player.sendMessage("Sync Code : " + arg.get(1));
                    return;
                }
            } //END FOR - ALREADY ASKED FOR CODE

            //First Ask for Sync
            Random rndGen = new Random();
            int rnd = rndGen.nextInt() % 10000;
            ArrayList<String> addReq = new ArrayList<>();
            addReq.add(0 , uuid);
            addReq.add(1, rnd + "");
            addReq.add(2, player.getName());
            CurrentSyncingMemberList.add(addReq);
            player.sendMessage(syncMessage.trim().replaceAll("&code", rnd +""));
        }
    }
}
