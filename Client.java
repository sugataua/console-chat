package chat;

import java.net.*;
import java.io.*;
import java.util.Date;

public class Client
{
	final static int port = 1234;
	private InetAddress address;
	private String userName;
	
	
	public static void main(String[] args)
	 {
		 new Client();
	 }
	 
	public Client()
	{
		try
		 {
			 BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
		     do
			 {
				System.out.println("Please, write your user name for chat: \n Notice, that name length must be more or equal then 3 symbol!");			 
				this.userName = keyboard.readLine();
			 } while(this.userName.length() < 3);
			 System.out.println("OK, your user-name is: " + userName);	
			 
			 this.address = InetAddress.getLocalHost();
			 
			 Socket newsocket = new Socket(this.address, port);
			 
			 ServerConnection scon = new ServerConnection(newsocket);
			 
			 
			 		 
			 String line = "";
			 

			 		 
			 
			 System.out.println("Type message and press enter to send...");
			 while(true)
			 {			 			
				 line = keyboard.readLine();	
			     scon.write(line);				 	
			 }
		 } catch (Exception exc) {
			 exc.printStackTrace();
		 }
	}	
	
	 
	public class ServerConnection implements Runnable
	{
		private Socket serverSocket;
		
		//private DataInputStream in;
		//private DataOutputStream out;
		
		private ObjectInputStream in;
		private ObjectOutputStream out;
		
		private boolean isClosed;
		
		ServerConnection(Socket socket)
		{
			try
			{
				this.serverSocket = socket;
				this.isClosed = false;
				
				InputStream sin = serverSocket.getInputStream();
				OutputStream sout = serverSocket.getOutputStream();
				 
				//this.in = new DataInputStream(sin);
				//this.out = new DataOutputStream(sout);
				
				
				this.out = new ObjectOutputStream(sout);				
				this.in = new ObjectInputStream(sin);
				
				new Thread(this).start();				
			} catch(IOException ioex) {
				System.out.println("failed ServerConnection " + ioex);
			}
			
		}
		
		@Override
		public void run()
		{
			Message msg;
			
			while((msg = readMessage()) != null)
			{
				System.out.println(msg.getUsername() + ": " + msg.getMessage());								
			}
			
			System.out.println("Connection to server is lost!");
			close();
			
		}
		
		private Message readMessage()
		{
						
			try
			{
				return (Message) in.readObject();
			} catch(IOException ex) {
				return null;
			} catch(ClassNotFoundException cnfex)
			{
				return null;
			}
			
		}
/*		
		private String readline()
		{
			try
			{
				return in.readUTF();
			} catch(IOException ex) {
				return null;				
			}
		}
*/	
		public void write(String s)
		{
			if (s != null)
			{
				Message msg = new Message(userName, "127.0.0.1", s, new Date());
				try {
					 out.writeObject(msg);
					 out.flush();
				} catch (IOException ex) {
					System.out.println("Connection lost! Error while client was sending message.");
					close();
				}
			}
			
		}
		
		public boolean isClosed()
		{
			return isClosed;
		}
		
		public void close() 
		{
			try {
				System.out.println("Client will close socket soon...");
				this.serverSocket.close();
				System.out.println("Socket closed!");
			} catch(IOException ioex) {}
			finally {
				isClosed = true;				
			}
		}
		
	}
	
}