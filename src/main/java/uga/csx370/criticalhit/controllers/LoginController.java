package uga.csx370.criticalhit.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uga.csx370.criticalhit.services.MockDataStore;
import uga.csx370.criticalhit.services.UserService;
import uga.csx370.criticalhit.utility.Format;

/**
 * Handles /login and /logout.
 */
@Controller
public class LoginController {

    private final UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public ModelAndView webpage(@RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "info", required = false) String info) {
        ModelAndView mv = new ModelAndView("login_page");
        // Visiting the login page logs the user out.
        userService.unAuthenticate();
        mv.addObject("pageTitle", "Log in");
        mv.addObject("demoPassword", MockDataStore.DEMO_PASSWORD);
        mv.addObject("errorMessage", error);
        mv.addObject("infoMessage", info);
        return mv;
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
            @RequestParam("password") String password) {
        if (userService.authenticate(username, password)) {
            return "redirect:/";
        }
        return Format.redirect("/login", "error", "Invalid username or password. Please try again.");
    }

    @GetMapping("/logout")
    public String logout() {
        userService.unAuthenticate();
        return Format.redirect("/login", "info", "You have been logged out.");
    }
}
