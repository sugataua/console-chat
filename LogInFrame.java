package chat;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LogInFrame extends JFrame implements ActionListener
	{
		JLabel welcome;
		JTextField inputName;
		JButton enter;
		public LogInFrame()
		{
			super("Chat Log in window");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setSize(300, 300);
			setLayout(new FlowLayout(FlowLayout.LEFT));
			welcome = new JLabel("Please write your name below:");
			inputName = new JTextField(15);
			enter = new JButton("Enter");
			
			enter.addActionListener(this);
			getRootPane().setDefaultButton(enter);
			
			add(welcome);
			add(inputName);
			add(enter);
			pack();
			setVisible(true);
			
		}
		
		public void actionPerformed(ActionEvent evt)
		{
			String name = inputName.getText();
			if (name.length() > 0)
			{
				new SwingChatClient(name);
				setVisible(false);
				
			}
		}
		
		
	}