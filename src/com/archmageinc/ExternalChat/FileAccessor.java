package com.archmageinc.ExternalChat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;




public class FileAccessor {
	private ExternalChat plugin		=	null;
	
	public FileAccessor(ExternalChat plugin){
		this.plugin 	= 	plugin;
		
	}
	
	public void append(String fileName,String message){
		try {
			BufferedWriter writer		=	new BufferedWriter(new FileWriter(fileName,true));
			writer.write(message);
			writer.close();
		}catch (java.io.IOException e) {
			this.plugin.logMessage("Unable to write to "+fileName+": "+e.getMessage());
		}
	}
	
	public void write(String fileName,String message){
		try {
			BufferedWriter writer		=	new BufferedWriter(new FileWriter(fileName));
			writer.write(message);
			writer.close();
		}catch (java.io.IOException e) {
			this.plugin.logMessage("Unable to write to "+fileName+": "+e.getMessage());
		}
	}
	@SuppressWarnings("unchecked")
	public void write(String fileName,JSONObject json){
		JSONParser parser	=	new JSONParser();
		String data			=	this.read(fileName);
		JSONArray jArray	=	new JSONArray();
		try {
			jArray = (JSONArray) parser.parse(data);
		} catch (ParseException e) {
			if(e.getMessage()!=null){
				this.plugin.logMessage("Error while parsing JSON: "+e.getMessage());
			}
		}
		jArray.add(json);
		this.write(fileName, jArray.toString());
	}
	
	public void write(String fileName,JSONArray jArray){
		this.write(fileName, jArray.toString());
	}
	public void truncate(String fileName){
		try {
			BufferedWriter writer		=	new BufferedWriter(new FileWriter(fileName));
			writer.write("");
			writer.close();
		}catch (java.io.IOException e) {
			this.plugin.logMessage("Unable to truncate "+fileName+": "+e.getMessage());
		}
	}
	
	public String read(String fileName){
		String data	=	"";
		String line;
		try {
			BufferedReader reader		=	new BufferedReader(new FileReader(fileName));
			while((line=reader.readLine())!=null){
				data+=line;
			}
		} catch (FileNotFoundException e) {
			this.plugin.logMessage("Unable to read from "+fileName+": "+e.getMessage());
		} catch (IOException e) {
			this.plugin.logMessage("Error while reading "+fileName+": "+e.getMessage());
		}
		return data;
	}
}
