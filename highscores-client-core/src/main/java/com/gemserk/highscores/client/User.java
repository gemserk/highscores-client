package com.gemserk.highscores.client;

public class User {

	long userId;
	String name;
	String privatekey;
	boolean guest;

	public User(long userId, String name, String privatekey, boolean guest) {
		this.userId = userId;
		this.name = name;
		this.privatekey = privatekey;
		this.guest = guest;
	}

	public long getUserId() {
		return userId;
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
