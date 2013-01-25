package client;

import java.io.IOException;
import java.util.ArrayList;


public class Commands extends Utilities {
	
	private Network network;
	
	public Commands(Network network) {
		this.network = network;
	}
	
	public void joinChannels(ArrayList<String> channels) throws IOException {
		for (int i = 0; i < channels.size(); i++) {
			this.network.writer().write("JOIN " + channels.get(i) + "\r\n");
	        this.network.writer().flush();
		}
	}
	
	public void pong(String ping) throws IOException {
		String pong = ping.substring(6);
		debug("PONG :" + pong);
		this.network.writer().write("PONG :" + pong + "\r\n");
		this.network.writer().flush();
	}
	
	public void sendMessage(String chan, String msg) {
        try {
        	if (msg.startsWith("/")) {
				this.commands(chan, msg);
			} else {
				this.network.writer().write("PRIVMSG " + chan + " " + msg + "\r\n");
				this.network.writer().flush();
				this.network.channels().get(chan).addText(this.network.user().nickname(), msg);
			}
		} catch (IOException e) {
			debug(e.getStackTrace());
		}
	}
	
	public void sendRawMessage(String chan, String msg) {
		try {
			if (msg.startsWith("/")) {
				this.commands(chan, msg);
			} else {
				this.network.writer().write(msg + "\r\n");
				this.network.writer().flush();
				this.network.channels().get(chan).addText(msg);
			}
		} catch (IOException e) {
			debug(e.getStackTrace());
		}
	}
	
	public void commands(String chan, String msg) throws IOException {
		String[] splitMsg = msg.split(" ");
		String lMsg = msg.toLowerCase();
		if (lMsg.startsWith("/topic")) {
			this.network.writer().write("TOPIC " + chan + " :" + this.cleanLine(msg, 1, " ") + "\r\n");
		} else if (lMsg.startsWith("/msg")) {
			this.network.writer().write("PRIVMSG " + splitMsg[1] + " :" + this.cleanLine(msg, 2, " ") + "\r\n");
        } else if (lMsg.startsWith("/notice")) {
        	this.network.writer().write("NOTICE " + splitMsg[1] + " :" + this.cleanLine(msg, 2, " ") + "\r\n");
		} else if (lMsg.startsWith("/me")) {
        	this.network.writer().write("PRIVMSG " + chan + " :\u0001ACTION " + this.cleanLine(msg, 1, " ") + "\u0001\r\n");
        	this.network.channels().get(chan).addText(true, "* " + this.network.user().nickname() + " " + this.cleanLine(msg, 1, " "));
		} else if (lMsg.startsWith("/action")) {
        	this.network.writer().write("PRIVMSG " + splitMsg[1] + " :\u0001ACTION " + this.cleanLine(msg, 2, " ") + "\u0001\r\n");
        	this.network.channels().get(chan).addText(true, "* " + this.network.user().nickname() + " " + this.cleanLine(msg, 2, " "));
		} else if (lMsg.startsWith("/nick")) {
        	this.network.writer().write("NICK " + splitMsg[1] + "\r\n");
		} else if (lMsg.startsWith("/join")) {
			for (int i = 1; i < splitMsg.length; i++) {
				this.network.writer().write("JOIN " + splitMsg[i] + "\r\n");
			}
		} else if (lMsg.startsWith("/quit")) {
			this.network.writer().write("QUIT Goodbye!\r\n");
			this.network.cleanUp();
			System.exit(0);
		} else if (lMsg.startsWith("/leave") || msg.toLowerCase().startsWith("/part")) {
			String closeChan = null;
			if (!chan.equals("Status") || !chan.equals("Messages")) {
				this.network.writer().write("PART " + chan + "\r\n");
				closeChan = chan;
			} else if (splitMsg.length > 1) {
				this.network.writer().write("PART " + splitMsg[1] + "\r\n");
				closeChan = splitMsg[1];
			}
			if (!closeChan.equals("Status") && !closeChan.equals("Messages") && closeChan != null) {
				for (int i = 0; i < this.network.guiObj().tabbedPanes.getTabCount(); i++) {
					if (this.network.guiObj().tabbedPanes.getTitleAt(i).equals(closeChan)) {
						this.network.guiObj().tabbedPanes.remove(i);
					}
				}
			}
		}
		this.network.writer().flush();
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
	
}
