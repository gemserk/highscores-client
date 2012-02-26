package com.gemserk.highscores.client;

import java.util.List;
import java.util.concurrent.Future;

public interface Server {

	Future<User> getNewGuestUser();

	Future<User> updateUser(final User user, final String newName);

	Future<Void> submitScore(final String leaderboard, final SubmittableScore score);

	Future<Void> submitScore(final User user, final String leaderboard, final SubmittableScore score);

	Future<List<Score>> getScores(final String leaderboard, final Range range);

	Future<List<Score>> getScores(final String leaderboard, final Range range, final int page, final int pageSize);

	void setCurrentUser(User currentUser);

	User getCurrentUser();

}