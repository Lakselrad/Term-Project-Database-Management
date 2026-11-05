package uga.csx370.criticalhit.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import uga.csx370.criticalhit.models.Game;
import uga.csx370.criticalhit.models.LibraryEntry;
import uga.csx370.criticalhit.models.Review;
import uga.csx370.criticalhit.models.User;

/**
 * TEMPORARY in-memory data so the front-end works before the database exists.
 * Everything here resets when the app restarts.
 *
 * When the MySQL database is ready, replace the code inside the services
 * (GameService, LibraryService, UserService) with JDBC prepared statements
 * and delete this class.
 */
@Component
public class MockDataStore {

    // Demo accounts all use this password.
    public static final String DEMO_PASSWORD = "password123";

    final Map<Integer, Game> games = new ConcurrentHashMap<>();
    final Map<Integer, Review> reviews = new ConcurrentHashMap<>();
    // Keyed by lowercase username.
    final Map<String, User> users = new ConcurrentHashMap<>();
    final Map<String, String> passwordHashes = new ConcurrentHashMap<>();
    // username -> (gameId -> entry)
    final Map<String, Map<Integer, LibraryEntry>> libraries = new ConcurrentHashMap<>();

    private final AtomicInteger nextReviewId = new AtomicInteger(1);
    private final Random random = new Random(370);

    public MockDataStore() {
        seedGames();
        seedUsers();
        seedReviews();
        seedLibraries();
    }

    int nextReviewId() {
        return nextReviewId.getAndIncrement();
    }

    void addUser(User user, String passwordHash) {
        String key = user.getUsername().toLowerCase();
        users.put(key, user);
        passwordHashes.put(key, passwordHash);
        libraries.put(key, new ConcurrentHashMap<>());
    }

    private void addGame(int id, String title, String developer, String publisher, int year,
            String genre, String platforms, String description) {
        games.put(id, new Game(id, title, developer, publisher, year, genre, platforms, description));
    }

    private void seedGames() {
        addGame(1, "The Legend of Zelda: Breath of the Wild", "Nintendo EPD", "Nintendo", 2017,
                "Action-Adventure", "Switch, Wii U",
                "Explore a vast open Hyrule at your own pace, climbing, gliding and experimenting your way to Ganon.");
        addGame(2, "Elden Ring", "FromSoftware", "Bandai Namco", 2022, "RPG", "PC, PS5, Xbox Series X",
                "A punishing open-world action RPG set in the Lands Between, full of hidden dungeons and enormous bosses.");
        addGame(3, "Hades", "Supergiant Games", "Supergiant Games", 2020, "Roguelike", "PC, Switch, PS5",
                "Fight your way out of the underworld again and again, growing stronger and unraveling a family drama.");
        addGame(4, "Stardew Valley", "ConcernedApe", "ConcernedApe", 2016, "Simulation",
                "PC, Switch, PS4, Mobile",
                "Inherit an old farm, grow crops, befriend the townsfolk and slowly bring a valley back to life.");
        addGame(5, "Celeste", "Maddy Makes Games", "Maddy Makes Games", 2018, "Platformer", "PC, Switch, PS4",
                "A tight, challenging platformer about climbing a mountain and coming to terms with anxiety.");
        addGame(6, "Hollow Knight", "Team Cherry", "Team Cherry", 2017, "Metroidvania", "PC, Switch, PS4",
                "Descend into the ruined bug kingdom of Hallownest in a beautiful, demanding hand-drawn adventure.");
        addGame(7, "Minecraft", "Mojang Studios", "Microsoft", 2011, "Sandbox",
                "PC, Switch, PS4, Xbox One, Mobile",
                "Mine, craft and build anything you can imagine in an endless block world, alone or with friends.");
        addGame(8, "The Witcher 3: Wild Hunt", "CD Projekt Red", "CD Projekt", 2015, "RPG",
                "PC, PS4, Xbox One, Switch",
                "Monster hunter Geralt searches for his adopted daughter across a war-torn, story-rich open world.");
        addGame(9, "Red Dead Redemption 2", "Rockstar Games", "Rockstar Games", 2018, "Action-Adventure",
                "PC, PS4, Xbox One",
                "An outlaw gang struggles to survive the end of the Wild West in a slow-burning, detailed epic.");
        addGame(10, "Baldur's Gate 3", "Larian Studios", "Larian Studios", 2023, "RPG", "PC, PS5",
                "A sprawling party-based RPG built on tabletop rules, where nearly every choice changes the story.");
        addGame(11, "Portal 2", "Valve", "Valve", 2011, "Puzzle", "PC, PS3",
                "Solve physics puzzles with a portal gun, guided by a passive-aggressive AI, solo or in co-op.");
        addGame(12, "Disco Elysium", "ZA/UM", "ZA/UM", 2019, "RPG", "PC, PS5, Switch",
                "An amnesiac detective solves a murder through dialogue, skill checks and a very talkative inner mind.");
        addGame(13, "Super Mario Odyssey", "Nintendo EPD", "Nintendo", 2017, "Platformer", "Switch",
                "Mario and his sentient hat Cappy hop between colorful kingdoms collecting moons.");
        addGame(14, "God of War", "Santa Monica Studio", "Sony", 2018, "Action-Adventure", "PS4, PC",
                "Kratos and his son Atreus journey through Norse realms in a story about grief and fatherhood.");
        addGame(15, "Cyberpunk 2077", "CD Projekt Red", "CD Projekt", 2020, "RPG", "PC, PS5, Xbox Series X",
                "A mercenary in Night City races to survive a deadly implant in a neon-soaked open-world RPG.");
        addGame(16, "Among Us", "Innersloth", "Innersloth", 2018, "Party", "PC, Switch, Mobile",
                "Crewmates complete tasks while hidden impostors sabotage the ship in this social deduction game.");
        addGame(17, "Overwatch 2", "Blizzard Entertainment", "Blizzard Entertainment", 2022, "Shooter",
                "PC, PS5, Xbox Series X, Switch",
                "Team-based hero shooter where squads of five fight over objectives with unique abilities.");
        addGame(18, "Animal Crossing: New Horizons", "Nintendo EPD", "Nintendo", 2020, "Simulation", "Switch",
                "Move to a deserted island, decorate, fish, catch bugs and build a cozy community.");
        addGame(19, "Doom Eternal", "id Software", "Bethesda", 2020, "Shooter", "PC, PS4, Xbox One",
                "A relentless, fast-paced first-person shooter about ripping and tearing through demon hordes.");
        addGame(20, "Tetris Effect", "Monstars", "Enhance", 2018, "Puzzle", "PC, PS4, Switch",
                "Classic falling-block puzzles wrapped in music and visuals that react to every move you make.");
    }

    private void seedUsers() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(DEMO_PASSWORD);
        String[][] demoUsers = {
                { "alex", "Alex Rivera", "RPG addict. Will 100% anything with a map." },
                { "jordan", "Jordan Lee", "Speedrunner in training." },
                { "sam", "Sam Patel", "Cozy games and puzzle games only, please." },
                { "taylor", "Taylor Kim", "Competitive shooters, late nights." },
                { "riley", "Riley Chen", "Indie game enthusiast." },
                { "casey", "Casey Morgan", "I play everything at least once." },
                { "morgan", "Morgan Brooks", "Retro collector and Nintendo fan." },
                { "jamie", "Jamie Ortiz", "Story first, gameplay second." },
        };
        for (String[] u : demoUsers) {
            LocalDate joined = LocalDate.now().minusDays(400 + random.nextInt(600));
            addUser(new User(u[0], u[1], u[2], joined), hash);
        }
    }

    private void seedReviews() {
        // Rough "true quality" per game so the fake ratings look believable.
        double[] quality = { 0, 4.7, 4.5, 4.6, 4.5, 4.4, 4.5, 4.2, 4.6, 4.5, 4.7, 4.6, 4.3, 4.4, 4.5,
                3.4, 3.6, 3.0, 4.0, 4.1, 4.2 };
        List<String> usernames = List.copyOf(users.keySet());
        for (Game game : games.values()) {
            for (String username : usernames) {
                if (random.nextDouble() > 0.65) {
                    continue;
                }
                int rating = (int) Math.round(quality[game.getGameId()] + random.nextGaussian() * 0.8);
                rating = Math.max(1, Math.min(5, rating));
                String[] text = REVIEW_TEXT[rating - 1][random.nextInt(REVIEW_TEXT[rating - 1].length)];
                LocalDate date = LocalDate.now().minusDays(random.nextInt(365));
                Review review = new Review(nextReviewId(), game.getGameId(), game.getTitle(), username,
                        rating, text[0], text[1], date);
                for (String other : usernames) {
                    if (!other.equals(username) && random.nextDouble() < 0.25) {
                        review.addHelpful(other);
                    }
                }
                reviews.put(review.getReviewId(), review);
            }
        }
    }

    private void seedLibraries() {
        List<Integer> gameIds = List.copyOf(games.keySet());
        for (String username : users.keySet()) {
            Map<Integer, LibraryEntry> library = libraries.get(username);
            for (int i = 0; i < 6; i++) {
                int gameId = gameIds.get(random.nextInt(gameIds.size()));
                String status = LibraryEntry.STATUSES.get(random.nextInt(LibraryEntry.STATUSES.size()));
                library.put(gameId, new LibraryEntry(gameId, status, LocalDate.now().minusDays(random.nextInt(200))));
            }
        }
    }

    // [rating - 1][option] = { headline, body }
    private static final String[][][] REVIEW_TEXT = {
            {
                    { "Could not get into it", "I tried for several hours but the controls felt clunky and the pacing dragged. Not for me." },
                    { "Disappointing", "The trailers promised a lot more than the game delivers. Lots of bugs on my playthrough too." },
            },
            {
                    { "Some good ideas, rough execution", "There is a fun core here, but it is buried under repetitive missions and padding." },
                    { "Fine for a weekend", "Played it with friends and had a few laughs, but I would not go back to it." },
            },
            {
                    { "Solid but not special", "It does what it sets out to do. Nothing groundbreaking, but I enjoyed my time with it." },
                    { "Worth it on sale", "Good gameplay loop with a few frustrating sections. Wait for a discount." },
                    { "Mixed feelings", "Great art direction and music, but the late game drags on longer than it should." },
            },
            {
                    { "Really enjoyed it", "Tight gameplay, memorable moments and a great sense of progression. Easy recommendation." },
                    { "Hooked from the start", "I lost entire weekends to this. A couple of small issues, but overall excellent." },
                    { "Great with friends", "Fun solo, even better in co-op. The developers clearly care about the details." },
            },
            {
                    { "An all-time favorite", "Everything clicks: the world, the mechanics, the soundtrack. I think about it constantly." },
                    { "Masterpiece", "One of those rare games that is worth every minute. Play it if you have not already." },
                    { "Instant classic", "Polished, surprising and endlessly replayable. This sets the bar for the genre." },
            },
    };
}
