package com.gemserk.highscores.client;

public class User {

	long userId;
	String name;
	String privatekey;

	public User(long userId, String name, String privatekey) {
		this.userId = userId;
		this.name = name;
		this.privatekey = privatekey;
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
}
