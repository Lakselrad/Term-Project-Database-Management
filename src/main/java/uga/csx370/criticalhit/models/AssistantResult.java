package uga.csx370.criticalhit.models;

import java.util.List;

/**
 * What the assistant understood from a natural language request, and the matching games.
 */
public class AssistantResult {

    private final String prompt;
    private final List<String> understood;
    private final List<Game> games;

    public AssistantResult(String prompt, List<String> understood, List<Game> games) {
        this.prompt = prompt;
        this.understood = understood;
        this.games = games;
    }

    public String getPrompt() {
        return prompt;
    }

    public List<String> getUnderstood() {
        return understood;
    }

    public List<Game> getGames() {
        return games;
    }

    public int getGameCount() {
        return games.size();
    }

    public boolean isEmpty() {
        return games.isEmpty();
    }
}
