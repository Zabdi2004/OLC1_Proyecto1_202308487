package engine.battle;

import java.util.*;

public final class BattleResult {
    private final String matchName, winner; 
    private final int rounds; 
    private final List<String> log;
    public BattleResult(String matchName, String winner, int rounds, List<String> log) { 
        this.matchName=matchName; 
        this.winner=winner; 
        this.rounds=rounds; 
        this.log=new ArrayList<String>(log); 
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
    public List<String> getLog() {
        return Collections.unmodifiableList(log); 
    }
}
