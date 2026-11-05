package uga.csx370.criticalhit.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uga.csx370.criticalhit.models.LibraryEntry;
import uga.csx370.criticalhit.models.Review;
import uga.csx370.criticalhit.models.User;
import uga.csx370.criticalhit.services.GameService;
import uga.csx370.criticalhit.services.LibraryService;
import uga.csx370.criticalhit.services.UserService;
import uga.csx370.criticalhit.utility.Format;

/**
 * Handles /profile (your own) and /users/{username} (anyone's profile).
 */
@Controller
public class ProfileController {

    private final UserService userService;
    private final GameService gameService;
    private final LibraryService libraryService;

    @Autowired
    public ProfileController(UserService userService, GameService gameService, LibraryService libraryService) {
        this.userService = userService;
        this.gameService = gameService;
        this.libraryService = libraryService;
    }

    @GetMapping("/profile")
    public String myProfile() {
        return "redirect:/users/" + userService.getLoggedInUser().getUsername();
    }

    @GetMapping("/users/{username}")
    public ModelAndView webpage(@PathVariable("username") String username,
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "info", required = false) String info) {
        Optional<User> user = userService.findUser(username);
        if (user.isEmpty()) {
            return new ModelAndView(Format.redirect("/", "error", "That user does not exist."));
        }
        User profileUser = user.get();
        String viewer = userService.getLoggedInUser().getUsername();

        List<Review> reviews = gameService.getReviewsByUser(profileUser.getUsername(), viewer);
        List<LibraryEntry> library = libraryService.getLibrary(profileUser.getUsername(), null);
        Map<String, Integer> statusCounts = libraryService.getStatusCounts(profileUser.getUsername());
        double avgGiven = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
        String favoriteGenre = library.stream()
                .collect(Collectors.groupingBy(e -> e.getGame().getGenre(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("–");

        ModelAndView mv = new ModelAndView("profile_page");
        boolean isOwnProfile = profileUser.getUsername().equals(viewer);
        mv.addObject("pageTitle", profileUser.getDisplayName());
        mv.addObject("activeProfile", isOwnProfile);
        mv.addObject("profileUser", profileUser);
        mv.addObject("isOwnProfile", isOwnProfile);
        mv.addObject("reviews", reviews);
        mv.addObject("isNoReviews", reviews.isEmpty());
        mv.addObject("reviewCount", String.valueOf(reviews.size()));
        mv.addObject("avgGiven", reviews.isEmpty() ? "–" : String.format("%.1f", avgGiven));
        mv.addObject("libraryCount", String.valueOf(library.size()));
        mv.addObject("completedCount", String.valueOf(statusCounts.getOrDefault("Completed", 0)));
        mv.addObject("favoriteGenre", favoriteGenre);
        mv.addObject("libraryPreview", library.stream().limit(6).map(LibraryEntry::getGame).toList());
        mv.addObject("errorMessage", error);
        mv.addObject("infoMessage", info);
        return mv;
    }
}
