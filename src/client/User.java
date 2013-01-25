package client;


public class User extends Utilities {
	
	private String _nickname;
	
	private String _realname = "";
	
	private String _mode = "";
	
	private boolean _online = true;
	
	public User(String nick) {
		this.setNickname(nick);
	}
	
	public User(String nick, String name) {
		this.setNickname(nick);
		this.setRealname(name);
	}
	
	public void setNickname(String nick) {
		this._nickname = nick;
	}
	
	public String nickname() {
		return this._nickname;
	}
	
	public void setRealname(String name) {
		this._realname = name;
	}
	
	public String realname() {
		return this._realname;
	}
	
	public void setMode(String mode) {
		this._mode = mode;
	}
	
	public String mode() {
		return this._mode;
	}
	
	public void setOnline(boolean status) {
		this._online = status;
	}
	
	public boolean online() {
		return this._online;
	}

}
