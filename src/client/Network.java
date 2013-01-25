package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;


public class Network extends Thread {
	
	private Socket _sock = null;

	private BufferedWriter _writer = null;

	private BufferedReader _reader = null;
	
	private String _server = "irc.freenode.net";
	
	private int _port = 6667;
	
	private String _node = null;
	
	private ArrayList<String> _initJoinChannels = new ArrayList<String>();
	
	private HashMap<String, Channel> _channels = new HashMap<String, Channel>();
	
	private HashMap<String, User> _users = new HashMap<String, User>();
    
    private User _user;
    
    private ClientGUI _guiObj;
    
    private Parser parser;
    
    public Commands commands;
	
	public Network(ClientGUI guiObj) {
		Utilities.debug("Initialized IRC Object...");
		this.setGuiObj(guiObj);
		this.commands = new Commands(this);
        this.parser = new Parser(this, this.commands);
	}
	
	public Network(ClientGUI guiObj, String server, int port, String nick, String name) {
		Utilities.debug("Initialized IRC Object...");
		this.setGuiObj(guiObj);
		this.setServer(server);
		this.setPort(port);
		this.setUser(nick, name);
		this.commands = new Commands(this);
        this.parser = new Parser(this, this.commands);
	}
	
	public void connect() {
		try {
			this.setSock(new Socket(this.server(), this.port()));
			this.setWriter(new BufferedWriter(new OutputStreamWriter(this.sock().getOutputStream())));
			this.setReader(new BufferedReader(new InputStreamReader(this.sock().getInputStream())));
			this.ident();
			this.commands.joinChannels(this.initJoinChannels());
			this.listen();
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(null, "Unknown Host!");
			this.guiObj().getIRCSettings();
			this.guiObj().tabbedPanes = new JTabbedPane();
			Utilities.debug(e.getStackTrace());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "An error occured with the connection!");
			Utilities.debug(e.getStackTrace());
		}
	}
	
	public void ident() throws IOException {
		this.writer().write("NICK " + this.user().nickname() + "\r\n");
		this.writer().write("USER " + this.user().realname() + " 8 * : Java IRC Client\r\n");
		this.writer().flush();
        
		String line = null;
        while ((line = this.reader().readLine()) != null) {
        	// Store the name of the server node, so we can distinguish it later on
        	if (this.node() == null && line.indexOf("NOTICE") != -1) {
        		this.setNode(line.split(" ")[0].substring(1));
        		Utilities.debug("NODE SET TO --> " + this.node());
        	} else {
        		String parseResult = this.parser.parse(line);
            	if (parseResult == "Connected") {
            		Utilities.debug("Successfully established connection to the IRC server.");
                    break;
            	} else if (parseResult == "Nick in use") {
            		Utilities.debug("Nickname is already in use.");
                    this.user().setNickname(this.user().nickname() + "_");
                    this.ident();
                    break;
            	}
        	}
        }
	}
	
	public void listen() throws IOException {
		String line = null;
        while ((line = this.reader().readLine()) != null) {
        	this.parser.parse(line);
        }
	}
	
	public String cleanLine(String l, int start, String sep) {
		String[] splitLine = l.split(sep);
		String line = "";
		for (int i = start; i < splitLine.length; i++) {
			String prepend = sep;
			if (i == start) {
				prepend = "";
			}
			line = line + prepend + splitLine[i];
		}
		if (line.startsWith(":")) {
			line = line.substring(1);
		}
		return line;
	}
	
	public void cleanUp() {
		Utilities.debug("Closing IRC Object...");
		if (this.sock() != null) {
			try {
				this.writer().write("QUIT Goodbye!\r\n");
				this.setSock(null);
				this.setWriter(null);
				this.setReader(null);
				Utilities.debug("Closed IRC Object...");
			} catch (IOException e) {
				Utilities.debug("Error closing IRC Object...");
				Utilities.debug(e.getStackTrace());
			}
		} else {
			Utilities.debug("Closed IRC Object...");
		}
	}
	
	public void run() {
		this.connect();
	}
	
	public void addUser(String name, User user) {
		this.users().put(name, user);
	}
	
	public void removeUser(String name) {
		Iterator<Entry<String, Channel>> iter = this.channels().entrySet().iterator();
	    while (iter.hasNext()) {
	        Entry<String, Channel> pairs = iter.next();
	        Channel val = (Channel)pairs.getValue();
	        val.removeNick(name);
	        val.addText(name + " has quit");
	    }
	    this.users().remove(name);
	}
	
	public void renameUser(String oldName, String newName) {
		if (this.user().nickname().equals(oldName)) {
			this.user().setNickname(newName);
		}
		User user = new User(newName);
		if (this.users().containsKey(oldName)) {
			user = this.users().remove(oldName);
		}
		this.users().put(newName, user);
		this.users().get(newName).setNickname(newName);

		Iterator<Entry<String, Channel>> iter = this.channels().entrySet().iterator();
	    while (iter.hasNext()) {
	        Entry<String, Channel> pairs = iter.next();
	        Channel chan = (Channel)pairs.getValue();
	        chan.drawNickField();
	        chan.addText(oldName + " is now known as " + newName);
	    }
	}
	
	public void addInitJoinChannel(String chan) {
		initJoinChannels().add(chan);
	}
	
	public ArrayList<String> initJoinChannels() {
		return this._initJoinChannels;
	}
	
	public void addChannel(String chan) {
		this._channels.put(chan, new Channel(
        	chan,
        	this,
        	this.guiObj().insertComponentsInTab(chan)
        ));
	}
	
	public HashMap<String, Channel> channels() {
		return this._channels;
	}
	
	public void setUser(String nick, String name) {
		if (this.user() == null) {
			this._user = new User(nick, name);
		} else {
			this.user().setNickname(nick);
			this.user().setRealname(name);
		}
	}
	
	public User user() {
		return this._user;
	}
	
	public HashMap<String, User> users() {
		return this._users;
	}
	
	public void setServer(String server) {
		this._server = server;
	}
	
	public String server() {
		return this._server;
	}
	
	public void setGuiObj(ClientGUI obj) {
		this._guiObj = obj;
	}
	
	public ClientGUI guiObj() {
		return this._guiObj;
	}
	
	public void setPort(int port) {
		this._port = port;
	}
	
	public int port() {
		return this._port;
	}
	
	public void setNode(String node) {
		this._node = node;
	}
	
	public String node() {
		return this._node;
	}
	
	public void setSock(Socket sock) {
		this._sock = sock;
	}
	
	public Socket sock() {
		return this._sock;
	}
	
	public void setWriter(BufferedWriter writer) {
		this._writer = writer;
	}
	
	public BufferedWriter writer() {
		return this._writer;
	}
	
	public void setReader(BufferedReader reader) {
		this._reader = reader;
	}
	
	public BufferedReader reader() {
		return this._reader;
	}
	
}
