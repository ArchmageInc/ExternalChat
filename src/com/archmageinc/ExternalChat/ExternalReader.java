package com.archmageinc.ExternalChat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import org.bukkit.ChatColor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class ExternalReader extends TimerTask {
	
	private ExternalChat plugin;
	private Date lastCheck			=	new Date(0);
	private SimpleDateFormat fmt	=	new SimpleDateFormat();
	
	public ExternalReader(ExternalChat plugin){
		fmt.applyPattern("EEE MMM d H:m:s z yyyy");
		this.plugin=plugin;
	}
	
	@Override
	public void run() {
		String data		=	this.plugin.getFileMan().read(this.plugin.chatFile());
		JSONArray jArray;
		try {
			jArray = (JSONArray) new JSONParser().parse(data);
			for(int i=0;i<jArray.size();i++){
				JSONObject json	=	(JSONObject) jArray.get(i);
				String time		=	(String) json.get("time");
				try {
					Date date		=	this.fmt.parse(time);
					String source	=	(String) json.get("source");
					if(date.after(lastCheck) && !source.equals("in")){
						lastCheck		=	date;
						String player	=	(String) json.get("player");
						String message	=	(String) json.get("message");
						this.plugin.getServer().broadcastMessage(ChatColor.DARK_PURPLE+"["+player+"] "+ChatColor.WHITE+message);
					}
				} catch (ParseException ex) {
					this.plugin.logMessage("Invalid date format: "+ex.getMessage());
				}
			}
		} catch (org.json.simple.parser.ParseException ex) {
			if(ex.getMessage()!=null){
				this.plugin.logMessage("Error while parsing JSON: "+ex.getMessage());
			}
		}
	}

}
