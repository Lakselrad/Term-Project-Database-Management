package uga.csx370.criticalhit.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uga.csx370.criticalhit.services.UserService;

/**
 * Redirects to the login page when the user is not logged in.
 * See WebConfig.java for which URLs this applies to.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final UserService userService;

    @Autowired
    public AuthInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        if (!userService.isAuthenticated()) {
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }
}
