package client;

import java.util.ArrayList;
import javax.swing.JEditorPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;


public class Channel extends Utilities {
	
	private Network network;
	
	private String name = "";
	
	private JEditorPane chatField;
	
	private JEditorPane nickField;
	
	private JEditorPane topicField;
	
	private JScrollPane chatScrollPane;
	
	private String log = "";
	
	private String topic = "";
	
	private ArrayList<User> _users = new ArrayList<User>();
	
	public Channel(String name, Network network, Object[] widgets) {
		this.setName(name);
		this.chatField = (JEditorPane)widgets[0];
		this.nickField = (JEditorPane)widgets[1];
		this.topicField = (JEditorPane)widgets[2];
		this.chatScrollPane = (JScrollPane)widgets[3];
		this.network = network;
	}
	
	public void addText(String text) {
		text = Utilities.prettyTime() + " ~!~ " + text;
		Document doc = this.chatField.getDocument();
		try {
			doc.insertString(doc.getLength(), text + "\n", null);
			this.appendLog(text);
			JScrollBar bar = this.chatScrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
		} catch(BadLocationException e) {
			debug(e.getStackTrace());
		}
	}
	
	public void addText(boolean pre, String text) {
		text = Utilities.prettyTime() + " " + text;
		Document doc = this.chatField.getDocument();
		try {
			doc.insertString(doc.getLength(), text + "\n", null);
			this.appendLog(text);
			JScrollBar bar = this.chatScrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
		} catch(BadLocationException e) {
			debug(e.getStackTrace());
		}
	}
	
	public void addText(String user, String msg) {
		Document doc = this.chatField.getDocument();
		try {
			doc.insertString(doc.getLength(), Utilities.prettyTime() + " " + user + " : " + msg + "\n", null);
			this.appendLog(Utilities.prettyTime() + " " + user + " : " + msg);
			JScrollBar bar = this.chatScrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
		} catch(BadLocationException e) {
			debug(e.getStackTrace());
		}
	}
	
	public void addNicks(String[] nicks) {
		String users = "";
		for (int i = 0; i < nicks.length; i++) {
			String nick = nicks[i];
			if (nick.startsWith("@") || nick.startsWith("~") || nick.startsWith("&")) {
				users = users + "[" + nick + "] ";
				nick = nick.substring(1);
			} else {
				users = users + "[ " + nick + "] ";
			}
			User user = new User(nick);
			this.network.addUser(nick, user);
			this.users().add(user);
		}
		this.addText(true, "[Users " + this.name() + "]");
		this.addText(true, users);
		this.drawNickField();
	}
	
	public void addNick(String nick) {
		if (nick.startsWith("@") || nick.startsWith("~") || nick.startsWith("&")) {
			nick = nick.substring(1);
		}
		User user = new User(nick);
		this.network.addUser(nick, user);
		this.users().add(user);
		this.drawNickField();
	}
	
	public void removeNick(String nick) {
		for (int i = 0; i < this.users().size(); i++) {
			if (this.users().get(i).equals(nick)) {
				this.users().remove(i);
			}
		}
		this.drawNickField();
	}
	
	public void drawNickField() {
		this.nickField.setText("");
		for (int i = 0; i < this.users().size(); i++) {
			try {
				Document doc = this.nickField.getDocument();
				doc.insertString(doc.getLength(), this.users().get(i).nickname() + "\n", null);
			} catch(BadLocationException e) {
				debug(e.getStackTrace());
			}
		}
	}
	
	public void appendLog(String text) {
		this.log += text + "\n";
	}
	
	public String log() {
		return this.log;
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
		this.topicField.setText("Topic: " + topic);
	}
	
	public String topic() {
		return this.topic;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String name() {
		return this.name;
	}
	
	public ArrayList<User> users() {
		return this._users;
	}

}
