package client;

import java.io.IOException;


public class Parser extends Utilities {
    
    private Network network;
    
    private Commands commands;
    
    public Parser(Network network, Commands commands) {
    	this.network = network;
		this.commands = commands;
    }
    
	public String parse(String line) throws IOException {
		debug(line);
		if (line.startsWith(":" + this.network.node())) {
			return this.parseServer(line);
		} else if (line.startsWith("PING")) {
			this.commands.pong(line);
		} else {
			try {
				this.parseUser(line);
			} catch (ArrayIndexOutOfBoundsException e) {
				debug(e.getStackTrace());
			}
		}
		return "";
	}
	
	public String parseServer(String l) {
		String line = this.cleanLine(l, 2, ":");
		boolean output = false;
		String res = "";
		int code = 0;
		try {
			code = Integer.parseInt(l.split(" ")[1]);
		} catch (NumberFormatException e) {}
		try {
			switch (code) {
				case 4: 	res = "Connected";
							break;
				case 5:		res = "Connected";
							break;
				case 433:	res = "Nick in use";
							break;
				case 332:	this.network.channels().get(l.split(" ")[3]).setTopic(line);
							this.network.channels().get(l.split(" ")[3]).addText("Topic for " + l.split(" ")[3] + ": " + line);
							break;
				case 353:	this.network.channels().get(l.split(" ")[4]).addNicks(line.split(" "));
							break;
				case 366:   break;
				default: 	output = true;
							break;
			}
		} catch (NullPointerException e) {
			debug(true, "EXCEPTION PARSING (server): " + l);
		}
		if (output) {
			this.network.channels().get("Status").addText(line);
		}
		return res;
	}
	
	public void parseUser(String l) throws ArrayIndexOutOfBoundsException {
		String[] lss = l.split(" ");
		String[] lsc = l.split(":");
		if (lsc[1].startsWith(this.network.user().nickname() + " ") && l.indexOf("MODE") != -1) {
			this.network.user().setMode(lss[2]);
		} else if (lsc[1].startsWith(this.network.user().nickname() + "!") && lsc[1].indexOf("JOIN") != -1) {
			if (lsc.length > 2) {
				this.network.addChannel(lsc[2]);
			} else if (lss[2].startsWith("#")) {
				this.network.addChannel(lss[2]);
			}
		} else {
			String user = lsc[1].split("!")[0];
			if (lsc[1].indexOf("JOIN") != -1) {
				String chan = lsc[2];
				this.network.channels().get(chan).addNick(user);
				this.network.channels().get(chan).addText(user + " has joined the " + chan);
			} else if (lsc[1].indexOf("PART") != -1) {
				String chan = lss[2];
				this.network.channels().get(l.split(" ")[2]).removeNick(user);
				this.network.channels().get(chan).addText(user + " has left " + chan);
			} else if (lsc[1].indexOf("QUIT") != -1) {
				this.network.removeUser(user);
			} else if (lsc[1].indexOf("NICK") != -1) {
				this.network.renameUser(user, lsc[2]);
			} else if (lsc.length > 2 && lsc[2].startsWith("\u0001ACTION")) {
				String chan = lss[2];
				if (!chan.startsWith("#")) {
					chan = "Messages";
				}
				this.network.channels().get(chan).addText(true, "* " + user + " " + this.cleanLine(l, 4, " "));
			} else if (lsc[1].indexOf("TOPIC") != -1) {
				String chan = lss[2];
				this.network.channels().get(chan).addText(user + " changed the topic of " + chan + " to: " + this.cleanLine(l, 2, ":"));
				this.network.channels().get(lss[2]).setTopic(this.cleanLine(l, 2, ":"));
			} else {
				try {
					String chan = lss[2];
					String msg = this.cleanLine(l, 2, ":");
					if (chan.startsWith("#")) {
						this.network.channels().get(chan).addText(user, msg);
					} else {
						if (l.split(" ")[1].equals("NOTICE")) {
							user = "[Notice(" + user + ")]";
						} else {
							user = "[Msg(" + user + ")]";
						}
						this.network.channels().get("Messages").addText(user, msg);
					}
				} catch (NullPointerException e) {
					debug(true, "EXCEPTION PARSING (user): " + l);
				}
			}
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
    
}
