package de.tobiyas.racesandclasses.chat.channels.container;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;

import de.tobiyas.racesandclasses.util.chat.ChannelLevel;

@Entity
@Table
public class ChannelSaveContainer {

	@NotEmpty
	private String channelName;
	
	private String channelPassword;
	
	private String channelAdmin;
	
	@Embedded
	private ChannelLevel channelLevel;
	
	@NotNull
	private String participants;
	
	@NotNull
	private String bannedMap;
	
	@NotNull
	private String mutedMap;
	
	
	//Channel Formatter
	private String prefix;
	private String suffix;
	private String channelColor;
	private String channelFormat;
	
	
	
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public String getChannelPassword() {
		return channelPassword;
	}
	public void setChannelPassword(String channelPassword) {
		this.channelPassword = channelPassword;
	}
	public String getChannelAdmin() {
		return channelAdmin;
	}
	public void setChannelAdmin(String channelAdmin) {
		this.channelAdmin = channelAdmin;
	}
	public ChannelLevel getChannelLevel() {
		return channelLevel;
	}
	public void setChannelLevel(ChannelLevel channelLevel) {
		this.channelLevel = channelLevel;
	}
	public String getParticipants() {
		return participants;
	}
	
	/**
	 * Use {@link #generateParitipants()} for a parsed list.
	 * 
	 * @param participants
	 */
	@Deprecated
	public void setParticipants(String participants) {
		this.participants = participants;
	}
	
	/**
	 * Use {@link #generateBannedMap()} for a parsed Map.
	 */
	@Deprecated
	public String getBannedMap() {
		return bannedMap;
	}
	
	/**
	 * Use {@link #saveBannedMap()} to save.
	 * 
	 * @param bannedMap to save
	 */
	@Deprecated
	public void setBannedMap(String bannedMap) {
		this.bannedMap = bannedMap;
	}
	
	/**
	 * Use {@link #generateMutedMap()} for a parsed Map.
	 */
	@Deprecated
	public String getMutedMap() {
		return mutedMap;
	}
	
	/**
	 * Use {@link #saveBannedMap()} to save.
	 * 
	 * @param mutedMap
	 */
	@Deprecated
	public void setMutedMap(String mutedMap) {
		this.mutedMap = mutedMap;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public String getSuffix() {
		return suffix;
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	public String getChannelColor() {
		return channelColor;
	}
	
	public void setChannelColor(String channelColor) {
		this.channelColor = channelColor;
	}
	
	public String getChannelFormat() {
		return channelFormat;
	}
	
	public void setChannelFormat(String channelFormat) {
		this.channelFormat = channelFormat;
	}
	
	
	
	/**
	 * Generates the List of Participants from the value saved in the DB.
	 * 
	 * @return
	 */
	public List<String> generateParitipants(){
		try {
			List<String> participants = new LinkedList<String>();
			JSONArray tempObject = (JSONArray) new JSONParser().parse(this.participants);
			
			//early out for error in reading.
			if(tempObject == null || tempObject.size() == 0) return participants;
			
			for(int i = 0; i < tempObject.size(); i++){
				participants.add(tempObject.get(i).toString());
			}
			
			return participants;
		} catch (ParseException e) {
			e.printStackTrace();
			return new LinkedList<String>();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void saveParticipants(List<String> participants){
		JSONArray tempObject = new JSONArray();
		for(String playerName : participants){
			tempObject.add(playerName);
		}
		
		this.participants = tempObject.toJSONString();
	}
	
	
	public void saveBannedMap(Map<String, Integer> banned){
		this.bannedMap = parseMapToString(banned);
	}
	
	
	public Map<String, Integer> generateBannedMap(){
		return parseStringToMap(this.bannedMap);
	}
	
	
	public void saveMutedMap(Map<String, Integer> banned){
		this.mutedMap = parseMapToString(banned);
	}
	
	
	public Map<String, Integer> generateMutedMap(){
		return parseStringToMap(this.mutedMap);
	}
	
	
	
	@SuppressWarnings("unchecked")
	private String parseMapToString(Map<String, Integer> map){
		JSONObject tempObject = new JSONObject();
		for( Entry<String, Integer> player : map.entrySet()){
			tempObject.put(player.getKey(), player.getValue());
		}
		
		return tempObject.toJSONString();
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Integer> parseStringToMap(String parseable){
		try {
			Map<String, Integer> bannedmap = new HashMap<String, Integer>();
			JSONObject tempObject = (JSONObject) new JSONParser().parse(parseable);
			
			//early out for error in reading.
			if(tempObject == null || tempObject.size() == 0) return bannedmap;
			
			Set<String> entrySet = tempObject.entrySet();
			for(String entry : entrySet){
				int value = (Integer) tempObject.get(entry);
				bannedmap.put(entry, value);
			}
			
			return bannedmap;
		} catch (ParseException e) {
			e.printStackTrace();
			return new HashMap<String, Integer>();
		}
	}
}
