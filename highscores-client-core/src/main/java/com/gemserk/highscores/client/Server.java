package com.gemserk.highscores.client;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

public class Server {

	

	Logger logger = LoggerFactory.getLogger(Server.class);

	private String apiKey;
	private ExecutorService executorService;
	
	private User currentUser;

	private URI baseUri;
	private static final String createGuestUrl = "/users/createGuest";
	private static final String updateUserUrl = "/users/updateUser";
	private static final String submitScoreUrl = "/leaderboards/score";
	private static final String viewScores = "/leaderboards/scores";
	
	
	private Gson gson;

	public void init(String apikey) {
		init(apikey,"http://highscores.gemserk.com/");
	}

	public void init(String apiKey, String baseUri) {
		this.apiKey = apiKey;
		this.baseUri = URI.create(baseUri);
		executorService = Executors.newSingleThreadExecutor();
		gson = new Gson();
	}

	public Future<User> getNewGuestUser() {
		return executorService.submit(new Callable<User>() {

			@Override
			public User call() throws Exception {
				HttpClient httpClient = new DefaultHttpClient();

				URI uri = URIUtils.resolve(baseUri, createGuestUrl);

				HttpGet httpget = new HttpGet(uri);

				if (logger.isDebugEnabled())
					logger.debug("CreateGuest query uri: " + httpget.getURI());

				HttpResponse response = httpClient.execute(httpget);

				StatusLine statusLine = response.getStatusLine();

				if (statusLine.getStatusCode() != HttpStatus.SC_OK)
					throw new HighscoresComunicationException("failed to create guest",statusLine.getStatusCode(), statusLine.getReasonPhrase());

				String guestUserJson = EntityUtils.toString(response.getEntity());

				if (logger.isDebugEnabled())
					logger.debug("createGuest json retrieved from server: " + guestUserJson);

				User user = gson.fromJson(guestUserJson, User.class);

				return user;
			}
		});
	}
	
	public Future<User> updateUser(final User user, final String newName ) {
		return executorService.submit(new Callable<User>() {

			@Override
			public User call() throws Exception {
				if(user.privatekey==null){
					throw new IllegalArgumentException("the privatekey must be not null");
				}
				
				HttpClient httpClient = new DefaultHttpClient();
				
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

				StatusLine statusLine = response.getStatusLine();

				if (statusLine.getStatusCode() != HttpStatus.SC_OK)
					throw new HighscoresComunicationException("failed to updateUser",statusLine.getStatusCode(), statusLine.getReasonPhrase());

				String guestUserJson = EntityUtils.toString(response.getEntity());

				if (logger.isDebugEnabled())
					logger.debug("updateUser json retrieved from server: " + guestUserJson);

				User user = gson.fromJson(guestUserJson, User.class);

				return user;
			}
		});
	}
	
	public Future<Void> submitScore(final String leaderboard, final SubmittableScore score) {
		return submitScore(currentUser,leaderboard, score);
	}
	
	
	public Future<Void> submitScore(final User user, final String leaderboard, final SubmittableScore score ) {
		return executorService.submit(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				if(user.privatekey==null){
					throw new IllegalArgumentException("the privatekey must be not null");
				}
				
				HttpClient httpClient = new DefaultHttpClient();
				
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

				StatusLine statusLine = response.getStatusLine();

				if (statusLine.getStatusCode() != HttpStatus.SC_OK)
					throw new HighscoresComunicationException("failed to submit score",statusLine.getStatusCode(), statusLine.getReasonPhrase());

				String responseText = EntityUtils.toString(response.getEntity());

				if (logger.isDebugEnabled())
					logger.debug("submit score retrieved from server: " + responseText);

				return null;
			}
		});
	}
	
	
	
	public Future<List<Score>> getScores(final String leaderboard, final Range range) {
		return getScores(leaderboard, range,-1,-1);
	}
	
	public Future<List<Score>> getScores(final String leaderboard, final Range range, final int page, final int pageSize) {
		return executorService.submit(new Callable<List<Score>>() {

			@Override
			public List<Score> call() throws Exception {
				
				HttpClient httpClient = new DefaultHttpClient();
				
				List<NameValuePair> params = new ArrayList<NameValuePair>();

				params.add(new BasicNameValuePair("apiKey", apiKey));
				params.add(new BasicNameValuePair("leaderboard", leaderboard));
				params.add(new BasicNameValuePair("range",range.key));
				if(page!=-1){
					params.add(new BasicNameValuePair("page",Integer.toString(page)));
					params.add(new BasicNameValuePair("pageSize",Integer.toString(pageSize)));
				}
				String encodedParams = URLEncodedUtils.format(params, "UTF-8");

				URI uri = URIUtils.resolve(baseUri, viewScores + "?" + encodedParams);

				HttpGet httpget = new HttpGet(uri);

				if (logger.isDebugEnabled())
					logger.debug("Submiting score query uri: " + httpget.getURI());

				HttpResponse response = httpClient.execute(httpget);

				StatusLine statusLine = response.getStatusLine();

				if (statusLine.getStatusCode() != HttpStatus.SC_OK)
					throw new HighscoresComunicationException("failed to submit score",statusLine.getStatusCode(), statusLine.getReasonPhrase());

				String scoresJson = EntityUtils.toString(response.getEntity());

				if (logger.isDebugEnabled())
					logger.debug("createGuest json retrieved from server: " + scoresJson);

				Type collectionType = new TypeToken<List<Score>>() {}.getType();
				List<Score> scores = gson.fromJson(scoresJson, collectionType);

				return scores;
			}
		});
	}
	
	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}
	
	public User getCurrentUser() {
		return currentUser;
	}

	public void close() {
		executorService.shutdown();		
	}
}
