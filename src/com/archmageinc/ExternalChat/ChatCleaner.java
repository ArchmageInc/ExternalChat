package com.archmageinc.ExternalChat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ChatCleaner extends TimerTask {
	
	private ExternalChat plugin;
	private SimpleDateFormat fmt	=	new SimpleDateFormat();
	
	public ChatCleaner(ExternalChat plugin){
		this.plugin=plugin;
		fmt.applyPattern("EEE MMM d H:m:s z yyyy");
	}
	
	@Override
	public void run() {
		String data	=	this.plugin.getFileMan().read(this.plugin.chatFile());
		JSONArray jArray;
		try {
			jArray = (JSONArray) new JSONParser().parse(data);
			for(int i=0;i<jArray.size();i++){
				JSONObject json	=	(JSONObject) jArray.get(i);
				String time		=	(String) json.get("time");
				try {
					Date date		=	this.fmt.parse(time);
					if(date.getTime()<new Date().getTime()-this.plugin.historyChat()){
						jArray.remove(i);
					}
				} catch (ParseException e) {
					this.plugin.logMessage("Invalid date format: "+e.getMessage());
				}
			}
			this.plugin.getFileMan().write(this.plugin.chatFile(), jArray);
		} catch (org.json.simple.parser.ParseException ex) {
			if(ex.getMessage()!=null){
				this.plugin.logMessage("Error while parsing JSON: "+ex.getMessage());
			}
		}
	}

}
