# MinecraftConnectsDiscord - Discord Integration for Minecraft! 
Connect your Minecraft in game chat to Discord!

Minecraft and Discord chats are together! This piece of plugin integrates your server's in-game chat to your defined discord text channel. Simple.

Config file allows you to change,

* Player Join Message
* Player Disconnect Message
* Server Start Message
* Server Stop Message
* Discord Server Invite Link

Commands are (only for OPs):

* /discord fast : Fast Reload without changing the Bot Token
* /discord full : Full Reload, restarts bot

If default player tries "/discord", he will receive the discord invitation link which is configurable at config file!
Config file located at "/discord/config.ini", it generates itself at first run.

This plugin also allows you to execute commands via Discord chat.
Only Authorized People can execute commands. To be able execute command you have to create a Discord Role, and copy its ID then paste it to relative place in the config file. And assign the role to yourself.

How can I execute commands?

Go to the Discord Text Channel which is watching by your bot. Type "!exec ". For vanilla commands, commands will execute but you can't see the output. You have to check console to see if it succeed or not.

In Ex. of usage

*    !exec kill MahmudKocas ---> You can't see if this succeed or not.
*    !exec help --> Output will be shown.
*    !exec help 2
*    !exec discord ---> Output will be shown.
*    !exec discord full

Syncing Minecraft Account with Discord Account

Usage is as follows:
User gets the **sync code** with
***/discord sync***
User verifies his sync code at discord with
***!verify sync-code***
**OPs** can remove sync with the following command
***/discord sync remove <minecraft-username>***

If user gets banned from Discord than he will also gets banned from Minecraft. And if user is online at the server and he gets banned from server while online, he will also gets banned from Discord. There is a configuration option at config file which allows you to toggle this.

From discord if authorized people type "!exec discord sync" bot will send a private message which contains list of the synchronized players.

To be able to use this in your Spigot Server,

* You have to create a Discord Bot via https://discordapp.com/developers/ (its free)
* You have to have a Discord Server.

Get your Discord Bot's TOKEN and invite the bot to your Discord Server. Put the TOKEN in to the config file.
Open Discord's Developer Mode. Right click the text channel which you want to integrate with Minecraft Chat. Minecraft Chat will be mirrored to that text channel. And click "Copy ID", also put it in to config file. 

Links
> [SpigotMC Link](https://www.spigotmc.org/resources/minecraft-chat-connects-to-discord-chat.72427/ "Click to see Spigot Page!")<br>
> [Turkish - Forum Gamer](https://forum.gamer.com.tr/indirmeler/minecraft-connects-to-discord.169/ "Forum Gamer'da ki Konumuz")<br>
> [Turkish - MCTR](https://www.mc-tr.com/konu/minecraft-connects-to-discord-bir-spigot-plugini.69826/ "MC-TR Forumundaki Konumuz")
