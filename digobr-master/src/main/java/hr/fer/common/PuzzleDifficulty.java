package hr.fer.common;

public enum PuzzleDifficulty {
    EASY(1, "lako"),
    MEDIUM(2, "normalno"),
    HARD(3, "teško");

    private final int difficulty;
    private final String description;

    PuzzleDifficulty(int difficulty, String description) {
        this.difficulty = difficulty;
        this.description = description;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getDescription() {
        return description;
    }
}
