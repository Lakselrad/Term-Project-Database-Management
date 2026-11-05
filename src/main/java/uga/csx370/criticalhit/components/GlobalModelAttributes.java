package uga.csx370.criticalhit.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import uga.csx370.criticalhit.models.User;
import uga.csx370.criticalhit.services.UserService;

/**
 * Adds values every page needs (like the logged in user for the navbar)
 * so each controller does not have to add them itself.
 */
@ControllerAdvice
public class GlobalModelAttributes {

    private final UserService userService;

    @Autowired
    public GlobalModelAttributes(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("currentUser")
    public User currentUser() {
        return userService.getLoggedInUser();
    }
}
