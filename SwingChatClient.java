package chat;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import javax.swing.text.DefaultCaret;

public class SwingChatClient implements ActionListener, IMessageReceiver
{
	JTextField messageTextField;
	JTextArea chatText;
	NewClient client;
	public SwingChatClient(String username)
	{
		JFrame frame = new JFrame("Simple chat client");
		frame.setSize(400, 400);
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		JPanel panelHistory = new JPanel();
		JPanel panelMessage = new JPanel();
		
		chatText = new JTextArea(10,20);
		DefaultCaret caret = (DefaultCaret)chatText.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		chatText.setEditable(false);
		
		JButton jbtn1 = new JButton("Button");
		jbtn1.addActionListener(this);
		messageTextField = new JTextField(20);
		
		JScrollPane scrollPane = new JScrollPane(chatText);
		panelHistory.add(scrollPane);
		frame.add(panelHistory, BorderLayout.CENTER);
		panelMessage.add(messageTextField);
		panelMessage.add(messageTextField);
			
		panelMessage.add(jbtn1);
		
		frame.add(panelMessage, BorderLayout.SOUTH);
		//frame.add(jbtn1, BorderLayout.SOUTH);
		
		frame.pack();
		frame.setVisible(true);
		//frame.setVisible(true);
		
		// Set a default button that will automatically listen to the Enter key
		frame.getRootPane().setDefaultButton(jbtn1);
		messageTextField.requestFocusInWindow();
		client = new NewClient(username);
		client.addReceiver(this);

		
				
	}
	
	public void actionPerformed(ActionEvent evt) {
		String msg = messageTextField.getText();
		if (msg.length() > 0)
		{
			client.send(messageTextField.getText());
			messageTextField.setText("");
		}
	}
	
	public void receive(Message msg)
	{
		chatText.append(" [" + msg.getUsername() + "]: " + msg.getMessage() + "\n");
	}
	
	
	
	
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				//new SwingChatClient("test");
				new LogInFrame();
			}
		} );
	}
	
}