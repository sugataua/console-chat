package chat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import org.xml.sax.SAXException;

public class ConfigParser
{
	final static String PORT = "port";
	final static String MAX_CLIENTS = "max_clients";
	final static String MAX_MESSAGES = "max_messages";
	
	private Document doc;
	
	public ConfigParser(String path)
	{
		try
		 {
			 File file = new File(path);
			 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();		 
			 DocumentBuilder db = dbf.newDocumentBuilder();
			 doc = db.parse(file);
			 
			 doc.getDocumentElement().normalize();
			 
		 } catch(ParserConfigurationException pcex) {
			 System.out.println("Configuration file has error!");
		 } catch(SAXException sasex) {
			 System.out.println("Parse error!");
		 } catch(IOException ioex) {
			 ioex.printStackTrace();
		 } 
	}
	
	public ServerConfig getServerConfig() {
		ServerConfig cfg = null;
		try
		{
			String port_str = doc.getElementsByTagName(PORT).item(0).getTextContent();
			String limit_msg_str = doc.getElementsByTagName(MAX_CLIENTS).item(0).getTextContent();
			String limit_cls_str = doc.getElementsByTagName(MAX_MESSAGES).item(0).getTextContent();	 
			 
			int port = Integer.parseInt(port_str);
			int mlimit = Integer.parseInt(limit_msg_str);
			int climit = Integer.parseInt(limit_cls_str);
			
			cfg = new ServerConfig(port, climit, mlimit);
		} catch(NumberFormatException nfex) {
			 System.out.println("Parse error! Check numeric values!");
		 } finally {
			 return cfg;
		 }
	}
			 
}