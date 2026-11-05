package uga.csx370.criticalhit.models;

/**
 * A game with its position on the Top Rated page.
 */
public class RankedGame {

    private final int rank;
    private final Game game;

    public RankedGame(int rank, Game game) {
        this.rank = rank;
        this.game = game;
    }

    public int getRank() {
        return rank;
    }

    public Game getGame() {
        return game;
    }

    public boolean isPodium() {
        return rank <= 3;
    }
}
