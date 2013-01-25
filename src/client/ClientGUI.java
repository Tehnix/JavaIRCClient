package client;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class ClientGUI extends JFrame {
	
	/**
	 * Just a default serialization UID
	 */
	private static final long serialVersionUID = 1L;

	final private static String windowName = "Java IRC Client";

	final private static int[] windowSize = {600, 432};
	
	final private static int[] minimumWindowSize = {600, 432};
	
	final private static int chatHeight = 250;
	
	private ArrayList<String> history = new ArrayList<String>();
	
	private int historyPosition = -1;
	
	private Network network;
	
	public JTabbedPane tabbedPanes = new JTabbedPane();
	
	public ClientGUI() {
		this.getIRCSettings();
		this.setTitle(windowName);
		this.setSize(windowSize[0], windowSize[1]);
		this.setMinimumSize(new Dimension(minimumWindowSize[0], minimumWindowSize[1]));
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(true);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				cleanUp();
			}
		});
	}
	
	public void getIRCSettings() {
		new ConnectGUI(this);
	}
	
	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new ClientGUI();
            }
        });
	}
	
	public void initializeUI(String server, int port, String nick, String name, String channels) {
		this.network = new Network(this, server, port, nick, name);
		this.network.addChannel("Status");
		this.network.addChannel("Messages");
		String[] chans = channels.split(",");
		for (int i = 0; i < chans.length; i++) {
			String channel = chans[i].trim();
			if (!channel.startsWith("#")) {
				channel = "#" + channel;
			} 
			this.network.addInitJoinChannel(channel);
		}
		this.network.start();
		this.getContentPane().add(this.tabbedPanes);
		this.setVisible(true);
	}
	
	public Object[] insertComponentsInTab(String tabName) {
		final String channel = tabName;
		JComponent contentPane = new JPanel();
		contentPane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		final JEditorPane chatField = new JEditorPane();
		final JScrollPane chatScrollPane = new JScrollPane(chatField);
		final JEditorPane nicknameField = new JEditorPane();
		final JScrollPane nickScrollPane = new JScrollPane(nicknameField);
		final JEditorPane topicField = new JEditorPane();
		final JTextField messageField = new JTextField(15);
		final JButton sendMsg = new JButton("Send");
		
		topicField.setOpaque(false);
		if (tabName != "Status") {
			topicField.setText("Topic: ");
		}
		
		contentPane.setLayout(new GridBagLayout());
		chatField.setEditorKit(Styles.kit);
		chatField.setDocument(Styles.doc());
		chatField.setContentType("text/html");
		chatField.setEditable(false);

		nicknameField.setEditable(false);
		
		topicField.setEditable(false);
		
		sendMsg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				sendMessage(channel, messageField, chatField);
			}
		});
		messageField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				sendMessage(channel, messageField, chatField);
			}
		});
		messageField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 38) { // Up arrow
					if (historyPosition < 0 && !messageField.getText().isEmpty()) {
						appendHistory(messageField.getText());
						historyPosition = historyPosition + 1;
					}
					messageField.setText(getPreviousHistory());
				} else if (e.getKeyCode() == 40) { // Down arrow
					messageField.setText(getNextHistory());
				}
			}
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}
		});
		
		
		// Insert the elements with proper grid constraints
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 10, 0, 5);
		c.gridwidth = 2;
		c.ipady = 0;
		c.gridy = 0;
		contentPane.add(topicField, c);
		
		c.gridwidth = 1; // reset
		if (tabName.equals("Messages")) {
			c.gridwidth = 2;
		}

		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 10, 5, 5);
		c.ipady = chatHeight;
		c.weightx = 1d;
		c.weighty = 1d;
		c.gridx = 0;
		c.gridy = 1;
		contentPane.add(chatScrollPane, c);
		
		c.gridwidth = 1; // reset
		
		if (!tabName.equals("Messages")) {
			c.fill = GridBagConstraints.BOTH;
			c.insets = new Insets(5, 5, 5, 10);
			c.ipady = chatHeight;
			c.weightx = 1d;
			c.weighty = 1d;
			c.gridx = 1;
			c.gridy = 1;
			contentPane.add(nickScrollPane, c);
		}
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.PAGE_END;
		c.insets = new Insets(0, 5, 10, 0);
		c.ipady = 0;
		c.weightx = 20;
		c.gridx = 0;
		c.gridy = 2;
		contentPane.add(messageField, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.PAGE_END;
		c.insets = new Insets(0, 0, 10, 0);
		c.ipady = 0;
		c.weightx = 1;
		c.gridx = 1;
		c.gridy = 2;
		contentPane.add(sendMsg, c);
		
		
		Object[] widgets = {
				chatField,
				nicknameField,
				topicField,
				chatScrollPane
		};
		this.tabbedPanes.add(tabName, contentPane);
		return widgets;
	}
	
	public void sendMessage(String channel, JTextField msgField, JEditorPane chatField) {
		String msg = msgField.getText();
		if (!msg.isEmpty()) {
			msgField.setText("");
			if (channel.equals("Status")) {
				this.network.commands.sendRawMessage(channel, msg);
			} else {
				this.network.commands.sendMessage(channel, msg);
			}
		}
		this.appendHistory(msg);
		this.historyPosition = -1;
	}
	
	public void appendHistory(String text) {
		if (!text.isEmpty()) {
			ArrayList<String> tmpHistory = new ArrayList<String>();
			tmpHistory.add(0, text);
			for (int i = 0; i < this.history.size() && i < 30; i++) {
				tmpHistory.add(i+1, this.history.get(i));
			}
			this.history = tmpHistory;
		}
	}
	
	public String getPreviousHistory() {
		String text = "";
		if (this.historyPosition < this.history.size() - 1) {
			this.historyPosition = this.historyPosition + 1;
		}
		if (this.history.size() > this.historyPosition) {
			text = this.history.get(this.historyPosition);
		}
		return text;
	}
	
	public String getNextHistory() {
		String text = "";
		this.historyPosition = this.historyPosition - 1;
		if (this.historyPosition < 0) {
			this.historyPosition = -1;
		} else {
			if (this.history.size() > this.historyPosition) {
				text = this.history.get(this.historyPosition);
			}
		}
		return text;
	}
	
	public void cleanUp() {
		if (this.network != null) {
			this.network.cleanUp();
		}
		System.exit(0);
	}
	
}
