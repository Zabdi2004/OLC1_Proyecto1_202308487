package engine.context;

import battlescript.model.Action;
import java.util.List;


/**
 * Contiene el estado actual de una estrategia durante una batalla
 * 
 * El contexto almacena la info que puede cambiar durante 
 * la ejecucion de las rondas y que las expresiones necesitan consultar
 * 
 */
public class Context {
    private int selfHealth; //vida actual del prota de la estrategia
    private int opponentHealth; //vida el oponente 
    private int selfResource;//recurso del prota: maná o energía
    private int opponentResource;//recurso del oponente
    private int selfScore;//puntuación acumulada del personaje
    private int opponentScore;//puntuación acum del oponente
    private int roundNumber;//número de la runda actual
    private int totalRounds;//cantidad total de rondas de la partida
    private double random; //valor random utilizado por las estrategias
    private List<Action> selfHistory;//historial de movs del prota
    private List<Action> opponentHistory;//historial de movs del oponente

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