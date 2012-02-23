package com.gemserk.highscores.client;

public class HighscoresComunicationException extends RuntimeException {

	public static enum HighscoreError {
		CommunicationError, InvalidApiKey, InvalidLeaderboard, InvalidUser;
	}
	
	
	int statusCode;
	String statusMessage;
	HighscoreError error;

	
	public HighscoresComunicationException(String message, int statusCode, String statusMessage, Throwable cause) {
		super(message, cause);
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}

	public HighscoresComunicationException(String message, int statusCode, String statusMessage) {
		this(message,statusCode,statusMessage,null);
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	
	public String getStatusMessage() {
		return statusMessage;
	}
	
	public HighscoreError getError() {
		return error;
	}
	
	@Override
	public String toString() {
		
		return super.toString() + ": " + error + " (" + statusCode + ") " + statusMessage;
	}
	
	

}
