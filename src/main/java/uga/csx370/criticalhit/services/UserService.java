package uga.csx370.criticalhit.services;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import uga.csx370.criticalhit.models.User;

/**
 * Login, logout and registration. One instance per browser session,
 * so it can remember who is logged in.
 *
 * TEMPORARY: accounts live in MockDataStore. Replace with JDBC when the
 * database is ready (keep the BCrypt hashing).
 */
@Service
@SessionScope
public class UserService {

    private final MockDataStore store;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private User loggedInUser = null;

    @Autowired
    public UserService(MockDataStore store) {
        this.store = store;
    }

    /** Logs the user in if the username and password match. */
    public boolean authenticate(String username, String password) {
        String key = username.trim().toLowerCase();
        String storedHash = store.passwordHashes.get(key);
        if (storedHash != null && passwordEncoder.matches(password, storedHash)) {
            loggedInUser = store.users.get(key);
            return true;
        }
        return false;
    }

    /** Creates a new account. Returns false if the username is already taken. */
    public synchronized boolean registerUser(String username, String displayName, String password) {
        String key = username.trim().toLowerCase();
        if (store.users.containsKey(key)) {
            return false;
        }
        User user = new User(key, displayName.trim(), "", LocalDate.now());
        store.addUser(user, passwordEncoder.encode(password));
        return true;
    }

    public Optional<User> findUser(String username) {
        return Optional.ofNullable(store.users.get(username.toLowerCase()));
    }

    public void unAuthenticate() {
        loggedInUser = null;
    }

    public boolean isAuthenticated() {
        return loggedInUser != null;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }
}
