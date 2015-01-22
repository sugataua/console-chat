package chat;

/**
 * Class contains parameters for server configuration
 */
public class ServerConfig
{
	private int limitMessages;
	private int limitClients;
	private int port;
	
	public ServerConfig(int port, int maxClients, int maxMessages)
	{
		this.port = port;
		this.limitClients = maxClients;
		this.limitMessages = maxMessages;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public int getLimitClients() {
		return this.limitClients;
	}
	
	public int getLimitMessages() {
		return this.limitMessages;
	}
}