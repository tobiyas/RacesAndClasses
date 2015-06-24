package de.tobiyas.racesandclasses.standalonegui.login;

public class ServerConnectionData {

	/**
	 * The Host / Server
	 */
	private final String host;
	
	/**
	 * The port of the Server to use
	 */
	private final int port;
	
	/**
	 * The Username to use.
	 */
	private final String username;
	
	/**
	 * The Connection token to use.
	 */
	private final String connectionToken;

	
	public ServerConnectionData(String host, int port, String username, String connectionToken) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.connectionToken = connectionToken;
	}
	

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public String getConnectionToken() {
		return connectionToken;
	}
	
	
	@Override
	public String toString() {
		return "{host:'" + host + ":" + port + "' username:'"+username+"' token:'" + connectionToken  + "'}";
	}
	
}
