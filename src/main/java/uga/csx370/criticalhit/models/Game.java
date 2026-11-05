package uga.csx370.criticalhit.models;

import java.util.Arrays;
import java.util.List;

import uga.csx370.criticalhit.utility.Format;

/**
 * A video game in the catalog, together with its review statistics.
 */
public class Game {

    private final int gameId;
    private final String title;
    private final String developer;
    private final String publisher;
    private final int releaseYear;
    private final String genre;
    // Comma separated, e.g. "PC, PS5, Switch".
    private final String platforms;
    private final String description;

    // Calculated from reviews.
    private final double avgRating;
    private final int reviewCount;

    public Game(int gameId, String title, String developer, String publisher, int releaseYear,
            String genre, String platforms, String description) {
        this(gameId, title, developer, publisher, releaseYear, genre, platforms, description, 0, 0);
    }

    public Game(int gameId, String title, String developer, String publisher, int releaseYear,
            String genre, String platforms, String description, double avgRating, int reviewCount) {
        this.gameId = gameId;
        this.title = title;
        this.developer = developer;
        this.publisher = publisher;
        this.releaseYear = releaseYear;
        this.genre = genre;
        this.platforms = platforms;
        this.description = description;
        this.avgRating = avgRating;
        this.reviewCount = reviewCount;
    }

    /** Returns a copy of this game with the given review statistics. */
    public Game withStats(double avgRating, int reviewCount) {
        return new Game(gameId, title, developer, publisher, releaseYear, genre, platforms,
                description, avgRating, reviewCount);
    }

    public int getGameId() {
        return gameId;
    }

    public String getTitle() {
        return title;
    }

    public String getDeveloper() {
        return developer;
    }

    public String getPublisher() {
        return publisher;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public String getGenre() {
        return genre;
    }

    public String getPlatforms() {
        return platforms;
    }

    public List<String> getPlatformList() {
        return Arrays.stream(platforms.split(",")).map(String::trim).toList();
    }

    public boolean hasPlatform(String platform) {
        return getPlatformList().stream().anyMatch(p -> p.equalsIgnoreCase(platform));
    }

    public String getDescription() {
        return description;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public boolean isReviewed() {
        return reviewCount > 0;
    }

    /** "4.3", or a dash when the game has no reviews yet. */
    public String getAvgRatingDisplay() {
        return reviewCount == 0 ? "–" : String.format("%.1f", avgRating);
    }

    public String getStars() {
        return Format.stars(avgRating);
    }

    /** CSS class used to color the score badge. */
    public String getScoreClass() {
        if (reviewCount == 0) {
            return "score-none";
        }
        if (avgRating >= 4) {
            return "score-high";
        }
        if (avgRating >= 3) {
            return "score-mid";
        }
        return "score-low";
    }

    /** Inline style for the placeholder cover art. Replace with a real image URL later if you like. */
    public String getCoverStyle() {
        return Format.coverGradient(title);
    }

    public String getInitials() {
        return Format.initials(title);
    }
}
