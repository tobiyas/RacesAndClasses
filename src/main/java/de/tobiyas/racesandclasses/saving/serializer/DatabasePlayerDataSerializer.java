package de.tobiyas.racesandclasses.saving.serializer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.saving.PlayerSavingData;
import de.tobiyas.util.file.IOUtils;
import de.tobiyas.util.schedule.DebugBukkitRunnable;
import de.tobiyas.util.sql.SQL;
import de.tobiyas.util.sql.SQL.SQLProperties;

public class DatabasePlayerDataSerializer implements PlayerDataSerializer {

	
	private static final String TABLE_NAME = "RacesAndClasses";
	
	
	/**
	 * The Plugin to use.
	 */
	private final RacesAndClasses plugin;
	
	/**
	 * Use the connection here!
	 */
	private Connection connection;

	
	
	public DatabasePlayerDataSerializer(RacesAndClasses plugin) {
		this.plugin = plugin;
	}
	
	
	@Override
	public void saveData(PlayerSavingData data) {
		boolean sync = RacesAndClasses.isBukkitInShutdownMode();
		saveData(data, sync);
	}
	
	private void saveData(final PlayerSavingData data, boolean sync){
		BukkitRunnable runnable = new DebugBukkitRunnable("LoadRaCData") {
			@Override
			protected void runIntern() {
				
				Statement statement = null;
				
				try{
					checkConnection();
					if(connection == null) {
						RacesAndClasses.getPlugin().logWarning("Could not open DB connection! Please change to YML stuff!");
						return;
					}
					
					statement = connection.createStatement();
					String sqlUpdateStatement = 
							"REPLACE INTO `" + TABLE_NAME + "` "
							+ "(id,lastLogin,lastName,race,class,level,exp,godMode,additionalData) "
							+ " VALUES ("
							+ "'" + data.getPlayerId().toString() + "',"
							+ data.getLastLogin() + ","
							+ "'" + data.getLastName() + "',"

							+ "'" + data.getRaceName() + "',"
							+ "'" + data.getClassName() + "',"

							+ data.getLevel() + ","
							+ data.getLevelExp() + ","

							+ (data.isGodMode() ? 1 : 0) + ","
							
							+ "'" + data.getAdditionalJsonData().replace("'", "\\'") + "'"
							
							+ ")";
					
					statement.execute(sqlUpdateStatement);
				}catch(Throwable exp){ exp.printStackTrace(); }
				finally {
					//Finnaly close!
					try{ statement.close(); }catch(Throwable exp){}
				}
			}
		};
		
		if(sync) runnable.run();
		else runnable.runTaskAsynchronously(plugin);
	}

	@Override
	public void loadData(final UUID id, final PlayerDataLoadedCallback callback) {
		new DebugBukkitRunnable("LoadRaCData") {
			@Override
			protected void runIntern() {
				PlayerSavingData data = loadDataNow(id);
				callback.playerDataLoaded(data);
			}
		}.runTaskAsynchronously(plugin);
	}
	

	@Override
	public Collection<PlayerSavingData> bulkLoadDataNow(Set<UUID> ids) {
		//First build the Query!
		Set<PlayerSavingData> datas = new HashSet<>();
		StringBuilder selectQueryBuilder = new StringBuilder();
		selectQueryBuilder.append("SELECT * FROM `").append(TABLE_NAME).append("` WHERE id IN (");
		boolean first = true;
		
		for(UUID id : ids){
			if(!first) selectQueryBuilder.append(",");
			first = false;
			
			selectQueryBuilder.append("'").append(id).append("'");
		}
		
		selectQueryBuilder.append(")");
		
		
		//Now execute!
		Connection connection = null;
		Statement statement = null;
		ResultSet result = null;
		try{
			//Create own connection to not block other connection.
			connection = SQL.getSQLConnection(getProperties());
			
			String query = selectQueryBuilder.toString();
			checkConnection();
			
			statement = connection.createStatement();
			result = statement.executeQuery(query);
			
			while(result.next()){
				PlayerSavingData data = analyseCurrentResultLine(result);
				if(data != null) datas.add(data);
			}
		}catch(Throwable exp){
			exp.printStackTrace();
		}finally {
			IOUtils.closeQuietly(statement);
			IOUtils.closeQuietly(result);
			IOUtils.closeQuietly(connection);
		}
		
		return datas;
	}
	
	
	@Override
	public void bulkLoadData(final Set<UUID> ids, final PlayerDataLoadedCallback callback){
		new DebugBukkitRunnable("LoadRaCData") {
			@Override
			protected void runIntern() {
				for(PlayerSavingData data : bulkLoadDataNow(ids)){
					callback.playerDataLoaded(data);
				}
			}
		}.runTaskAsynchronously(plugin);
	}
	
	
	/**
	 * Reads current Line.
	 * 
	 * @param set to analyse
	 * @return the read data.
	 */
	private PlayerSavingData analyseCurrentResultLine(ResultSet resultSet){
		try{
			int columns = resultSet.getMetaData().getColumnCount();
			if(columns < 9) {
				RacesAndClasses.getPlugin().logError("Something on the DB is broken!");
				return null;
			}
			
			//Remember that counting starts by 1 with columns!
			UUID id = UUID.fromString(resultSet.getString(1));
			long lastLogin = resultSet.getLong(2);
			String lastName = resultSet.getString(3);
			String raceName = resultSet.getString(4);
			String className = resultSet.getString(5);
			int level = resultSet.getInt(6);
			int exp = resultSet.getInt(7);
			boolean god = resultSet.getInt(8) == 1;
			String additionalData = resultSet.getString(9);
			
			PlayerSavingData data = new PlayerSavingData(id, lastLogin, lastName, raceName, className, level, exp, god, null, null);
			data.setAdditionalJsonData(additionalData);
			data.unserializeJsonData();
			
			return data;
		}catch(Throwable exp){
			return null;
		}
	}
	

	@Override
	public PlayerSavingData loadDataNow(UUID id) {
		PlayerSavingData data = null;
		
		
		Statement statement = null;
		ResultSet resultSet = null;
		
		try{
			checkConnection();
			if(connection == null) {
				RacesAndClasses.getPlugin().logWarning("Could not open DB connection! Please change to YML stuff!");
				return null;
			}
			
			statement = connection.createStatement();
			
			String sqlSelectStatement = "SELECT * FROM `" + TABLE_NAME + "` WHERE " + " id='" + id.toString() + "'";
			resultSet = statement.executeQuery(sqlSelectStatement);

			//No result!
			if(!resultSet.next()) return null;
			
			data = analyseCurrentResultLine(resultSet);
		}catch(Throwable exp){ exp.printStackTrace(); }
		finally {
			//Finnaly close!
			try{ statement.close(); }catch(Throwable exp){}
			try{ resultSet.close(); }catch(Throwable exp){}
		}
		
		return data;
	}

	@Override
	public Set<UUID> getAllIDsPresent() {
		Set<UUID> ids = new HashSet<>();
		
		
		Statement statement = null;
		ResultSet resultSet = null;
		
		try{
			checkConnection();
			if(connection == null) {
				RacesAndClasses.getPlugin().logWarning("Could not open DB connection! Please change to YML stuff!");
				return null;
			}
			
			statement = connection.createStatement();
			
			String sqlSelectStatement = "SELECT id FROM `" + TABLE_NAME + "`";
			resultSet = statement.executeQuery(sqlSelectStatement);

			//Iterate over results:
			while(resultSet.next()){
				try{
					String id = resultSet.getString(1);
					UUID uID = UUID.fromString(id);
					if(id != null) ids.add(uID);
				}catch(Throwable exp){}
			}
		}catch(Throwable exp){
			exp.printStackTrace();
		}finally {
			//Finnaly close!
			try{ statement.close(); }catch(Throwable exp){}
			try{ resultSet.close(); }catch(Throwable exp){}
		}
		
		
		return ids;
	}
	
	@Override
	public void shutdown() {
		
	}
	
	
	/**
	 * Gets all Properties.
	 * @return properties.
	 */
	private SQLProperties getProperties(){
		return RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_databaseData();
	}
	
	
	@Override
	public boolean isFunctional() {
		SQLProperties props = getProperties();
		try{
			//First tell the SQL where to log!
			SQL.init(plugin);
			
			//Now try to connect!
			Connection connection = SQL.getSQLConnection(props);
			if(connection == null) return false;
			
			boolean reachable = SQL.checkDBIsReachable(props);
			if(!reachable){
				RacesAndClasses.getPlugin().logWarning("Could not establish DB-Connection! Switching to YML files!");
				System.out.println("Props: " + props.serverName + ":" + props.serverPort + " - " + props.serverDB + " U:" + props.userName + " P:" + props.password);
				return false;
			}
			
			//Create the Table if not exist:
			String sqlCommand = "CREATE TABLE " 
							+ "IF NOT EXISTS "
							+ "`RacesAndClasses`"
							+"(" 
							+   "`id` VARCHAR(64) NOT NULL,"
							+   "`lastLogin` BIGINT NOT NULL DEFAULT '0',"
							+   "`lastName` VARCHAR(32) NOT NULL DEFAULT '',"
							
							+   "`race` VARCHAR(32) NOT NULL DEFAULT '',"
							+   "`class` VARCHAR(32) NOT NULL DEFAULT '',"
							
							+   "`level` INT NOT NULL DEFAULT '0',"
							+   "`exp` INT NOT NULL DEFAULT '0',"
							
							+   "`godMode` TINYINT NOT NULL DEFAULT '0',"
							
							+   "`additionalData` MEDIUMTEXT NOT NULL,"
							
							+	"PRIMARY KEY (`id`)"
							+")";
			SQL.tryCreateDBIfNotExist(props, "", sqlCommand);
			
			//GENERATE_AND_UPLOAD_DATA();
			return reachable;
		}catch(Throwable exp){
			exp.printStackTrace();
			return false;
		}
	}
	
	private void checkConnection(){
		if(connection == null) try{ connection = SQL.getSQLConnection(getProperties()); }catch(Throwable exp){}
	}
	
	
	
	protected void GENERATE_AND_UPLOAD_DATA(){
		for(int i = 0; i <= 10; i++){
			StringBuilder queryBuilder = new StringBuilder();
			queryBuilder.append("INSERT INTO `").append(TABLE_NAME).append("` (id,additionalData) VALUES ");
			
			for(int j = 0; j <= 10_000; j++){
				
				queryBuilder.append("('").append(UUID.randomUUID()).append("','").append("{text=\"" + generateRandomString(200) + "\"}").append("')");
				if(j != 10_000) queryBuilder.append(",");
			}
			
			checkConnection();
			try{
				Statement st = connection.createStatement();
				st.execute(queryBuilder.toString());
				st.close();
			}catch(Throwable exp){ exp.printStackTrace(); }
		}
	}
	
	
	protected String generateRandomString(int length){
		Random rand = new Random();
		String set = "";
		for(int i = 0; i < length; i++){
			char cha = (char)('a'+rand.nextInt(20));
			set+= cha;
		}
		
		return set;
	}
	

}
