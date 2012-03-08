package com.archmageinc.ExternalChat;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;


public class ExternalChat extends JavaPlugin {
	private Logger log				=	Logger.getLogger("Minecraft");
	private FileAccessor fileMan	=	new FileAccessor(this);
	private ChatListener listener	=	new ChatListener(this);
	private ExternalReader reader	=	new ExternalReader(this);
	private ChatCleaner cleaner		=	new ChatCleaner(this);
	
	@Override
	public void onDisable() {
		this.saveConfig();
		this.logMessage("Disabled");
	}

	@Override
	public void onEnable() {
		this.initialConfigCheck();
		this.getServer().getPluginManager().registerEvents(this.listener, this);
		this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, this.reader, this.getConfig().getInt("chat_delay")/50, this.getConfig().getInt("chat_delay")/50);
		this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, this.cleaner, this.getConfig().getInt("clean_delay")/50, this.getConfig().getInt("clean_delay")/50);
		this.fileMan.write(this.getConfig().getString("chat_file"), "");
		this.logMessage("Enabled");
	}
	
	private void initialConfigCheck(){
		if(!(new File(this.getDataFolder(),"config.yml").exists())){
			this.logMessage("Saving default configuration file.");
			this.saveDefaultConfig();
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args){
		if(sender instanceof Player && cmd.getName().equals("public")){
			Player p	=	(Player) sender;
			if(this.listener.isPrivate(p)){
				this.listener.removePrivate(p);
				p.sendMessage(ChatColor.DARK_PURPLE+"[Chat]"+ChatColor.WHITE+" Your messages will be broadcast externally.");
			}else{
				p.sendMessage(ChatColor.DARK_PURPLE+"[Chat]"+ChatColor.WHITE+" Your messages were already broadcast externally");
			}
			return true;

	}
		if(sender instanceof Player && cmd.getName().equals("private")){
			Player p	=	(Player) sender;
			if(!this.listener.isPrivate(p)){
				this.listener.addPrivate(p);
				p.sendMessage(ChatColor.DARK_PURPLE+"[Chat]"+ChatColor.WHITE+" Your messages will not be broadcast externally.");
			}else{
				p.sendMessage(ChatColor.DARK_PURPLE+"[Chat]"+ChatColor.WHITE+" Your messages were already not broadcast externally.");
			}
			return true;
		}
		if(!(sender instanceof Player) && (cmd.getName().equals("public") || cmd.getName().equals("private"))){
			this.logMessage("Internal / External chat may only be controlled by players");
			return false;
		}
		return false;
	}
	
	public void logMessage(String msg){
		PluginDescriptionFile pdFile	=	this.getDescription();
		log.info("["+pdFile.getName()+" "+pdFile.getVersion()+"]: "+msg);
	}
	
	public FileAccessor getFileMan(){
		return this.fileMan;
	}
	
	public String chatFile(){
		return this.getConfig().getString("chat_file");
	}
	
	public String deathName(){
		return this.getConfig().getString("death_name");
	}
	
	public int historyChat(){
		return this.getConfig().getInt("clean_age");
	}
	
	public String playerFile(){
		return this.getConfig().getString("player_file");
	}
}
