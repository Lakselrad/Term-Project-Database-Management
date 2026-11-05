package uga.csx370.criticalhit.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import uga.csx370.criticalhit.services.GameService;
import uga.csx370.criticalhit.services.UserService;
import uga.csx370.criticalhit.utility.Format;

/**
 * Review actions that can happen from several pages (delete, helpful vote).
 * After the action the user is sent back to the page they came from.
 */
@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final GameService gameService;
    private final UserService userService;

    @Autowired
    public ReviewController(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @PostMapping("/{reviewId}/delete")
    public String delete(@PathVariable("reviewId") int reviewId,
            @RequestHeader(value = "Referer", required = false) String referer) {
        String back = Format.back(referer, "/");
        String username = userService.getLoggedInUser().getUsername();
        if (gameService.deleteReview(reviewId, username)) {
            return Format.redirect(back, "info", "Your review was deleted.");
        }
        return Format.redirect(back, "error", "You can only delete your own reviews.");
    }

    @PostMapping("/{reviewId}/helpful")
    public String helpful(@PathVariable("reviewId") int reviewId,
            @RequestHeader(value = "Referer", required = false) String referer) {
        gameService.toggleHelpful(reviewId, userService.getLoggedInUser().getUsername());
        return "redirect:" + Format.back(referer, "/");
    }
}
