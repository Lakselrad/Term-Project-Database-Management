package uga.csx370.criticalhit.services;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uga.csx370.criticalhit.models.LibraryEntry;
import uga.csx370.criticalhit.utility.Format;

/**
 * A user's personal game library (Playing / Completed / Wishlist / Dropped).
 *
 * TEMPORARY: reads from MockDataStore. Replace with JDBC when the database is ready.
 */
@Service
public class LibraryService {

    private final MockDataStore store;
    private final GameService gameService;

    @Autowired
    public LibraryService(MockDataStore store, GameService gameService) {
        this.store = store;
        this.gameService = gameService;
    }

    /** All library entries for a user, optionally only one status. Newest first. */
    public List<LibraryEntry> getLibrary(String username, String status) {
        return library(username).values().stream()
                .filter(e -> Format.isBlank(status) || e.getStatus().equalsIgnoreCase(status))
                .sorted(Comparator.comparing(LibraryEntry::getAddedOn).reversed())
                .map(e -> e.withGame(gameService.findGame(e.getGameId()).orElse(null)))
                .filter(e -> e.getGame() != null)
                .toList();
    }

    /** Number of games in each status, in the order of LibraryEntry.STATUSES. */
    public Map<String, Integer> getStatusCounts(String username) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String status : LibraryEntry.STATUSES) {
            counts.put(status, 0);
        }
        for (LibraryEntry entry : library(username).values()) {
            counts.merge(entry.getStatus(), 1, Integer::sum);
        }
        return counts;
    }

    public Optional<String> getStatus(String username, int gameId) {
        return Optional.ofNullable(library(username).get(gameId)).map(LibraryEntry::getStatus);
    }

    /** Adds the game or changes its status. Returns false for an unknown status/game. */
    public boolean setStatus(String username, int gameId, String status) {
        if (!LibraryEntry.STATUSES.contains(status) || gameService.findGame(gameId).isEmpty()) {
            return false;
        }
        library(username).put(gameId, new LibraryEntry(gameId, status, LocalDate.now()));
        return true;
    }

    public void remove(String username, int gameId) {
        library(username).remove(gameId);
    }

    private Map<Integer, LibraryEntry> library(String username) {
        return store.libraries.computeIfAbsent(username.toLowerCase(), k -> new ConcurrentHashMap<>());
    }
}
