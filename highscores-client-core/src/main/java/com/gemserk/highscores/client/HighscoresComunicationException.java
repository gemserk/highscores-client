package com.gemserk.highscores.client;

public class HighscoresComunicationException extends RuntimeException {

	public static int CustomErrorCodes = 600;
	
	public static enum HighscoreError {
		CommunicationError, InvalidApiKey, InvalidLeaderboard, InvalidUser;
	}

	int statusCode;
	String statusMessage;
	HighscoreError error;

	public HighscoresComunicationException(String message, Throwable cause) {
		super(message, cause);
		this.statusCode = -1;
		this.statusMessage = null;
		error = HighscoreError.CommunicationError;
	}

	public HighscoresComunicationException(String message, int statusCode, String statusMessage) {
		super(message);
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
		error = getErrorFromStatusCode(statusCode);
	}

	private HighscoreError getErrorFromStatusCode(int statusCode) {
		switch (statusCode) {
		case 601:
			return HighscoreError.InvalidApiKey;
		case 602:
			return HighscoreError.InvalidLeaderboard;
		case 603:
			return HighscoreError.InvalidUser;
		case 604:
			return HighscoreError.InvalidUser;
		default:
			return HighscoreError.CommunicationError;
		}
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
