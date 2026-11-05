package uga.csx370.criticalhit.services;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uga.csx370.criticalhit.models.AssistantResult;
import uga.csx370.criticalhit.models.Game;

/**
 * Natural language game finder ("cozy games on Switch after 2018").
 *
 * PLACEHOLDER: this uses simple keyword matching. The plan is to send the
 * prompt to an AI API (e.g. Gemini free tier), ask it to return the filters
 * (genre, platform, year range, minimum rating) as JSON, and then run the
 * same search as below.
 */
@Service
public class AssistantService {

    private static final Pattern AFTER_YEAR = Pattern.compile("(?:after|since|newer than) (\\d{4})");
    private static final Pattern BEFORE_YEAR = Pattern.compile("(?:before|older than) (\\d{4})");

    private final GameService gameService;

    @Autowired
    public AssistantService(GameService gameService) {
        this.gameService = gameService;
    }

    public AssistantResult ask(String prompt) {
        String p = prompt.toLowerCase();
        List<String> understood = new ArrayList<>();

        String genre = null;
        for (String g : gameService.getGenres()) {
            if (p.contains(g.toLowerCase()) || p.contains(g.toLowerCase().replace("-", " "))) {
                genre = g;
            }
        }
        if (genre == null) {
            genre = genreFromSynonym(p);
        }
        if (genre != null) {
            understood.add("Genre: " + genre);
        }

        String platform = null;
        for (String pl : gameService.getPlatforms()) {
            if (Pattern.compile("\\b" + Pattern.quote(pl.toLowerCase()) + "\\b").matcher(p).find()) {
                platform = pl;
            }
        }
        if (platform != null) {
            understood.add("Platform: " + platform);
        }

        int afterYear = yearFrom(AFTER_YEAR, p);
        int beforeYear = yearFrom(BEFORE_YEAR, p);
        if (afterYear > 0) {
            understood.add("Released after " + afterYear);
        }
        if (beforeYear > 0) {
            understood.add("Released before " + beforeYear);
        }

        boolean onlyGood = p.matches(".*\\b(best|top|great|highly rated|good|must play)\\b.*");
        if (onlyGood) {
            understood.add("Rated 4.0 or higher");
        }

        List<Game> games = gameService.searchGames(null, genre, platform, "rating").stream()
                .filter(g -> afterYear == 0 || g.getReleaseYear() > afterYear)
                .filter(g -> beforeYear == 0 || g.getReleaseYear() < beforeYear)
                .filter(g -> !onlyGood || g.getAvgRating() >= 4.0)
                .toList();

        if (understood.isEmpty()) {
            understood.add("No filters recognized, showing the highest rated games");
        }
        return new AssistantResult(prompt, understood, games);
    }

    private static String genreFromSynonym(String p) {
        if (p.contains("fps") || p.contains("shoot")) {
            return "Shooter";
        }
        if (p.contains("role playing") || p.contains("role-playing")) {
            return "RPG";
        }
        if (p.contains("cozy") || p.contains("relaxing") || p.contains("farming")) {
            return "Simulation";
        }
        if (p.contains("open world") || p.contains("adventure")) {
            return "Action-Adventure";
        }
        if (p.contains("friends") || p.contains("party")) {
            return "Party";
        }
        return null;
    }

    private static int yearFrom(Pattern pattern, String text) {
        Matcher m = pattern.matcher(text);
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }
}
