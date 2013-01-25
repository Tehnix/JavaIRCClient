package client;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class ConnectGUI extends JFrame {

	/**
	 * Just a default serialization UID
	 */
	private static final long serialVersionUID = 1L;

	public ConnectGUI(ClientGUI obj) {
		final ClientGUI guiObj = obj;
		JComponent contentPane = new JPanel();
		GridBagConstraints c = new GridBagConstraints();
		contentPane.setLayout(new GridBagLayout());
		final JButton connectBtn = new JButton("Connect");
		final JButton quitBtn = new JButton("Quit");
		final JTextField serverField = new JTextField("irc.freenode.net");
		final JTextField portField = new JTextField("6667");
		final JTextField nickField = new JTextField("MrJava");
		final JTextField nameField = new JTextField("JClient");
		final JTextField chanField = new JTextField("#lobby");
		
		connectBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String s = serverField.getText();
				String n = nickField.getText();
				String rn = nameField.getText();
				String c = chanField.getText();
				if (s.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please specify a server!");
				} else if (n.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please specify a nickname!");
				} else if (rn.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please choose a real name!");
				} else {
					try {
						int p = Integer.parseInt(portField.getText());
						guiObj.initializeUI(s, p, n, rn, c);
						dispose();
					} catch (NumberFormatException e) {
						JOptionPane.showMessageDialog(null, "Invalid port (must be a number)!");
					}
				}
			}
		});
		quitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				guiObj.cleanUp();
			}
		});
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 10, 0, 5);
		c.ipady = 0;
		c.weightx = 1;
		c.weighty = 1d;
		c.gridy = 0;
		c.gridx = 0;
		contentPane.add(new JLabel("Server: "), c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 10, 0, 5);
		c.weightx = 20;
		c.weighty = 1d;
		c.gridy = 0;
		c.gridx = 1;
		contentPane.add(serverField, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 10, 0, 5);
		c.weightx = 1d;
		c.weighty = 1d;
		c.gridy = 1;
		c.gridx = 0;
		contentPane.add(new JLabel("Port: "), c);
		
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 10, 0, 5);
		c.weightx = 1d;
		c.weighty = 1d;
		c.gridy = 1;
		c.gridx = 1;
		contentPane.add(portField, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 10, 0, 5);
		c.weightx = 1d;
		c.weighty = 1d;
		c.gridy = 2;
		c.gridx = 0;
		contentPane.add(new JLabel("Nickname: "), c);
		
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 10, 0, 5);
		c.gridy = 2;
		c.gridx = 1;
		contentPane.add(nickField, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 10, 0, 5);
		c.gridy = 3;
		c.gridx = 0;
		contentPane.add(new JLabel("Real Name: "), c);
		
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 10, 0, 5);
		c.gridy = 3;
		c.gridx = 1;
		contentPane.add(nameField, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 10, 0, 5);
		c.gridy = 4;
		c.gridx = 0;
		contentPane.add(new JLabel("Channels: "), c);
		
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 10, 0, 5);
		c.gridy = 4;
		c.gridx = 1;
		contentPane.add(chanField, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.PAGE_END;
		c.insets = new Insets(0, 10, 0, 5);
		c.gridy = 5;
		c.gridx = 0;
		contentPane.add(quitBtn, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.PAGE_END;
		c.insets = new Insets(0, 10, 0, 5);
		c.gridy = 5;
		c.gridx = 1;
		contentPane.add(connectBtn, c);
		
		this.setTitle("IRC Settings");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.getContentPane().add(contentPane);
		this.setSize(350, 210);
		this.setResizable(false);
		this.setVisible(true);
	}
	
}
