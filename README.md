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

To be able to use this in your Spigot Server,

* You have to create a Discord Bot via https://discordapp.com/developers/ (its free)
* You have to have a Discord Server.

Get your Discord Bot's TOKEN and invite the bot to your Discord Server. Put the TOKEN in to the config file.
Open Discord's Developer Mode. Right click the text channel which you want to integrate with Minecraft Chat. Minecraft Chat will be mirrored to that text channel. And click "Copy ID", also put it in to config file. 

Links
> [SpigotMC Link](https://www.spigotmc.org/resources/minecraft-chat-connects-to-discord-chat.72427/ "Click to see Spigot Page!")<br>
> [Turkish - Forum Gamer](https://forum.gamer.com.tr/konu/minecraft-connects-to-discord-bir-spigot-plugini.436127 "Forum Gamer'da ki Konumuz")<br>
> [Turkish - MCTR](https://www.mc-tr.com/konu/minecraft-connects-to-discord-bir-spigot-plugini.69826/ "MC-TR Forumundaki Konumuz")
