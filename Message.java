package chat;

import java.io.Serializable;
import java.util.Date;

/**
 * This serializable class describes chat message for simple console chat.
 * @author Sergii Karpenko 
 */

public class Message implements Serializable
{
	private String username;
	private String address;
	private String message;
	private Date dtstamp;
	
	/**
	 * Constructor creates new message object.
	 * @param uname User's name.
	 * @param address IP-address of client.
	 * @param message Body of message.
	 * @param stamp Date and time of message sending.
	 */	
	public Message(String uname, String address, String message, Date stamp)
	{
		this.username = uname;
		this.address = address;
		this.message = message;
		this.dtstamp = stamp;
	}
	
	/**
	 * This method returns body of message.
	 * @return Body of message
	 */
	public String getMessage()
	{
		return this.message;
	}
	
	/**
	 * This method returns time-stamp of message.
	 * @return Time-stamp of message
	 */
	public Date getDT()
	{
		return this.dtstamp;
	}
	
	/**
	 * This method returns IP-address.
	 * @return IP-address
	 */	
	public String getAddress()
	{
		return  this.address;
	}
	
	/**
	 * This method returns name of user.
	 * @return User's name.
	 */
	public String getUsername()
	{
		return this.username;
	}
}