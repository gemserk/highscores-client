package com.gemserk.highscores.client;

public class HighscoresComunicationException extends RuntimeException {

	int statusCode;
	String statusMessage;

	
	public HighscoresComunicationException(String message, int statusCode, String statusMessage, Throwable cause) {
		super(message, cause);
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}

	public HighscoresComunicationException(String message, int statusCode, String statusMessage) {
		this(message,statusCode,statusMessage,null);
	}
	
	@Override
	public String toString() {
		
		return super.toString() + ": (" + statusCode + ") " + statusMessage;
	}

}
