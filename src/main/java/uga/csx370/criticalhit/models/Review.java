package uga.csx370.criticalhit.models;

import java.time.LocalDate;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import uga.csx370.criticalhit.utility.Format;

/**
 * A review a user wrote for a game.
 */
public class Review {

    private final int reviewId;
    private final int gameId;
    private final String gameTitle;
    private final String username;
    private int rating;
    private String title;
    private String body;
    private final LocalDate createdAt;
    private boolean edited;
    // Usernames of people who marked this review as helpful.
    private final Set<String> helpfulBy = ConcurrentHashMap.newKeySet();

    // Set per viewer, see viewedBy.
    private boolean own;
    private boolean markedHelpful;

    public Review(int reviewId, int gameId, String gameTitle, String username, int rating,
            String title, String body, LocalDate createdAt) {
        this.reviewId = reviewId;
        this.gameId = gameId;
        this.gameTitle = gameTitle;
        this.username = username;
        this.rating = rating;
        this.title = title;
        this.body = body;
        this.createdAt = createdAt;
    }

    /**
     * Returns a copy prepared for the given viewer, so templates can show
     * edit/delete buttons only on the viewer's own reviews.
     */
    public Review viewedBy(String viewer) {
        Review copy = new Review(reviewId, gameId, gameTitle, username, rating, title, body, createdAt);
        copy.helpfulBy.addAll(helpfulBy);
        copy.edited = edited;
        copy.own = username.equals(viewer);
        copy.markedHelpful = helpfulBy.contains(viewer);
        return copy;
    }

    public void update(int rating, String title, String body) {
        this.rating = rating;
        this.title = title;
        this.body = body;
        this.edited = true;
    }

    /** Adds or removes the user's helpful vote. */
    public void toggleHelpful(String user) {
        if (!helpfulBy.remove(user)) {
            helpfulBy.add(user);
        }
    }

    public void addHelpful(String user) {
        helpfulBy.add(user);
    }

    public int getReviewId() {
        return reviewId;
    }

    public int getGameId() {
        return gameId;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public String getUsername() {
        return username;
    }

    public int getRating() {
        return rating;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public String getCreatedAtDisplay() {
        return Format.date(createdAt);
    }

    public boolean isEdited() {
        return edited;
    }

    public int getHelpfulCount() {
        return helpfulBy.size();
    }

    public boolean isOwn() {
        return own;
    }

    public boolean isMarkedHelpful() {
        return markedHelpful;
    }

    public String getStars() {
        return Format.stars(rating);
    }

    public String getUserInitial() {
        return username.substring(0, 1).toUpperCase();
    }

    public String getUserColor() {
        return Format.color(username);
    }
}
