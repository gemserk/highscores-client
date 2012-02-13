package com.gemserk.highscores.client;

public class User {

	String username;
	String name;
	String privatekey;
	boolean guest;

	public User(String username, String name, String privatekey, boolean guest) {
		this.username = username;
		this.name = name;
		this.privatekey = privatekey;
		this.guest = guest;
	}

	public String getUsername() {
		return username;
	}

	public String getName() {
		return name;
	}

	public String getPrivatekey() {
		return privatekey;
	}

	public boolean isGuest() {
		return guest;
	}
}
