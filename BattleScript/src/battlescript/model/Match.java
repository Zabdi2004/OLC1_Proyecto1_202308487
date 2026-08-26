package battlescript.model;

public final class Match {
    private final String name, playerOne, playerTwo;
    private final int rounds;
    private final Scoring scoring;
    private final Bonuses bonuses;
    public Match(String name, String playerOne, String playerTwo, int rounds, Scoring scoring, Bonuses bonuses) {
        if (name == null || name.trim().isEmpty() || playerOne == null || playerTwo == null || playerOne.equals(playerTwo) || rounds <= 0)
            throw new IllegalArgumentException("Definición de partida inválida");
        this.name = name; this.playerOne = playerOne; this.playerTwo = playerTwo; this.rounds = rounds; this.scoring = scoring; this.bonuses = bonuses;
    }
    public String getName() { return name; } public String getPlayerOne() { return playerOne; } public String getPlayerTwo() { return playerTwo; }
    public int getRounds() { return rounds; } public Scoring getScoring() { return scoring; } public Bonuses getBonuses() { return bonuses; }
}
