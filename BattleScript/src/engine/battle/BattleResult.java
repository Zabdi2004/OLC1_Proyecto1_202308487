package engine.battle;

public class BattleResult {

    private final String matchName;
    private final String winner;
    private final int rounds;
    private final String log;

    // Datos finales del jugador 1
    private final String player1Name;
    private final String player1Class;
    private final int player1Health;
    private final int player1Resource;
    private final int player1Score;

    // Datos finales del jugador 2
    private final String player2Name;
    private final String player2Class;
    private final int player2Health;
    private final int player2Resource;
    private final int player2Score;

    public BattleResult(
            String matchName,
            String winner,
            int rounds,
            String log,
            String player1Name,
            String player1Class,
            int player1Health,
            int player1Resource,
            int player1Score,
            String player2Name,
            String player2Class,
            int player2Health,
            int player2Resource,
            int player2Score
    ) {

        this.matchName = matchName;
        this.winner = winner;
        this.rounds = rounds;
        this.log = log;

        this.player1Name = player1Name;
        this.player1Class = player1Class;
        this.player1Health = player1Health;
        this.player1Resource = player1Resource;
        this.player1Score = player1Score;

        this.player2Name = player2Name;
        this.player2Class = player2Class;
        this.player2Health = player2Health;
        this.player2Resource = player2Resource;
        this.player2Score = player2Score;
    }

    public String getMatchName() {
        return matchName;
    }

    public String getWinner() {
        return winner;
    }

    public int getRounds() {
        return rounds;
    }

    public String getLog() {
        return log;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer1Class() {
        return player1Class;
    }

    public int getPlayer1Health() {
        return player1Health;
    }

    public int getPlayer1Resource() {
        return player1Resource;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public String getPlayer2Class() {
        return player2Class;
    }

    public int getPlayer2Health() {
        return player2Health;
    }

    public int getPlayer2Resource() {
        return player2Resource;
    }

    public int getPlayer2Score() {
        return player2Score;
    }
}