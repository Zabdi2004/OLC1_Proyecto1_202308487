package engine.context;

import battlescript.model.Action;
import java.util.List;

public class Context {
    private int selfHealth;
    private int opponentHealth;
    private int selfResource;
    private int opponentResource;
    private int selfScore;
    private int opponentScore;
    private int roundNumber;
    private int totalRounds;
    private double random;
    private List<Action> selfHistory;
    private List<Action> opponentHistory;

    // Getters y setters
    public int getSelfHealth() { return selfHealth; }
    public void setSelfHealth(int selfHealth) { this.selfHealth = selfHealth; }
    public int getOpponentHealth() { return opponentHealth; }
    public void setOpponentHealth(int opponentHealth) { this.opponentHealth = opponentHealth; }
    public int getSelfResource() { return selfResource; }
    public void setSelfResource(int selfResource) { this.selfResource = selfResource; }
    public int getOpponentResource() { return opponentResource; }
    public void setOpponentResource(int opponentResource) { this.opponentResource = opponentResource; }
    public int getSelfScore() { return selfScore; }
    public void setSelfScore(int selfScore) { this.selfScore = selfScore; }
    public int getOpponentScore() { return opponentScore; }
    public void setOpponentScore(int opponentScore) { this.opponentScore = opponentScore; }
    public int getRoundNumber() { return roundNumber; }
    public void setRoundNumber(int roundNumber) { this.roundNumber = roundNumber; }
    public int getTotalRounds() { return totalRounds; }
    public void setTotalRounds(int totalRounds) { this.totalRounds = totalRounds; }
    public double getRandom() { return random; }
    public void setRandom(double random) { this.random = random; }
    public List<Action> getSelfHistory() { return selfHistory; }
    public void setSelfHistory(List<Action> selfHistory) { this.selfHistory = selfHistory; }
    public List<Action> getOpponentHistory() { return opponentHistory; }
    public void setOpponentHistory(List<Action> opponentHistory) { this.opponentHistory = opponentHistory; }

    public List<Action> history(String name) {
        if ("self".equals(name) || "self_history".equals(name)) {
            return selfHistory;
        }
        if ("opponent".equals(name) || "opponent_history".equals(name)) {
            return opponentHistory;
        }
        throw new IllegalArgumentException("Historial desconocido: " + name);
    }
}