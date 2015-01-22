package chat;

import java.net.*;
import java.io.*;
import java.util.Date;
import java.util.ArrayList;

public class NewClient
{
	final static int port = 1234;
	private InetAddress address;
	private String userName;
	private ServerConnection scon;
	
	ArrayList<IMessageReceiver> receivers;


	public NewClient(String username)
	{
		try
		 {

			 
			 this.userName = username;
			 this.address = InetAddress.getLocalHost();
			 
			 Socket newsocket = new Socket(this.address, port);
			 
			 scon = new ServerConnection(newsocket);
			 receivers = new ArrayList<IMessageReceiver>();
			 		 

		 } catch (Exception exc) {
			 exc.printStackTrace();
		 }
	}

	public void send(String msg)
	{
		scon.write(msg);
	}
	
	public void addReceiver(IMessageReceiver rec)
	{
		receivers.add(rec);
	}
	
	public void removeReceiver(IMessageReceiver rec)
	{
		receivers.remove(rec);
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
				for (IMessageReceiver rec : receivers)
				{
					rec.receive(msg);
				}
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