package de.tobiyas.racesandclasses.persistence.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.chat.channels.container.ChannelSaveContainer;
import de.tobiyas.racesandclasses.configuration.member.database.DBConfigOption;
import de.tobiyas.racesandclasses.configuration.member.file.ConfigOption;
import de.tobiyas.racesandclasses.configuration.member.file.ConfigOption.SaveFormat;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.PlayerHolderAssociation;
import de.tobiyas.racesandclasses.persistence.PersistenceStorage;
import de.tobiyas.racesandclasses.playermanagement.PlayerSavingContainer;

public class SQLPersistenceStorage implements PersistenceStorage {

	private static final String PLAYER_HOLDER_TABLE_NAME = "_player_holders";
	private static final String PLAYER_CONFIG_TABLE_NAME = "_config_options";
	private static final String CHANNEL_CONFIG_TABLE_NAME = "_channel_settings";
	private static final String PLAYER_GENERAL_CONFIG_TABLE_NAME = "_player_general_infos";
	
	/**
	 * The Plugin to call stuff on
	 */
	private final RacesAndClasses plugin = RacesAndClasses.getPlugin();

	private String connectionString;
	private String tablePrefix = "_RaC_Table";
	private Connection connection = null;
	
	
	//Connection infos
	private String serverName;
	private String serverPort;
	private String serverDB;
	private String username;
	private String password;
	
	
	// Scale waiting time by this much per failed attempt
	private final double SCALING_FACTOR = 40.0;

	// Minimum wait in nanoseconds (default 500ms)
	private final long MIN_WAIT = 500L * 1000000L;

	// Maximum time to wait between reconnects (default 5 minutes)
	private final long MAX_WAIT = 5L * 60L * 1000L * 1000000L;

	// How long to wait when checking if connection is valid (default 3 seconds)
	private final int VALID_TIMEOUT = 3;

	// When next to try connecting to Database in nanoseconds
	private long nextReconnectTimestamp = 0L;

	// How many connection attempts have failed
	private int reconnectAttempt = 0;
	
	
	
	
	public SQLPersistenceStorage() {
	}

	
	@Override
	public void initForStartup() {
		setupDB();
	}
	
	/**
	 * Sets up the SQL Connection and sets up all Tables
	 */
	private void setupDB(){
		checkConnected();	
		createSQLDBStructure();
	}

	
	/**
	 * Creates all Tables if not existent
	 */
	private void createSQLDBStructure() {

		//Init PlayerHolderAssociation
		write("CREATE TABLE IF NOT EXISTS `" + tablePrefix + PLAYER_HOLDER_TABLE_NAME + "` ("
                + "`player_name` varchar(40) NOT EMPTY,"
                + "`class_name` varchar(80),"
                + "`race_name` varchar(80) NOT NULL,"
                + "PRIMARY KEY (`player_name`),"
                + "DEFAULT CHARSET=latin1;");

		//Init PlayerGeneralData
		write("CREATE TABLE IF NOT EXISTS `" + tablePrefix + PLAYER_GENERAL_CONFIG_TABLE_NAME + "` ("
				+ "`player_name` varchar(40) NOT EMPTY,"
				+ "`player_level` int(64) DEFAULT(1),"
				+ "`player_level_exp` int(64) DEFAULT(0),"
				+ "`has_god` int(1) DEFAULT(0),"
				+ "PRIMARY KEY (`player_name`),"
				+ "DEFAULT CHARSET=latin1;");

		//Init PlayerConfigs
		write("CREATE TABLE IF NOT EXISTS `" + tablePrefix + PLAYER_CONFIG_TABLE_NAME + "` ("
				+ "`player_name` varchar(40) NOT EMPTY,"
				+ "`string_value` int(64) DEFAULT(1),"
				+ "`string_default_value` int(64) DEFAULT(0),"
				+ "`path` varchar(100) NOT NULL,"
				+ "`display_name` varchar(100) NOT NULL,"
				+ "`visible` int(1) DEFAULT(1),"
				+ "`format` varchar(3) DEFAULT(999),"
				+ "PRIMARY KEY (`player_name`, `path`),"
				+ "DEFAULT CHARSET=latin1;");

		//Init ChannelSavings
		write("CREATE TABLE IF NOT EXISTS `" + tablePrefix + CHANNEL_CONFIG_TABLE_NAME + "` ("
				+ "`channel_name` varchar(40) NOT EMPTY,"
				+ "`channel_password` varchar(100),"
				+ "`channel_admin` varchar(60),"
				+ "`channel_level` int(10) NOT NULL,"
				+ "`participants` varchar(255) NOT NULL,"
				+ "`banned_map` varchar(256),"
				+ "`muted_map` varchar(256),"
				+ "`prefix` varchar(20),"
				+ "`suffix` varchar(20),"
				+ "`channel_color` varchar(10),"
				+ "`channel_format` varchar(256),"
				+ "PRIMARY KEY (`channel_name`),"
				+ "DEFAULT CHARSET=latin1;");
	}

	@Override
	public void shutDown() {
		try{
			connection.close();
		}catch (Exception e) {
			plugin.log("Could not close SQL connection. This may cause a Leak. Problem was: " 
					+ e.getLocalizedMessage());
		}
	}

	@Override
	public boolean savePlayerSavingContainer(PlayerSavingContainer container) {
		
		String updateSQLString = 
				"UPDATE " + tablePrefix + PLAYER_GENERAL_CONFIG_TABLE_NAME +  " SET "
                + " player_level = " + container.getPlayerLevel() + ","
                + " player_level_exp = " + container.getPlayerLevelExp() + ","
                + " has_god = " + (container.isHasGod() ? 1 : 0) + ","
				+ " WHERE player_name = " + container.getPlayerName()
				
				+ "IF @@ROWCOUNT=0 "
				+ "INSERT INTO " + tablePrefix + PLAYER_GENERAL_CONFIG_TABLE_NAME +  " "
				+ "(player_level, player_level_exp, has_god, player_name)"
				+ " VALUES('" + container.getPlayerLevel()
				+ "','" + container.getPlayerLevelExp()
				+ "','" + container.getPlayerName()
				+ "')";
		
			if(plugin.getConfigManager().getGeneralConfig().isConfig_enableDebugOutputs()){
				plugin.getDebugLogger().log("SQL String for Update of PlayerGeneralInfo: " + updateSQLString);
			}
	
		
		return write(updateSQLString);
	}

	@Override
	public boolean savePlayerHolderAssociation(PlayerHolderAssociation container) {
		String updateSQLString = 
			"UPDATE " + tablePrefix + PLAYER_HOLDER_TABLE_NAME + " SET "
            + " class_name = " + container.getClassName() + ","
            + " race_name = " + container.getRaceName() + ","
			+ " WHERE player_name = " + container.getPlayerName()
			
			+ "IF @@ROWCOUNT=0 "
			+ "INSERT INTO " + tablePrefix + PLAYER_HOLDER_TABLE_NAME +  " "
			+ "(class_name, race_name, player_name)"
			+ " VALUES('" + container.getClassName()
			+ "','" + container.getRaceName()
			+ "','" + container.getPlayerName()
			+ "')";
		
			if(plugin.getConfigManager().getGeneralConfig().isConfig_enableDebugOutputs()){
				plugin.getDebugLogger().log("SQL String for Update of HolderAssociation: " + updateSQLString);
			}
		
		return write(updateSQLString);
	}

	@Override
	public boolean savePlayerMemberConfigEntry(ConfigOption uncheckedContainer, boolean forceSave) {
		DBConfigOption container;
		if(uncheckedContainer instanceof DBConfigOption){
			container = (DBConfigOption) uncheckedContainer;
		}else{
			container = DBConfigOption.copyFrom(uncheckedContainer);			
		}
		
		String updateSQLString = 
			"UPDATE " + tablePrefix + PLAYER_CONFIG_TABLE_NAME + " SET "
            + " string_value = " + container.getStringValue() + ","
            + " string_default_value = " + container.getStringDefaultValue() + ","
            + " path = " + container.getPath() + ","
            + " display_name = " + container.getDisplayName() + ","
            + " visible = " + (container.isVisible() ? 1 : 0) + ","
            + " format = " + container.getFormat() + ","
			+ " WHERE player_name = " + container.getPlayerName()
			
			+ "IF @@ROWCOUNT=0 "
			+ "INSERT INTO " + tablePrefix + PLAYER_CONFIG_TABLE_NAME + " "
			+ "(string_value, string_default_value, path, display_name, visible, format, player_name)"
			+ " VALUES('" + container.getStringValue()
			+ "','" + container.getStringDefaultValue()
			+ "','" + container.getPath()
			+ "','" + container.getDisplayName()
			+ "','" + (container.isVisible() ? 1 : 0)
			+ "','" + container.getFormat()
			+ "','" + container.getPlayerName()
			+ "')";

			if(plugin.getConfigManager().getGeneralConfig().isConfig_enableDebugOutputs()){
				plugin.getDebugLogger().log("SQL String for Update of ConfigEntry: " + updateSQLString);
			}
		
		return write(updateSQLString);
	}

	@Override
	public boolean saveChannelSaveContainer(ChannelSaveContainer container) {

		@SuppressWarnings("deprecation") //We need the flattened Maps
		String updateSQLString = 
			"UPDATE " + tablePrefix + CHANNEL_CONFIG_TABLE_NAME + " SET "
            + " channel_password = " + container.getChannelPassword() + ","
            + " channel_admin = " + container.getChannelAdmin() + ","
            + " channel_level = " + container.getChannelLevel().name() + ","
            + " participants = " + container.getParticipants() + ","
            + " banned_map = " + container.getBannedMap() + ","
            + " muted_map = " + container.getMutedMap() + ","
            + " prefix = " + container.getPrefix() + ","
            + " suffix = " + container.getSuffix() + ","
            + " channel_color = " + container.getChannelColor() + ","
            + " channel_format = " + container.getChannelFormat() + ","
			+ " WHERE channel_name = " + container.getChannelName()

			+ "IF @@ROWCOUNT=0 "
			+ "INSERT INTO " + tablePrefix + CHANNEL_CONFIG_TABLE_NAME + " "
			+ "(channel_password, channel_admin, channel_level, participants, banned_map, muted_map, prefix, "
			+ "suffix, channel_color,channel_format,channel_name)"
			+ " VALUES('" + container.getChannelPassword()
			+ "','" + container.getChannelAdmin()
			+ "','" + container.getChannelLevel().name() 
			+ "','" + container.getParticipants()
			+ "','" + container.getBannedMap()
			+ "','" + container.getMutedMap()
			+ "','" + container.getPrefix()
			+ "','" + container.getSuffix()
			+ "','" + container.getChannelColor()
			+ "','" + container.getChannelFormat()
			+ "','" + container.getChannelName()
			+ "')";
		
			if(plugin.getConfigManager().getGeneralConfig().isConfig_enableDebugOutputs()){
				plugin.getDebugLogger().log("SQL String for Update of ChannelContainer: " + updateSQLString);
			}
		
		return update(updateSQLString) > 0;
	}

	@Override
	public PlayerSavingContainer getPlayerContainer(String name) {
		Map<Integer, ArrayList<String>> result = read("Select * FROM " + tablePrefix + PLAYER_GENERAL_CONFIG_TABLE_NAME 
				+ " WHERE (player_name ='" + name + "')");
		
		//early out on no result.
		if(result == null || result.isEmpty()) return null;
		
		ArrayList<String> resultArray = result.get(0);
		
		try{
			PlayerSavingContainer container = new PlayerSavingContainer();
			container.setPlayerName(name);

			container.setHasGod(resultArray.get(1).equals("1") ? true : false);
			container.setPlayerLevel(Integer.parseInt(resultArray.get(2)));
			container.setPlayerLevelExp(Integer.parseInt(resultArray.get(3)));
			return container;
			
		}catch(Exception exp){
			plugin.getDebugLogger().logWarning("Retrieving from DB as PlayerContainer errored: ");
			plugin.getDebugLogger().logStackTrace(exp);
		}
		
		return null;
	}

	@Override
	public PlayerHolderAssociation getPlayerHolderAssociation(String name) {
		Map<Integer, ArrayList<String>> result = read("Select * FROM " + tablePrefix + PLAYER_HOLDER_TABLE_NAME 
				+ " WHERE (player_name ='" + name + "')");
		
		//early out on no result.
		if(result == null || result.isEmpty()) return null;
		
		ArrayList<String> resultArray = result.get(0);
		
		try{
			PlayerHolderAssociation container = new PlayerHolderAssociation();
			container.setPlayerName(name);

			container.setRaceName(resultArray.get(1));
			container.setClassName(resultArray.get(2));
			return container;
			
		}catch(Exception exp){
			plugin.getDebugLogger().logWarning("Retrieving from DB as PlayerHolderAssociation errored: ");
			plugin.getDebugLogger().logStackTrace(exp);
		}
		
		return null;
	}

	@Override
	public ConfigOption getPlayerMemberConfigEntryByPath(String playerName,
			String entryPath) {

		Map<Integer, ArrayList<String>> result = read("Select * FROM " + tablePrefix + PLAYER_CONFIG_TABLE_NAME 
				+ " WHERE (player_name ='" + playerName + "' AND path ='" + entryPath + "')");
		
		//early out on no result.
		if(result == null || result.isEmpty()) return null;
		
		ArrayList<String> resultArray = result.get(0);
		
		try{
			DBConfigOption container = new DBConfigOption(playerName);
			container.setPath(entryPath);

			container.setStringValue(resultArray.get(1));
			container.setStringDefaultValue(resultArray.get(2));
			
			container.setDisplayName(resultArray.get(4));
			container.setVisible(resultArray.get(6).equals("1") ? true : false);
			container.setFormat(SaveFormat.parseFromId(resultArray.get(6)));

			return container;
			
		}catch(Exception exp){
			plugin.getDebugLogger().logWarning("Retrieving from DB as PlayerHolderAssociation errored: ");
			plugin.getDebugLogger().logStackTrace(exp);
		}
		
		return null;
	}

	@Override
	public ConfigOption getPlayerMemberConfigEntryByName(String playerName,
			String entryName) {
		Map<Integer, ArrayList<String>> result = read("Select * FROM " + tablePrefix + PLAYER_CONFIG_TABLE_NAME 
				+ " WHERE (player_name ='" + playerName + "' AND display_name ='" + entryName + "')");
		
		//early out on no result.
		if(result == null || result.isEmpty()) return null;
		
		ArrayList<String> resultArray = result.get(0);
		
		try{
			DBConfigOption container = new DBConfigOption(playerName);
			container.setDisplayName(entryName);

			container.setStringValue(resultArray.get(1));
			container.setStringDefaultValue(resultArray.get(2));
			container.setPath(resultArray.get(3));
			
			container.setVisible(resultArray.get(6).equals("1") ? true : false);
			container.setFormat(SaveFormat.parseFromId(resultArray.get(6)));

			return container;
			
		}catch(Exception exp){
			plugin.getDebugLogger().logWarning("Retrieving from DB as PlayerHolderAssociation errored: ");
			plugin.getDebugLogger().logStackTrace(exp);
		}
		
		return null;
	}

	@Override
	public ChannelSaveContainer getChannelSaveContainer(String channelName, String channelLevel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ConfigOption> getAllConfigOptionsOfPlayer(String playerName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PlayerHolderAssociation> getAllPlayerHolderAssociationsForHolder(
			String holderName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ChannelSaveContainer> getAllChannelSaves() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PlayerSavingContainer> getAllPlayerSavingContainers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNameRepresentation() {
		return "SQL Persistence";
	}

	/**
	 * Attempt to write the SQL query.
	 * 
	 * @param sql
	 *            Query to write.
	 * @return true if the query was successfully written, false otherwise.
	 */
	private boolean write(String sql) {
		if (!checkConnected()) {
			return false;
		}

		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(sql);
			statement.executeUpdate();
			return true;
		} catch (SQLException ex) {
			plugin.getDebugLogger().logStackTrace(ex);
			return false;
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					// Ignore
				}
			}
		}
	}

	/**
	 * Returns the number of rows affected by either a DELETE or UPDATE query
	 * 
	 * @param sql
	 *            SQL query to execute
	 * @return the number of rows affected
	 */
	private int update(String sql) {
		int rows = 0;

		if (checkConnected()) {
			PreparedStatement statement = null;

			try {
				statement = connection.prepareStatement(sql);
				rows = statement.executeUpdate();
			} catch (SQLException ex) {
				plugin.getDebugLogger().logStackTrace(ex);
			} finally {
				if (statement != null) {
					try {
						statement.close();
					} catch (SQLException e) {
						// Ignore
					}
				}
			}
		}

		return rows;
	}

	/**
	 * Read SQL query.
	 * 
	 * @param sql
	 *            SQL query to read
	 * @return the rows in this SQL query
	 */
	private HashMap<Integer, ArrayList<String>> read(String sql) {
		HashMap<Integer, ArrayList<String>> rows = new HashMap<Integer, ArrayList<String>>();

		if (checkConnected()) {
			PreparedStatement statement = null;
			ResultSet resultSet;

			try {
				statement = connection.prepareStatement(sql);
				resultSet = statement.executeQuery();

				while (resultSet.next()) {
					ArrayList<String> column = new ArrayList<String>();

					for (int i = 1; i <= resultSet.getMetaData()
							.getColumnCount(); i++) {
						column.add(resultSet.getString(i));
					}

					rows.put(resultSet.getRow(), column);
				}
			} catch (SQLException ex) {
				plugin.getDebugLogger().logStackTrace(ex);
			} finally {
				if (statement != null) {
					try {
						statement.close();
					} catch (SQLException e) {
						// Ignore
					}
				}
			}
		}

		return rows;
	}

	/**
	 * Check connection status and re-establish if dead or stale.
	 * 
	 * If the very first immediate attempt fails, further attempts will be made
	 * in progressively larger intervals up to MAX_WAIT intervals.
	 * 
	 * This allows for MySQL to time out idle connections as needed by server
	 * operator, without affecting McMMO, while still providing protection
	 * against a database outage taking down Bukkit's tick processing loop due
	 * to attempting a database connection each time McMMO needs the database.
	 * 
	 * @return the boolean value for whether or not we are connected
	 */
	public boolean checkConnected() {
		boolean isClosed = true;
		boolean isValid = false;
		boolean exists = (connection != null);

		// If we're waiting for server to recover then leave early
		if (nextReconnectTimestamp > 0
				&& nextReconnectTimestamp > System.nanoTime()) {
			return false;
		}

		if (exists) {
			try {
				isClosed = connection.isClosed();
			} catch (SQLException e) {
				isClosed = true;
				e.printStackTrace();
				plugin.getDebugLogger().logStackTrace(e);
			}

			if (!isClosed) {
				try {
					isValid = connection.isValid(VALID_TIMEOUT);
				} catch (SQLException e) {
					// Don't print stack trace because it's valid to lose idle
					// connections to the server and have to restart them.
					isValid = false;
				}
			}
		}

		// Leave if all ok
		if (exists && !isClosed && isValid) {
			// Housekeeping
			nextReconnectTimestamp = 0;
			reconnectAttempt = 0;
			return true;
		}

		// Cleanup after ourselves for GC and MySQL's sake
		if (exists && !isClosed) {
			try {
				connection.close();
			} catch (SQLException ex) {
				// This is a housekeeping exercise, ignore errors
			}
		}

		// Try to connect again
		connect();

		// Leave if connection is good
		try {
			if (connection != null && !connection.isClosed()) {
				// Schedule a database save if we really had an outage
				if (reconnectAttempt > 1) {
					checkConnected();
				}

				nextReconnectTimestamp = 0;
				reconnectAttempt = 0;
				return true;
			}
		} catch (SQLException e) {
			// Failed to check isClosed, so presume connection is bad and
			// attempt later
			e.printStackTrace();
			plugin.getDebugLogger().logStackTrace(e);
		}

		reconnectAttempt++;
		nextReconnectTimestamp = (long) (System.nanoTime() + Math.min(MAX_WAIT,
				(reconnectAttempt * SCALING_FACTOR * MIN_WAIT)));
		return false;
	}

	/**
	 * Attempt to connect to the mySQL database.
	 */
	private void connect() {
		connectionString = "jdbc:mysql://"
				+ serverName + ":"
				+ serverPort + "/"
				+ serverDB;

		try {
			plugin.getDebugLogger().log("Attempting connection to MySQL...");

			// Force driver to load if not yet loaded
			Class.forName("com.mysql.jdbc.Driver");
			Properties connectionProperties = new Properties();
			connectionProperties.put("user", username);
			connectionProperties.put("password", password);
			connectionProperties.put("autoReconnect", "false");
			connectionProperties.put("maxReconnects", "0");
			connection = DriverManager.getConnection(connectionString,
					connectionProperties);

			plugin.getDebugLogger().log("Connection to MySQL was a success!");
		} catch (SQLException ex) {
			connection = null;

			if (reconnectAttempt == 0 || reconnectAttempt >= 11) {
				plugin.getDebugLogger().logError("Connection to MySQL failed!");
				plugin.getDebugLogger().logStackTrace(ex);
			}
		} catch (ClassNotFoundException ex) {
			connection = null;

			if (reconnectAttempt == 0 || reconnectAttempt >= 11) {
				plugin.getDebugLogger().logError("MySQL database driver not found!");
			}
		}
	}
}
