package chat;

import java.net.ServerSocket;
import java.net.Socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Date;

/**
 * Server part of simple console chat. 
 * @author Sergii Karpenko
 */

public class Server implements Runnable
{
	
    private boolean isWorking = true;
	private int clientCounter = 0;
	
	private int limitMessage;
	private int limitClient;
	private int port;
	
	private LinkedList<Message> lastMessages;
	private ArrayList<ClientConnection> clients;
	
	ServerSocket serverSocket;
	
	/**
	 * Entry point of Server application 
	 */	
	 public static void main(String[] args)
	 {
		 
		 // TODO Getting path to configuration file from parameters.
		 // Stub
		 ConfigParser cfgp = new ConfigParser("chat\\config.xml");
		 
		 ServerConfig config = cfgp.getServerConfig();
		  
		 new Server(config);
		 
		 //System.out.print("> ");
	
	 }
	 
	 
	/**
	 * Creates new Server object and starts server at new thread
	 * @param config Configuration of server (port and limits)
	 */	 
	 public Server(ServerConfig config)
	 {		
		this.port = config.getPort();
		this.limitClient = config.getLimitClients();
		this.limitMessage = config.getLimitMessages();	
	 
		this.serverSocket = null;
		
		lastMessages = new LinkedList<Message>();
		clients = new ArrayList<ClientConnection>();
		 
		new Thread(this).start();
		 		 
	 }
	 
	/**
	 * Server thread. It provides client connections to server.
	 */	 
	 @Override
	 public void run()
	 {
		try
		 {
			 serverSocket = new ServerSocket(port);
			 
			 System.out.println("Server is listening on port " + port);
			 System.out.println("Number of cached messages: " + limitMessage);
			 System.out.println("Maximum clients limit: " + limitClient);
			 
			 while(isWorking)
			 {
				Socket clientSocket = serverSocket.accept();				
				System.out.println("New client #" + clientCounter + " connected!\n");
				addConnection(clientSocket);
				
			 }
		} catch (IOException e)
			{
				 System.out.println("accept lookup IOException " + e);
				 ioex.printStackTrace();
		} finally {
			try {
				serverSocket.close();
			} catch (Exception ex) {
				
			}
			
		 }
	 }
	 
	 
	/**
	 * Provides log of messages (FIFO). Adds new message to log. Size of queue is limited by param in Server configuration.
	 * @param message Message to be added in log (queue)
	 */	 
	 synchronized void addMessage(Message message)
	 {
		 if (lastMessages.size() > limitMessage)
		 {
			lastMessages.removeFirst();
		 } 
			
		lastMessages.add(message);		 	 
	 }
	 
	 
	/**
	 * Creates client connection and add it to list of active connections
	 * @param s Client's socket
	 */	 	 
	 synchronized void addConnection(Socket s)
	 {
		if (clientCounter <= limitClient)
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

	/**
	 * Removes client's connection from list of active connections
	 * @param conn Client connection to be removed.
	 */	 	 
	 synchronized void removeConnection(ClientConnection conn)
	 {
		 clients.remove(conn);
		 clientCounter--;
	 }

	/**
	 * Sends message object to all clients registered among clients connections.
	 * @param msg Message
	 */	 	 
	 synchronized void broadcast(Message msg)
	 {
		 for (ClientConnection ccon : clients)
		 {
			 ccon.send(msg);
		 }
	 }

	/**
	 * ClientConnection class provides connection between server and client. Serialized class Message is used to exchange data.
	 */	 	 
	 public class ClientConnection implements Runnable 
	 {
		 private Socket socket;
		 private Server server;		 
		 private ObjectInputStream in;
		 private ObjectOutputStream out;
		 
		/**
		 * Creates input and output connection and starts
		 */	 
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
		 
		/**
		 * It's thread for listening client connection.
		 */	 		 
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

		/**
		 * Blocked reading from input stream
		 * @return Message or null. Response is null if I/O error occurred.
		 */	 		 
		private Message readMessage()
		{
						
			try
			{
				return (Message) in.readObject();
				
			} catch(IOException ex) {
				
				return null;
				
				} catch(ClassNotFoundException cnfex){
					return null;
				}
			
		}

		/**
		 * Send message to client
		 * msg Message to be sent
		 */	 
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

		/**
		 * Closes connection to client and removes it from list of active connections.
		 */	 		
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