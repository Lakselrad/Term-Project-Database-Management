package uga.csx370.criticalhit.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uga.csx370.criticalhit.services.UserService;
import uga.csx370.criticalhit.utility.Format;

/**
 * Handles /register.
 */
@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView webpage(@RequestParam(name = "error", required = false) String error) {
        ModelAndView mv = new ModelAndView("registration_page");
        mv.addObject("pageTitle", "Create account");
        mv.addObject("errorMessage", error);
        return mv;
    }

    @PostMapping
    public String register(@RequestParam("username") String username,
            @RequestParam("displayName") String displayName,
            @RequestParam("password") String password,
            @RequestParam("passwordRepeat") String passwordRepeat) {
        if (!username.trim().matches("[A-Za-z0-9_]{3,20}")) {
            return Format.redirect("/register", "error",
                    "Username must be 3-20 characters: letters, numbers or underscores.");
        }
        if (displayName.isBlank() || displayName.length() > 40) {
            return Format.redirect("/register", "error", "Please enter a display name (up to 40 characters).");
        }
        if (password.length() < 8) {
            return Format.redirect("/register", "error", "Password must be at least 8 characters.");
        }
        if (!password.equals(passwordRepeat)) {
            return Format.redirect("/register", "error", "Passwords do not match.");
        }
        if (!userService.registerUser(username, displayName, password)) {
            return Format.redirect("/register", "error", "That username is already taken.");
        }
        return Format.redirect("/login", "info", "Account created! You can log in now.");
    }
}
