package uga.csx370.criticalhit.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uga.csx370.criticalhit.models.LibraryEntry;
import uga.csx370.criticalhit.models.StatusTab;
import uga.csx370.criticalhit.services.LibraryService;
import uga.csx370.criticalhit.services.UserService;
import uga.csx370.criticalhit.utility.Format;

/**
 * Handles /library, the logged in user's personal game library.
 */
@Controller
@RequestMapping("/library")
public class LibraryController {

    private final LibraryService libraryService;
    private final UserService userService;

    @Autowired
    public LibraryController(LibraryService libraryService, UserService userService) {
        this.libraryService = libraryService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView webpage(@RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "info", required = false) String info) {
        ModelAndView mv = new ModelAndView("library_page");
        String username = userService.getLoggedInUser().getUsername();

        Map<String, Integer> counts = libraryService.getStatusCounts(username);
        int total = counts.values().stream().mapToInt(Integer::intValue).sum();
        List<StatusTab> tabs = new ArrayList<>();
        tabs.add(new StatusTab("", "All", total, Format.isBlank(status)));
        for (Map.Entry<String, Integer> c : counts.entrySet()) {
            tabs.add(new StatusTab(c.getKey(), c.getKey(), c.getValue(), c.getKey().equalsIgnoreCase(status)));
        }

        List<LibraryEntry> entries = libraryService.getLibrary(username, status);
        mv.addObject("pageTitle", "My library");
        mv.addObject("activeLibrary", true);
        mv.addObject("tabs", tabs);
        mv.addObject("entries", entries);
        mv.addObject("isNoContent", entries.isEmpty());
        mv.addObject("errorMessage", error);
        mv.addObject("infoMessage", info);
        return mv;
    }

    /** Adds a game / changes its status. An empty status removes it. Used by the game and library pages. */
    @PostMapping("/{gameId}")
    public String update(@PathVariable("gameId") int gameId,
            @RequestParam(name = "status", defaultValue = "") String status,
            @RequestHeader(value = "Referer", required = false) String referer) {
        String back = Format.back(referer, "/library");
        String username = userService.getLoggedInUser().getUsername();

        if (status.isBlank()) {
            libraryService.remove(username, gameId);
            return Format.redirect(back, "info", "Removed from your library.");
        }
        if (!libraryService.setStatus(username, gameId, status)) {
            return Format.redirect(back, "error", "Could not update your library.");
        }
        return Format.redirect(back, "info", "Library updated: " + status);
    }
}
