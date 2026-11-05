package uga.csx370.criticalhit.models;

/**
 * One row of a game's rating breakdown, e.g. "5 stars: 12 reviews (40%)".
 */
public class RatingBar {

    private final int stars;
    private final int count;
    private final int percent;

    public RatingBar(int stars, int count, int percent) {
        this.stars = stars;
        this.count = count;
        this.percent = percent;
    }

    public int getStars() {
        return stars;
    }

    public int getCount() {
        return count;
    }

    public int getPercent() {
        return percent;
    }
}
