package com.seatonvalleyccapp.About;

import android.widget.ImageView;

/**
 * Created by V on 16/03/2018.
 */

public class ProfileIcon {
    private int profilePicture;
    private String name;
    private String role;

    public ProfileIcon(int profilePicture, String name, String role) {
        this.profilePicture = profilePicture;
        this.name = name;
        this.role = role;
    }

    public int getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(int profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
