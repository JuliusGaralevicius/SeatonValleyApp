package com.seatonvalleyccapp;

/**
 * Created by julius on 14/03/2018.
 */

public class UserData {
    public String uid;
    public String name;
    public String surname;
    public String email;
    public long registerDate;
    public int stars;
    public int progress;
    public int postsAvailable;
    public long lastLoginReward;
    public String postcode;
    public String status = "";
    public long markerUsed;  // timestamp when user first had less than maximum number of posts available
    public int resolvedIssues;

    public UserData(String name, String surname, String email, long registerDate,
                    int stars, int progress, int postsAvailable, String postcode, long markerUsed, String status) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.registerDate = registerDate;
        this.stars = stars;
        this.progress = progress;
        this.postsAvailable = postsAvailable;
        this.postcode = postcode;
        this.markerUsed = markerUsed;
    }

    public long getMarkerUsed() {
        return markerUsed;
    }
    public void setMarkerUsed(long markerUsed) {
        this.markerUsed = markerUsed;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
    public int getResolvedIssues() {
        return resolvedIssues;
    }

    public void setResolvedIssues(int resolvedIssues) {
        this.resolvedIssues = resolvedIssues;
    }
    public UserData() {}
    public long getLastLoginReward() {
        return lastLoginReward;
    }
    public void setLastLoginReward(long lastLoginReward) {
        this.lastLoginReward = lastLoginReward;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(long registerDate) {
        this.registerDate = registerDate;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getPostsAvailable() {
        return postsAvailable;
    }

    public void setPostsAvailable(int postsAvailable) {
        this.postsAvailable = postsAvailable;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}