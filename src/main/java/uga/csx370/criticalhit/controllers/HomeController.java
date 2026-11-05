package uga.csx370.criticalhit.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uga.csx370.criticalhit.services.GameService;
import uga.csx370.criticalhit.services.UserService;

/**
 * Handles the home page at /.
 */
@Controller
public class HomeController {

    private final UserService userService;
    private final GameService gameService;

    @Autowired
    public HomeController(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @GetMapping("/")
    public ModelAndView webpage(@RequestParam(name = "error", required = false) String error) {
        ModelAndView mv = new ModelAndView("home_page");
        String viewer = userService.getLoggedInUser().getUsername();

        mv.addObject("pageTitle", "Home");
        mv.addObject("activeHome", true);
        mv.addObject("topRated", gameService.getTopRated(null, 2, 6));
        mv.addObject("mostReviewed", gameService.getMostReviewed(4));
        mv.addObject("recentReviews", gameService.getRecentReviews(6, viewer));
        mv.addObject("errorMessage", error);
        return mv;
    }
}
