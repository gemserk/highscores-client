package com.gemserk.highscores.client;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ServerHttpImpl implements Server {

	private static final String DEFAULT_SERVER_URI = "http://highscores.gemserk.com/";

	Logger logger = LoggerFactory.getLogger(ServerHttpImpl.class);

	private String apiKey;
	private ExecutorService executorService;

	private User currentUser;

	private URI baseUri;
	private static final String createGuestUrl = "/users/createGuest";
	private static final String updateUserUrl = "/users/updateUser";
	private static final String submitScoreUrl = "/leaderboards/score";
	private static final String viewScores = "/leaderboards/scores";
	private HttpClient httpClient;

	private Gson gson;

	public ServerHttpImpl(String apiKey, ExecutorService executorService) {
		this(apiKey, DEFAULT_SERVER_URI, executorService);
	}

	public ServerHttpImpl(String apiKey, String baseUri, ExecutorService executorService) {
		this.apiKey = apiKey;
		this.baseUri = URI.create(baseUri);
		this.gson = new Gson();
		this.httpClient = new DefaultHttpClient();
		this.executorService = executorService;
	}

	@Override
	public Future<User> getNewGuestUser() {
		return executorService.submit(new Callable<User>() {

			@Override
			public User call() throws Exception {

				try {
					URI uri = URIUtils.resolve(baseUri, createGuestUrl);

					HttpGet httpget = new HttpGet(uri);
					httpget.setHeader("accept", "application/json");

					if (logger.isDebugEnabled())
						logger.debug("CreateGuest query uri: " + httpget.getURI());

					HttpResponse response = httpClient.execute(httpget);

					handleError("Error while creating guest user", response);

					String guestUserJson = EntityUtils.toString(response.getEntity());

					if (logger.isDebugEnabled())
						logger.debug("createGuest json retrieved from server: " + guestUserJson);

					User user = gson.fromJson(guestUserJson, User.class);

					return user;
				} catch (HighscoresComunicationException exception) {
					throw exception;
				} catch (Exception exception) {
					throw new HighscoresComunicationException("Error while creating guest user", exception);
				}
			}

		});
	}

	@Override
	public Future<User> updateUser(final User user, final String newName) {
		return executorService.submit(new Callable<User>() {

			@Override
			public User call() throws Exception {
				try {
					if (user.privatekey == null) {
						throw new IllegalArgumentException("the privatekey must be not null");
					}

					List<NameValuePair> params = new ArrayList<NameValuePair>();

					params.add(new BasicNameValuePair("userId", Long.toString(user.userId)));
					params.add(new BasicNameValuePair("privatekey", user.privatekey));
					params.add(new BasicNameValuePair("newName", newName));

					String encodedParams = URLEncodedUtils.format(params, "UTF-8");

					URI uri = URIUtils.resolve(baseUri, updateUserUrl + "?" + encodedParams);

					HttpGet httpget = new HttpGet(uri);

					if (logger.isDebugEnabled())
						logger.debug("Submiting score query uri: " + httpget.getURI());

					HttpResponse response = httpClient.execute(httpget);

					handleError("Error while updating user", response);

					String guestUserJson = EntityUtils.toString(response.getEntity());

					if (logger.isDebugEnabled())
						logger.debug("updateUser json retrieved from server: " + guestUserJson);

					User user = gson.fromJson(guestUserJson, User.class);

					return user;
				} catch (HighscoresComunicationException exception) {
					throw exception;
				} catch (Exception exception) {
					throw new HighscoresComunicationException("Error while updating user", exception);
				}
			}
		});
	}

	@Override
	public Future<Void> submitScore(final String leaderboard, final SubmittableScore score) {
		return submitScore(currentUser, leaderboard, score);
	}

	@Override
	public Future<Void> submitScore(final User user, final String leaderboard, final SubmittableScore score) {
		return executorService.submit(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				try {
					if (user.privatekey == null) {
						throw new IllegalArgumentException("the privatekey must be not null");
					}

					List<NameValuePair> params = new ArrayList<NameValuePair>();

					params.add(new BasicNameValuePair("apiKey", apiKey));
					params.add(new BasicNameValuePair("leaderboard", leaderboard));
					params.add(new BasicNameValuePair("userId", Long.toString(user.userId)));
					params.add(new BasicNameValuePair("privatekey", user.privatekey));
					params.add(new BasicNameValuePair("score", Long.toString(score.score)));

					String encodedParams = URLEncodedUtils.format(params, "UTF-8");

					URI uri = URIUtils.resolve(baseUri, submitScoreUrl + "?" + encodedParams);

					HttpGet httpget = new HttpGet(uri);

					if (logger.isDebugEnabled())
						logger.debug("Submiting score query uri: " + httpget.getURI());

					HttpResponse response = httpClient.execute(httpget);

					handleError("Error while submitting score", response);

					String responseText = EntityUtils.toString(response.getEntity());

					if (logger.isDebugEnabled())
						logger.debug("submit score retrieved from server: " + responseText);

					return null;
				} catch (HighscoresComunicationException exception) {
					throw exception;
				} catch (Exception exception) {
					throw new HighscoresComunicationException("Error while submitting score", exception);
				}
			}
		});
	}

	@Override
	public Future<List<Score>> getScores(final String leaderboard, final Range range) {
		return getScores(leaderboard, range, -1, -1);
	}

	@Override
	public Future<List<Score>> getScores(final String leaderboard, final Range range, final int page, final int pageSize) {
		return executorService.submit(new Callable<List<Score>>() {

			@Override
			public List<Score> call() throws Exception {

				try {

					List<NameValuePair> params = new ArrayList<NameValuePair>();

					params.add(new BasicNameValuePair("apiKey", apiKey));
					params.add(new BasicNameValuePair("leaderboard", leaderboard));
					params.add(new BasicNameValuePair("range", range.key));
					if (page != -1) {
						params.add(new BasicNameValuePair("page", Integer.toString(page)));
						params.add(new BasicNameValuePair("pageSize", Integer.toString(pageSize)));
					}
					String encodedParams = URLEncodedUtils.format(params, "UTF-8");

					URI uri = URIUtils.resolve(baseUri, viewScores + "?" + encodedParams);

					HttpGet httpget = new HttpGet(uri);

					if (logger.isDebugEnabled())
						logger.debug("Submiting score query uri: " + httpget.getURI());

					HttpResponse response = httpClient.execute(httpget);
					handleError("Error while getting scores", response);

					String scoresJson = EntityUtils.toString(response.getEntity());

					if (logger.isDebugEnabled())
						logger.debug("getScores json retrieved from server: " + scoresJson);

					Type collectionType = new TypeToken<List<Score>>() {
					}.getType();
					List<Score> scores = gson.fromJson(scoresJson, collectionType);

					return scores;
				} catch (HighscoresComunicationException exception) {
					throw exception;
				} catch (Exception exception) {
					throw new HighscoresComunicationException("Error while getting scores", exception);
				}
			}
		});
	}

	@Override
	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	@Override
	public User getCurrentUser() {
		return currentUser;
	}

	private void handleError(String where, HttpResponse response) throws IOException {

		StatusLine statusLine = response.getStatusLine();

		int statusCode = statusLine.getStatusCode();
		if (statusCode == HttpStatus.SC_OK)
			return;

		if (statusCode == HighscoresComunicationException.CustomErrorCodes) {
			ErrorDTO errorDTO = gson.fromJson(EntityUtils.toString(response.getEntity()), ErrorDTO.class);
			throw new HighscoresComunicationException(where, errorDTO.errorCode, errorDTO.message);
		} else {
			throw new HighscoresComunicationException(where, statusCode, statusLine.getReasonPhrase());
		}
	}

}
