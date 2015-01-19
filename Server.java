package chat;

import java.net.*;

import java.io.*;
//import java.io.File;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Date;


public class Server
{
	
    private boolean isWorking = true;
	private int clientCounter = 0;
	private static LinkedList<Message> lastMessages;
	
	private int limitMessage;
	private int limitClient;
	private int port;
	
	
	
	 public static void main(String[] args)
	 {
		 
		 ConfigParser cfgp = new ConfigParser("chat\\config.xml");
		  
		 new Server(cfgp.getServerConfig());
	
	 }
	 
	 
	 //public class ClientConnections implements
	 
	 private ArrayList<ClientConnection> clients = new ArrayList<ClientConnection>();
	 
	 public Server(ServerConfig cfg)
	 {		
		this.port = cfg.getPort();
		this.limitClient = cfg.getLimitClients();
		this.limitMessage = cfg.getLimitMessages();
		
	 
		 ServerSocket serverSocket = null;
		 lastMessages = new LinkedList<Message>();
		 try
		 {
			 serverSocket = new ServerSocket(port);
			 System.out.println("Server is listening on port " + port);
			 System.out.println("Number of cached messages: " + limitMessage);
			 System.out.println("Maximum clients limit: " + limitClient);
			 while(isWorking)
			 {
				Socket clientSocket = serverSocket.accept();
				//new Thread(new SocketThread(clientSocket)).start();
				System.out.println("New client #" + clientCounter + " connected!\n");
				addConnection(clientSocket);
				
			 }
		} catch (IOException e)
			{
				 System.out.println("accept lookup IOException " + e);
				 //ioex.printStackTrace();
		} finally {
			try {
				serverSocket.close();
			} catch (Exception ex) {
				
			}
			
		 }
		 		 
	 }
	 

	 synchronized void addMessage(Message message)
	 {
		 if (lastMessages.size() < limitMessage)
		 {
			lastMessages.add(message);
		 } else {
			lastMessages.removeFirst();
			lastMessages.add(message);
		 }		 
	 }
	 
	 
	 
	 synchronized void addConnection(Socket s)
	 {
		if (clientCounter < limitClient)
		{
			ClientConnection conn = new ClientConnection(this, s);		
			for (Message msg : lastMessages) 
			{
				conn.send(msg);
			}
			clients.add(conn);
			clientCounter++;
		}
		
	 }
	 
	 synchronized void removeConnection(ClientConnection conn)
	 {
		 clients.remove(conn);
		 clientCounter--;
	 }
	 
	 synchronized void broadcast(Message msg)
	 {
		 for (ClientConnection ccon : clients)
		 {
			 ccon.send(msg);
		 }
	 }
	 
	 public class ClientConnection implements Runnable 
	 {
		 private Socket socket;
		 private Server server;		 
		 private ObjectInputStream in;
		 private ObjectOutputStream out;
		 
		 public ClientConnection(Server server, Socket socket)
		 {
			 try
			 {
				 this.server = server;
				 this.socket = socket;
				 
				 InputStream sin = socket.getInputStream();
				 OutputStream sout = socket.getOutputStream();
				 
				 // Only this order of creation ObjectOutputStream and ObjectInputStream is correct!
				 this.out = new ObjectOutputStream(sout);
				 this.in = new ObjectInputStream(sin);
				 
				 
				 new Thread(this).start();
			 } catch(IOException ioex) {
				System.out.println("failed ClientConnection " + ioex);
			}
		 }
		 
		 
		 @Override
		 public void run()
		 {
			Message msg;
			
			while((msg = readMessage()) != null)
			{
				System.out.println(msg.getUsername() + ": " + msg.getMessage());
				addMessage(msg);
				broadcast(msg);
			}
			
			System.out.println("Client disconnected!");
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
		
		public void send(Message msg)
		{
			try {
				 out.writeObject(msg);
				 out.flush();
			} catch (IOException ex) {
				System.out.println("Connection lost! Error while client was sending message.");
				close();
			}
			
		}
		
		public void close() 
		{
			server.removeConnection(this);
			try {
				System.out.println("Closing connection.");
				this.socket.close();
				System.out.println("Socket closed!");
			} catch(IOException ioex) {}
		}
		 
	 }
	 

}