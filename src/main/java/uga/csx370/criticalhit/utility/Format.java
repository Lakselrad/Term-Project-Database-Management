package uga.csx370.criticalhit.utility;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Small display and redirect helpers shared by models and controllers.
 */
public final class Format {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MMM d, yyyy");

    private Format() {
    }

    /** Turns a 0-5 rating into a star string like "★★★★☆". */
    public static String stars(double rating) {
        int full = (int) Math.round(Math.max(0, Math.min(5, rating)));
        return "★".repeat(full) + "☆".repeat(5 - full);
    }

    /** A stable color generated from any text (used for avatars). */
    public static String color(String seed) {
        int hue = Math.floorMod(seed.hashCode(), 360);
        return "hsl(" + hue + ", 60%, 45%)";
    }

    /** A stable gradient generated from any text (used as placeholder cover art). */
    public static String coverGradient(String seed) {
        int hue = Math.floorMod(seed.hashCode(), 360);
        int hue2 = (hue + 50) % 360;
        return "background: linear-gradient(135deg, hsl(" + hue + ", 70%, 45%), hsl(" + hue2 + ", 70%, 18%));";
    }

    /** "Elden Ring" becomes "ER". */
    public static String initials(String text) {
        String letters = Arrays.stream(text.split("[^A-Za-z0-9]+"))
                .filter(word -> !word.isEmpty())
                .limit(2)
                .map(word -> word.substring(0, 1).toUpperCase())
                .collect(Collectors.joining());
        return letters.isEmpty() ? "?" : letters;
    }

    public static String date(LocalDate date) {
        return date == null ? "" : date.format(DATE);
    }

    /** Builds "redirect:/path?key=message" with the message URL encoded. */
    public static String redirect(String path, String key, String message) {
        String separator = path.contains("?") ? "&" : "?";
        return "redirect:" + path + separator + key + "="
                + URLEncoder.encode(message, StandardCharsets.UTF_8);
    }

    /**
     * Returns the local path of the page the user came from (Referer header),
     * without old error/info messages. Falls back if there is no usable referer.
     */
    public static String back(String referer, String fallback) {
        if (referer == null || referer.isBlank()) {
            return fallback;
        }
        try {
            URI uri = URI.create(referer);
            String path = uri.getRawPath();
            if (path == null || !path.startsWith("/")) {
                return fallback;
            }
            String query = uri.getRawQuery();
            if (query == null) {
                return path;
            }
            String kept = Arrays.stream(query.split("&"))
                    .filter(part -> !part.startsWith("error=") && !part.startsWith("info="))
                    .collect(Collectors.joining("&"));
            return kept.isEmpty() ? path : path + "?" + kept;
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
