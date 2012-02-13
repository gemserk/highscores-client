package com.gemserk.highscores.client;

public class Score {
	String username;
	String name;
	long score;
	
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getScore() {
		return score;
	}
	public void setScore(long score) {
		this.score = score;
	}
	
	@Override
	public String toString() {
		return "Score[" + name + " - " + score + "]"; 
	}
	
}
