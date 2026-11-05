package uga.csx370.criticalhit.models;

import java.time.LocalDate;

import uga.csx370.criticalhit.utility.Format;

/**
 * A registered user of the site.
 */
public class User {

    private final String username;
    private final String displayName;
    private final String bio;
    private final LocalDate joinedOn;

    public User(String username, String displayName, String bio, LocalDate joinedOn) {
        this.username = username;
        this.displayName = displayName;
        this.bio = bio;
        this.joinedOn = joinedOn;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBio() {
        return bio;
    }

    public LocalDate getJoinedOn() {
        return joinedOn;
    }

    public String getJoinedDisplay() {
        return Format.date(joinedOn);
    }

    public String getInitial() {
        return displayName.substring(0, 1).toUpperCase();
    }

    public String getAvatarColor() {
        return Format.color(username);
    }
}
