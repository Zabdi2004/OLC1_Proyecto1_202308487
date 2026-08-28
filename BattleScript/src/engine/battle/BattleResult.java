package engine.battle;

public class BattleResult {
    private final String matchName;
    private final String winner;
    private final int rounds;
    private final String log;

    public BattleResult(String matchName, String winner, int rounds, String log) {
        this.matchName = matchName;
        this.winner = winner;
        this.rounds = rounds;
        this.log = log;
    }

    public String getMatchName() { return matchName; }
    public String getWinner() { return winner; }
    public int getRounds() { return rounds; }
    public String getLog() { return log; }
}