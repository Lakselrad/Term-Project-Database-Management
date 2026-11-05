package uga.csx370.criticalhit.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uga.csx370.criticalhit.models.Game;
import uga.csx370.criticalhit.models.LibraryEntry;
import uga.csx370.criticalhit.models.Option;
import uga.csx370.criticalhit.models.Review;
import uga.csx370.criticalhit.services.GameService;
import uga.csx370.criticalhit.services.LibraryService;
import uga.csx370.criticalhit.services.UserService;
import uga.csx370.criticalhit.utility.Format;

/**
 * Handles /games (browse + search) and /games/{gameId} (game details and reviews).
 */
@Controller
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;
    private final LibraryService libraryService;
    private final UserService userService;

    @Autowired
    public GameController(GameService gameService, LibraryService libraryService, UserService userService) {
        this.gameService = gameService;
        this.libraryService = libraryService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView browse(@RequestParam(name = "q", required = false) String query,
            @RequestParam(name = "genre", required = false) String genre,
            @RequestParam(name = "platform", required = false) String platform,
            @RequestParam(name = "sort", required = false, defaultValue = "title") String sort,
            @RequestParam(name = "error", required = false) String error) {
        ModelAndView mv = new ModelAndView("games_page");
        List<Game> games = gameService.searchGames(query, genre, platform, sort);

        mv.addObject("pageTitle", "Browse games");
        mv.addObject("activeBrowse", true);
        mv.addObject("query", query);
        mv.addObject("games", games);
        mv.addObject("resultCount", games.size());
        mv.addObject("isNoContent", games.isEmpty());
        mv.addObject("genreOptions", Option.listOf(gameService.getGenres(), genre, "All genres"));
        mv.addObject("platformOptions", Option.listOf(gameService.getPlatforms(), platform, "All platforms"));
        mv.addObject("sortOptions", List.of(
                new Option("title", "Title (A-Z)", sort.equals("title")),
                new Option("rating", "Highest rated", sort.equals("rating")),
                new Option("reviews", "Most reviewed", sort.equals("reviews")),
                new Option("newest", "Newest release", sort.equals("newest"))));
        mv.addObject("errorMessage", error);
        return mv;
    }

    @GetMapping("/{gameId}")
    public ModelAndView gamePage(@PathVariable("gameId") int gameId,
            @RequestParam(name = "sort", required = false, defaultValue = "helpful") String sort,
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "info", required = false) String info) {
        Optional<Game> game = gameService.findGame(gameId);
        if (game.isEmpty()) {
            return new ModelAndView(Format.redirect("/games", "error", "That game could not be found."));
        }

        String viewer = userService.getLoggedInUser().getUsername();
        Review myReview = gameService.findUserReview(gameId, viewer).orElse(null);
        String libraryStatus = libraryService.getStatus(viewer, gameId).orElse("");
        List<Review> reviews = gameService.getReviewsForGame(gameId, viewer, sort);

        ModelAndView mv = new ModelAndView("game_page");
        mv.addObject("pageTitle", game.get().getTitle());
        mv.addObject("activeBrowse", true);
        mv.addObject("game", game.get());
        mv.addObject("ratingBars", gameService.getRatingDistribution(gameId));
        mv.addObject("reviews", reviews);
        mv.addObject("isNoReviews", reviews.isEmpty());
        mv.addObject("myReview", myReview);
        mv.addObject("ratingOptions", Option.ratingOptions(myReview == null ? 0 : myReview.getRating()));
        mv.addObject("inLibrary", !libraryStatus.isEmpty());
        mv.addObject("libraryOptions", Option.listOf(LibraryEntry.STATUSES, libraryStatus, "Not in my library"));
        mv.addObject("reviewSortOptions", List.of(
                new Option("helpful", "Most helpful", sort.equals("helpful")),
                new Option("newest", "Newest", sort.equals("newest")),
                new Option("highest", "Highest", sort.equals("highest")),
                new Option("lowest", "Lowest", sort.equals("lowest"))));
        mv.addObject("errorMessage", error);
        mv.addObject("infoMessage", info);
        return mv;
    }

    /** Creates or updates the logged in user's review of this game. */
    @PostMapping("/{gameId}/review")
    public String saveReview(@PathVariable("gameId") int gameId,
            @RequestParam(name = "rating", required = false) Integer rating,
            @RequestParam(name = "title", defaultValue = "") String title,
            @RequestParam(name = "body", defaultValue = "") String body) {
        String path = "/games/" + gameId;
        if (gameService.findGame(gameId).isEmpty()) {
            return Format.redirect("/games", "error", "That game could not be found.");
        }
        if (rating == null || rating < 1 || rating > 5) {
            return Format.redirect(path, "error", "Please pick a star rating from 1 to 5.");
        }
        if (title.isBlank() || body.isBlank()) {
            return Format.redirect(path, "error", "Your review needs a headline and some text.");
        }
        if (title.length() > 100 || body.length() > 2000) {
            return Format.redirect(path, "error", "Headline max 100 characters, review max 2000 characters.");
        }

        String username = userService.getLoggedInUser().getUsername();
        boolean updated = gameService.saveReview(gameId, username, rating, title.trim(), body.trim());
        return Format.redirect(path, "info", updated ? "Your review was updated." : "Thanks! Your review was posted.");
    }
}
