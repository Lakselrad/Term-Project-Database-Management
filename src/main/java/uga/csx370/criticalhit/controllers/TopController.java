package uga.csx370.criticalhit.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uga.csx370.criticalhit.models.Game;
import uga.csx370.criticalhit.models.Option;
import uga.csx370.criticalhit.models.RankedGame;
import uga.csx370.criticalhit.services.GameService;

/**
 * Handles /top, the leaderboard of highest rated games (optionally per genre).
 */
@Controller
@RequestMapping("/top")
public class TopController {

    // Games need this many reviews before they can be ranked.
    private static final int MIN_REVIEWS = 2;

    private final GameService gameService;

    @Autowired
    public TopController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public ModelAndView webpage(@RequestParam(name = "genre", required = false) String genre) {
        ModelAndView mv = new ModelAndView("top_page");

        List<Game> games = gameService.getTopRated(genre, MIN_REVIEWS, 25);
        List<RankedGame> rankings = new ArrayList<>();
        for (int i = 0; i < games.size(); i++) {
            rankings.add(new RankedGame(i + 1, games.get(i)));
        }

        mv.addObject("pageTitle", "Top rated");
        mv.addObject("activeTop", true);
        mv.addObject("genre", genre);
        mv.addObject("minReviews", MIN_REVIEWS);
        mv.addObject("rankings", rankings);
        mv.addObject("isNoContent", rankings.isEmpty());
        mv.addObject("genreOptions", Option.listOf(gameService.getGenres(), genre, "All genres"));
        return mv;
    }
}
