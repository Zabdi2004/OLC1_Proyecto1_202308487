package battlescript.model;

public class Match {
    private final String name;
    private final String playerOne;
    private final String playerTwo;
    private final int rounds;
    private final Scoring scoring;
    private final Bonuses bonuses;

    // Constructor que usa el Parser
    public Match(String name, String playerOne, String playerTwo, int rounds, Scoring scoring, Bonuses bonuses) {
        this.name = name;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.rounds = rounds;
        this.scoring = scoring;
        this.bonuses = bonuses;
    }

    // Constructor por defecto (si lo necesitas)
    public Match() {
        this("", "", "", 0, new Scoring(), new Bonuses());
    }

    public String getName() { return name; }
    public String getPlayerOne() { return playerOne; }
    public String getPlayerTwo() { return playerTwo; }
    public int getRounds() { return rounds; }
    public Scoring getScoring() { return scoring; }
    public Bonuses getBonuses() { return bonuses; }
}