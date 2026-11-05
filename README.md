# Critical Hit – Video Game Review Site

Spring Boot + Mustache front-end template for the CSCI 4370/6370 term project.

> **Status:** front-end only. All data currently comes from an in-memory fake data
> store (`MockDataStore.java`) that resets every time the app restarts. No MySQL yet.

## Run it

```bash
mvn spring-boot:run
```

Then open http://localhost:8080. Demo logins: `alex`, `jordan`, `sam` (and `taylor`, `riley`,
`casey`, `morgan`, `jamie`). The password for all of them is `password123`.

## Pages

| URL | Page | What it shows |
| --- | --- | --- |
| `/login`, `/register`, `/logout` | Auth | Sign in / sign up (BCrypt-hashed passwords) |
| `/` | Home | Top rated games, latest reviews, most reviewed |
| `/games` | Browse | Search + filter by genre/platform + sort |
| `/games/{id}` | Game details | Info, average score, rating breakdown, reviews, write/edit review, add to library |
| `/top` | Top rated | Leaderboard by average rating, filter by genre |
| `/library` | My library | Your games by status (Playing / Completed / Wishlist / Dropped) |
| `/users/{username}` | Profile | User stats, library preview, all their reviews |
| `/assistant` | Game assistant | Natural language search (placeholder for an AI integration) |

User actions (these will map to INSERT / UPDATE / DELETE queries later):

- Post review / update review: `POST /games/{id}/review`
- Delete review: `POST /reviews/{id}/delete`
- Mark review helpful (toggle): `POST /reviews/{id}/helpful`
- Add / change / remove library status: `POST /library/{gameId}`
- Register: `POST /register`

## Project layout

```
src/main/java/uga/csx370/criticalhit/
├── App.java                     entry point
├── config/                      WebConfig (login interceptor), MustacheConfig
├── components/                  AuthInterceptor, GlobalModelAttributes (navbar user)
├── controllers/                 one controller per page
├── models/                      Game, Review, User, LibraryEntry + small view helpers
├── services/                    GameService, LibraryService, UserService, AssistantService
│   └── MockDataStore.java       <-- TEMPORARY fake data, delete when the DB is connected
└── utility/Format.java          stars, colors, redirects with messages
src/main/resources/
├── templates/                   *_page.mustache + fragments/
└── static/                      css/styles.css, js/main.js
```

## Connecting the database later

1. Uncomment the MySQL + JDBC dependencies in `pom.xml` and the `spring.datasource.*`
   lines in `application.properties`.
2. Inject `DataSource` into `GameService`, `LibraryService` and `UserService` (like Project 2)
   and replace each method body with a prepared statement. The method names and return
   types are already what the controllers and templates expect, so those should not need changes.
3. Delete `MockDataStore.java`.

## Notes for the team

- Mustache can't compare values, so things like the "selected" dropdown option are
  computed in Java (see `models/Option.java`).
- Missing or null template variables render as empty text (configured in `MustacheConfig`).
- Cover art is a generated gradient + initials. To use real images, add an image URL
  to `Game` and swap the `.cover` markup in `fragments/game_card.mustache`.
