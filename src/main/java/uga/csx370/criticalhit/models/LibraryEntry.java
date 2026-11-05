package uga.csx370.criticalhit.models;

import java.time.LocalDate;
import java.util.List;

import uga.csx370.criticalhit.utility.Format;

/**
 * A game in a user's personal library with a play status.
 */
public class LibraryEntry {

    public static final List<String> STATUSES = List.of("Playing", "Completed", "Wishlist", "Dropped");

    private final int gameId;
    private final String status;
    private final LocalDate addedOn;
    // Filled in when the entry is shown on a page.
    private final Game game;

    public LibraryEntry(int gameId, String status, LocalDate addedOn) {
        this(gameId, status, addedOn, null);
    }

    public LibraryEntry(int gameId, String status, LocalDate addedOn, Game game) {
        this.gameId = gameId;
        this.status = status;
        this.addedOn = addedOn;
        this.game = game;
    }

    public LibraryEntry withGame(Game game) {
        return new LibraryEntry(gameId, status, addedOn, game);
    }

    public int getGameId() {
        return gameId;
    }

    public String getStatus() {
        return status;
    }

    /** e.g. "status-playing", used for badge colors. */
    public String getStatusClass() {
        return "status-" + status.toLowerCase();
    }

    public LocalDate getAddedOn() {
        return addedOn;
    }

    public String getAddedOnDisplay() {
        return Format.date(addedOn);
    }

    public Game getGame() {
        return game;
    }

    /** Options for the status dropdown, with the current status selected. */
    public List<Option> getStatusOptions() {
        return STATUSES.stream().map(s -> new Option(s, s, s.equals(status))).toList();
    }
}
