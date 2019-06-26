package com.seatonvalleyccapp;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * Created by julius on 27/03/2018.
 */

public class RewardUtils {
    public enum RewardType{
        DAILY_LOGIN,
        ISSUE_RESOLVED
    }
    private static final int [] MAX_PROGRESS = {100, 200, 300, 500};
    private static final int DAILY_LOGIN_REWARD = 10;
    private static final int RESOLVED_ISSUE_REWARD = 40;
    private static final long DAY_MILLS = 86400000;
    private String uid;
    private DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

    private RewardCallback rewardCallback;

    public void checkReward(final String userID, RewardType type, RewardCallback rewardCallback){
        this.rewardCallback = rewardCallback;
        uid = userID;
        switch (type){
            case DAILY_LOGIN:
                users.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserData userData = dataSnapshot.getValue(UserData.class);
                        addDailyReward(DAILY_LOGIN_REWARD, userData);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                break;

            case ISSUE_RESOLVED:
                users.child(userID).child("resolvedIssues").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        users.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                UserData userData = dataSnapshot.getValue(UserData.class);
                                if (userData.getResolvedIssues()>0){
                                    addResolvedReward(RESOLVED_ISSUE_REWARD*userData.getResolvedIssues(), userData);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                break;
        }
    }

    // user earns only one star at the time
    private void addResolvedReward(int reward, UserData userData){
        RewardInformation info = new RewardInformation();
        info.resolvedReward = true;
        info.resolvedPoints = reward;
        userData.setProgress(userData.getProgress()+ reward);
        userData.setResolvedIssues(0);
        checkStars(info, userData);
        pushData(userData);
        rewardCallback.OnRewardEarned(info);
    }
    private void addDailyReward(int reward, UserData userData) {
        if (System.currentTimeMillis()-userData.getLastLoginReward()>DAY_MILLS){
            RewardInformation info = new RewardInformation();

            userData.setLastLoginReward(System.currentTimeMillis());
            info.dailyReward=true;
            info.dailyPoints=DAILY_LOGIN_REWARD;

            userData.setProgress(userData.getProgress()+ reward);
            checkStars(info, userData);
            pushData(userData);
            rewardCallback.OnRewardEarned(info);
        }
    }

    private void checkStars(RewardInformation info, UserData userData){
        if (userData.getProgress() >= MAX_PROGRESS[userData.getStars()-1]) {
            info.starEarned = true;
            info.newStars = userData.getStars()+1;
            int newProgress = userData.getProgress()-MAX_PROGRESS[userData.getStars()-1];
            int newStars = userData.getStars()+1;
            userData.setProgress(newProgress);
            userData.setStars(newStars);
        }
    }
    private void pushData(UserData userData){
        users.child(uid).setValue(userData);
    }
}
