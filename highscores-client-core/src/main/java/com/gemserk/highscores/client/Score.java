package com.gemserk.highscores.client;

public class Score {
	long userId;
	String name;
	long score;

	public Score(long userId, String name, long score) {
		this.userId = userId;
		this.name = name;
		this.score = score;
	}

	public long getUserId() {
		return userId;
	}
	
	public void setUserId(long userId) {
		this.userId = userId;
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
		return "Score[(" + userId + ") " + name + " - " + score + "]";
	}

}
