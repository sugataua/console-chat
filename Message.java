// Simple console chat project
// Class message
package chat;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable
{
	private String username;
	private String address;
	private String message;
	private Date dtstamp;
	
	public Message(String uname, String address, String message, Date stamp)
	{
		this.username = uname;
		this.address = address;
		this.message = message;
		this.dtstamp = stamp;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	public Date getDT()
	{
		return this.dtstamp;
	}
	
	public String getAddress()
	{
		return  this.address;
	}
	
	public String getUsername()
	{
		return this.username;
	}
}