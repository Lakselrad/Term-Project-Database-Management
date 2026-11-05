package uga.csx370.criticalhit.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uga.csx370.criticalhit.models.Game;
import uga.csx370.criticalhit.models.RatingBar;
import uga.csx370.criticalhit.models.Review;
import uga.csx370.criticalhit.utility.Format;

/**
 * Game catalog and review functions.
 *
 * TEMPORARY: reads from MockDataStore. Replace each method body with a JDBC
 * prepared statement when the database is ready; the controllers and
 * templates do not need to change.
 */
@Service
public class GameService {

    private final MockDataStore store;

    @Autowired
    public GameService(MockDataStore store) {
        this.store = store;
    }

    // ---------- Games ----------

    public List<Game> getAllGames() {
        return store.games.values().stream()
                .map(this::withStats)
                .sorted(Comparator.comparing(Game::getTitle))
                .toList();
    }

    public Optional<Game> findGame(int gameId) {
        return Optional.ofNullable(store.games.get(gameId)).map(this::withStats);
    }

    /**
     * Search by title/developer text and optional genre and platform filters.
     * sort: "title" (default), "rating", "reviews", "newest".
     */
    public List<Game> searchGames(String query, String genre, String platform, String sort) {
        Stream<Game> games = getAllGames().stream();
        if (!Format.isBlank(query)) {
            String q = query.trim().toLowerCase();
            games = games.filter(g -> g.getTitle().toLowerCase().contains(q)
                    || g.getDeveloper().toLowerCase().contains(q));
        }
        if (!Format.isBlank(genre)) {
            games = games.filter(g -> g.getGenre().equalsIgnoreCase(genre));
        }
        if (!Format.isBlank(platform)) {
            games = games.filter(g -> g.hasPlatform(platform));
        }
        return games.sorted(sortOrder(sort)).toList();
    }

    public List<String> getGenres() {
        return store.games.values().stream().map(Game::getGenre).distinct().sorted().toList();
    }

    public List<String> getPlatforms() {
        return store.games.values().stream()
                .flatMap(g -> g.getPlatformList().stream())
                .distinct().sorted().toList();
    }

    /** Highest average rating first. Games need at least minReviews reviews to be ranked. */
    public List<Game> getTopRated(String genre, int minReviews, int limit) {
        return searchGames(null, genre, null, "rating").stream()
                .filter(g -> g.getReviewCount() >= minReviews)
                .limit(limit)
                .toList();
    }

    public List<Game> getMostReviewed(int limit) {
        return searchGames(null, null, null, "reviews").stream().limit(limit).toList();
    }

    // ---------- Reviews ----------

    /** sort: "helpful" (default), "newest", "highest", "lowest". */
    public List<Review> getReviewsForGame(int gameId, String viewer, String sort) {
        Comparator<Review> order = switch (sort == null ? "" : sort) {
            case "newest" -> newestFirst();
            case "highest" -> Comparator.comparingInt(Review::getRating).reversed().thenComparing(newestFirst());
            case "lowest" -> Comparator.comparingInt(Review::getRating).thenComparing(newestFirst());
            default -> Comparator.comparingInt(Review::getHelpfulCount).reversed().thenComparing(newestFirst());
        };
        return store.reviews.values().stream()
                .filter(r -> r.getGameId() == gameId)
                .map(r -> r.viewedBy(viewer))
                .sorted(order)
                .toList();
    }

    public List<Review> getReviewsByUser(String username, String viewer) {
        return store.reviews.values().stream()
                .filter(r -> r.getUsername().equalsIgnoreCase(username))
                .map(r -> r.viewedBy(viewer))
                .sorted(newestFirst())
                .toList();
    }

    public List<Review> getRecentReviews(int limit, String viewer) {
        return store.reviews.values().stream()
                .map(r -> r.viewedBy(viewer))
                .sorted(newestFirst())
                .limit(limit)
                .toList();
    }

    public Optional<Review> findUserReview(int gameId, String username) {
        return store.reviews.values().stream()
                .filter(r -> r.getGameId() == gameId && r.getUsername().equals(username))
                .findFirst()
                .map(r -> r.viewedBy(username));
    }

    /** Count of reviews per star value, from 5 stars down to 1. */
    public List<RatingBar> getRatingDistribution(int gameId) {
        int[] counts = new int[6];
        int total = 0;
        for (Review r : store.reviews.values()) {
            if (r.getGameId() == gameId) {
                counts[r.getRating()]++;
                total++;
            }
        }
        List<RatingBar> bars = new ArrayList<>();
        for (int stars = 5; stars >= 1; stars--) {
            int percent = total == 0 ? 0 : Math.round(100f * counts[stars] / total);
            bars.add(new RatingBar(stars, counts[stars], percent));
        }
        return bars;
    }

    /**
     * Creates the user's review for a game, or updates it if they already wrote one.
     * Returns true if an existing review was updated.
     */
    public synchronized boolean saveReview(int gameId, String username, int rating, String title, String body) {
        Game game = store.games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Unknown game " + gameId);
        }
        for (Review r : store.reviews.values()) {
            if (r.getGameId() == gameId && r.getUsername().equals(username)) {
                r.update(rating, title, body);
                return true;
            }
        }
        Review review = new Review(store.nextReviewId(), gameId, game.getTitle(), username,
                rating, title, body, LocalDate.now());
        store.reviews.put(review.getReviewId(), review);
        return false;
    }

    /** Deletes a review if it belongs to the user. Returns true if deleted. */
    public boolean deleteReview(int reviewId, String username) {
        Review review = store.reviews.get(reviewId);
        if (review == null || !review.getUsername().equals(username)) {
            return false;
        }
        store.reviews.remove(reviewId);
        return true;
    }

    public void toggleHelpful(int reviewId, String username) {
        Review review = store.reviews.get(reviewId);
        if (review != null && !review.getUsername().equals(username)) {
            review.toggleHelpful(username);
        }
    }

    // ---------- Helpers ----------

    private Game withStats(Game game) {
        IntSummaryStatistics stats = store.reviews.values().stream()
                .filter(r -> r.getGameId() == game.getGameId())
                .mapToInt(Review::getRating)
                .summaryStatistics();
        double avg = stats.getCount() == 0 ? 0 : stats.getAverage();
        return game.withStats(avg, (int) stats.getCount());
    }

    private static Comparator<Game> sortOrder(String sort) {
        return switch (sort == null ? "" : sort) {
            case "rating" -> Comparator.comparingDouble(Game::getAvgRating).reversed()
                    .thenComparing(Comparator.comparingInt(Game::getReviewCount).reversed());
            case "reviews" -> Comparator.comparingInt(Game::getReviewCount).reversed()
                    .thenComparing(Game::getTitle);
            case "newest" -> Comparator.comparingInt(Game::getReleaseYear).reversed()
                    .thenComparing(Game::getTitle);
            default -> Comparator.comparing(Game::getTitle);
        };
    }

    private static Comparator<Review> newestFirst() {
        return Comparator.comparing(Review::getCreatedAt).reversed()
                .thenComparing(Comparator.comparingInt(Review::getReviewId).reversed());
    }
}
