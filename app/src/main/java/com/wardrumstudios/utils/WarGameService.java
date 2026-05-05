package com.wardrumstudios.utils;
public class WarGameService implements WarActivityLifecycleListener {
    public void onStart() {}
    public void onResume() {}
    public void onPause() {}
    public void onStop() {}
    public void onDestroy() {}
    public void onSaveInstanceState(android.os.Bundle outState) {}
    public void onActivityResult(int request, int response, android.content.Intent data) {}
    public boolean isSignedIn() { return false; }
    public void beginUserInitiatedSignIn() {}
    public void signOut() {}
    public String getPlayerName() { return ""; }
    public void unlockAchievement(String id) {}
    public void showAchievements() {}
    public void incrementAchievement(String id, int numSteps) {}
    public void showLeaderboards() {}
    public void showLeaderboard(String gameLeaderboardId) {}
    public void submitScore(String gameLeaderboardId, long score) {}
    public void LoadScoreboard(String gameLeaderboardId, int numResuts, int friendCollection, int timeSpan) {}
    public void notifyScoresLoaded(int queryId, java.util.List scoreList) {}
    public void RequestPlayersScore(String leaderboardID, int timeSpan, int collection) {}
    public void RequestPlayerImage(String playerID) {}
    public void SendSnapshot(String name, byte[] data, String desc, int playtime, android.graphics.Bitmap bitmap) {}
    public void QuerySnapshot(String name) {}
}