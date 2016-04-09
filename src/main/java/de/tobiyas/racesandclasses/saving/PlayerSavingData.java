package de.tobiyas.racesandclasses.saving;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class PlayerSavingData {
	
	/**
	 * The ID of the Player.
	 */
	private UUID playerId;
	
	/**
	 * The last login date.
	 */
	private long lastLogin = 0;
	
	/**
	 * The last login date.
	 */
	private String lastName = "";
	
	/**
	 * The Race of the Player
	 */
	private String raceName = "";
	
	/**
	 * The Class of the Player
	 */
	private String className = "";
	
	/**
	 * The Level to use.
	 */
	private int level = 1;
	
	/**
	 * The EXP of the Level.
	 */
	private int levelExp = 0;
	
	/**
	 * If godmode is enabled.
	 */
	private boolean godMode = false;
	 
	/**
	 * The Hotkeys to use.
	 */
	private final Map<Integer,String> hotKeys = new HashMap<>();
	
	/**
	 * The Hotkeys to use.
	 */
	private final Map<String,Integer> skillTree = new HashMap<>();
	
	
	//////Field only for Serialization///
	private String additionalJsonData = "";
	
	
	public PlayerSavingData(UUID playerId) {
		this.playerId = playerId;
	}
	


	public PlayerSavingData(UUID playerId, 
			long lastLogin, String lastName, String raceName, String className, int level, int levelExp, boolean godMode, 
			Map<Integer, String> hotKeys, Map<String,Integer> skillTree) {
		
		this.playerId = playerId;
		this.lastName = lastName;
		this.lastLogin = lastLogin;
		this.raceName = raceName;
		this.className = className;
		this.level = level;
		this.levelExp = levelExp;
		this.godMode = godMode;
		
		if(hotKeys != null) this.hotKeys.putAll(hotKeys);
		if(skillTree != null) this.skillTree.putAll(skillTree);
	}



	public long getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(long lastLogin) {
		if(lastLogin != this.lastLogin) return;
		this.lastLogin = lastLogin;
		
		save();
	}

	public String getRaceName() {
		return raceName;
	}
	
	public void setRaceName(String raceName) {
		if(raceName.equals(this.raceName)) return;
		
		this.raceName = raceName;
		
		save();
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		if(this.lastName.equals(lastName)) return;
		this.lastName = lastName;
		
		save();
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		if(className.equals(this.className)) return;
		this.className = className;
		
		save();
	}
	
	public void setGodMode(boolean godMode) {
		this.godMode = godMode;
		
		save();
	}
	
	public boolean isGodModeEnabled() {
		return godMode;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		if(this.level == level) return;
		this.level = level;
		
		save();
	}
	
	public void setLevelExp(int levelExp) {
		if(this.levelExp == levelExp) return;
		this.levelExp = levelExp;
		
		save();
	}
	
	public void setLevelAndExp(int level, int levelExp){
		if(this.level == level && this.levelExp == levelExp) return;
		this.level = level;
		this.levelExp = levelExp;
		
		save();
	}
	
	public int getLevelExp() {
		return levelExp;
	}

	public UUID getPlayerId() {
		return playerId;
	}
	
	public void setPlayerId(UUID playerId) {
		this.playerId = playerId;
	}
	
	public void setHotKey(Integer slot, String trait) {
		this.hotKeys.put(slot, trait);
		
		save();
	}
	
	public void setHotKeys(Map<Integer,String> hotKeys) {
		this.hotKeys.clear();
		this.hotKeys.putAll(hotKeys);
		
		save();
	}
	
	/**
	 * Clears the Hotkeys.
	 * <br>Starts a save.
	 */
	public void clearHotKeys() {
		this.hotKeys.clear();
		save();
	}
	

	public void clearHotKey(int slot) {
		this.hotKeys.remove(slot);
		save();
	}
	
	/**
	 * Gets the Hotkeys.
	 * This is only a copy!
	 * @return a copy of the Hotkeys.
	 */
	public Map<Integer,String> getHotKeys() {
		return new HashMap<>(hotKeys);
	}
	
	/**
	 * Gets the Hotkeys.
	 * This is only a copy!
	 * @return a copy of the Hotkeys.
	 */
	public void overrideSkilltree(Map<String,Integer> map) {
		this.skillTree.clear();
		this.skillTree.putAll(map);
		
		save();
	}
	
	/**
	 * Gets the SkillTree.
	 */
	public Map<String,Integer> getSkillTree() {
		return new HashMap<>(skillTree);
	}
	
	/**
	 * Clears the SkillTree.
	 */
	public void clearSkilltree() {
		this.skillTree.clear();
		
		save();
	}	
	
	
	/**
	 * Saves the Container.
	 */
	private void save(){
		//Check if empty. If empty -> Skip!
		if(raceName.isEmpty() && className.isEmpty() 
				&& hotKeys.isEmpty() && skillTree.isEmpty()) return;
		
		updateSerializedJsonData();
		PlayerSavingManager.get().getSerializer().saveData(this);
	}
	
	/**
	 * Updates the Serialized Values.
	 */
	private void updateSerializedJsonData(){
		JsonObject root = new JsonObject();
		
		//Serialize HotKeys:
		JsonObject rootNode = new JsonObject();
		root.add("hotkey", rootNode);
		for(Map.Entry<Integer,String> entry : hotKeys.entrySet()){
			rootNode.addProperty(String.valueOf(entry.getKey()), entry.getValue());
		}
		

		//Serialize SkillTree:
		rootNode = new JsonObject();
		root.add("skilltree", rootNode);
		for(Map.Entry<String,Integer> entry : skillTree.entrySet()){
			rootNode.addProperty(entry.getKey(), entry.getValue());
		}
		
		this.additionalJsonData = root.toString();
	}
	
	/**
	 * Updates the Serialized Values.
	 */
	public void unserializeJsonData(){
		JsonParser parser = new JsonParser();
		try{
			JsonObject obj = (JsonObject) parser.parse(additionalJsonData);
			if(obj.has("hotkey")){
				this.hotKeys.clear();
				
				JsonElement hotKeyRoot = obj.get("hotkey");
				if(hotKeyRoot.isJsonObject()){
					JsonObject hkObj = hotKeyRoot.getAsJsonObject();
					for(Map.Entry<String,JsonElement> entry : hkObj.entrySet()){
						int key = Integer.getInteger(entry.getKey());
						String trait = entry.getValue().getAsString();
						
						this.hotKeys.put(key, trait);
					}
				}
			}

			if(obj.has("skilltree")){
				this.skillTree.clear();
				
				JsonElement stRoot = obj.get("skilltree");
				if(stRoot.isJsonObject()){
					JsonObject stObj = stRoot.getAsJsonObject();
					for(Map.Entry<String,JsonElement> entry : stObj.entrySet()){
						String trait = entry.getKey();
						int level = entry.getValue().getAsInt();
						
						this.skillTree.put(trait, level);
					}
				}
			}
		}catch(Throwable exp){}
	}


	public String getAdditionalJsonData() {
		return additionalJsonData;
	}


	public void setAdditionalJsonData(String additionalJsonData) {
		this.additionalJsonData = additionalJsonData;
	}


	public boolean isGodMode() {
		return godMode;
	}
	
	
}