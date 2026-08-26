package engine.context;

import battlescript.model.Action;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Valores disponibles mientras se evalúa una condición de una estrategia. */
public final class Context {
    private int selfHealth, 
            opponentHealth, 
            selfResource, 
            opponentResource, 
            selfScore, 
            opponentScore, 
            roundNumber, 
            totalRounds;
    private double random;
    private List<Action> selfHistory = new ArrayList<Action>();
    private List<Action> opponentHistory = new ArrayList<Action>();

    public Context() { }
    public int getSelfHealth() { return selfHealth; } 
    public void setSelfHealth(int value) { selfHealth = value; }
    public int getOpponentHealth() { return opponentHealth; } 
    public void setOpponentHealth(int value) { opponentHealth = value; }
    public int getSelfResource() { return selfResource; } 
    public void setSelfResource(int value) { selfResource = value; }
    public int getOpponentResource() { return opponentResource; } 
    public void setOpponentResource(int value) { opponentResource = value; }
    public int getSelfScore() { return selfScore; }
    public void setSelfScore(int value) { selfScore = value; }
    public int getOpponentScore() { return opponentScore; } 
    public void setOpponentScore(int value) { opponentScore = value; }
    public int getRoundNumber() { return roundNumber; } 
    public void setRoundNumber(int value) { roundNumber = value; }
    public int getTotalRounds() { return totalRounds; } 
    public void setTotalRounds(int value) { totalRounds = value; }
    public double getRandom() { return random; } 
    public void setRandom(double value) { random = value; }
    public List<Action> getSelfHistory() { return Collections.unmodifiableList(selfHistory); }
    public void setSelfHistory(List<Action> value) { selfHistory = new ArrayList<Action>(value); }
    public List<Action> getOpponentHistory() { return Collections.unmodifiableList(opponentHistory); }
    public void setOpponentHistory(List<Action> value) { opponentHistory = new ArrayList<Action>(value); }
    public List<Action> history(String owner) {
        if ("self".equals(owner)) return getSelfHistory();
        if ("opponent".equals(owner)) return getOpponentHistory();
        throw new IllegalArgumentException("Historial desconocido: " + owner);
    }
}
