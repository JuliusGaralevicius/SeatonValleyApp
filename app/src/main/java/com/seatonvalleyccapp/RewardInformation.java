package com.seatonvalleyccapp;

/**
 * Created by julius on 06/04/2018.
 */

class RewardInformation {
    boolean dailyReward = false;
    boolean resolvedReward = false;
    boolean starEarned = false;
    int dailyPoints;
    int resolvedPoints;
    int newStars;

    @Override
    public String toString() {
        String s = "Daily reward: " +dailyReward +"\n Star Earned: " +starEarned + "\n Daily points: " +dailyPoints;
        return s;
    }
}
