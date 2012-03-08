package com.archmageinc.ExternalChat;

import java.util.ArrayList;
import java.util.Date;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.json.simple.JSONObject;


public class ChatListener implements Listener {
	
	private ExternalChat plugin					=	null;
	private ArrayList<Player> privatePlayers	=	new ArrayList<Player>();
	
	public ChatListener(ExternalChat plugin){
		this.plugin	=	plugin;
	}
	
	public void addPrivate(Player p){
		this.privatePlayers.add(p);
	}
	
	public void removePrivate(Player p){
		this.privatePlayers.remove(p);
	}
	
	public boolean isPrivate(Player p){
		return this.privatePlayers.contains(p);
	}
	
	@SuppressWarnings("unchecked")
	@EventHandler
	public void onPlayerChat(PlayerChatEvent e){
		if(!this.privatePlayers.contains(e.getPlayer())){
			JSONObject json	=	new JSONObject();
			json.put("message", e.getMessage());
			json.put("time", new Date().toString());
			json.put("player", e.getPlayer().getName());
			json.put("source", "in");
			this.plugin.getFileMan().write(this.plugin.chatFile(), json);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@EventHandler
	public void onPlayerDeath(EntityDeathEvent e){
		if(e instanceof PlayerDeathEvent){
			if(this.plugin.getConfig().getBoolean("show_death") && !this.privatePlayers.contains(e.getEntity()) ){
				JSONObject json	=	new JSONObject();
				json.put("message",((PlayerDeathEvent) e).getDeathMessage());
				json.put("time",new Date().toString());
				json.put("player","Humiliator");
				json.put("source", "in");
				this.plugin.getFileMan().write(this.plugin.chatFile(), json);
			}
		}
	}
}