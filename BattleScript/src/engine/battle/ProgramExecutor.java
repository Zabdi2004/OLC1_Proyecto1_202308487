package engine.battle;

import battlescript.model.*; 
import java.util.*;
public final class ProgramExecutor {
    
    public List<BattleResult> execute(ProgramStore program){
        Map<String,Strategy>s=new HashMap<String,Strategy>();
        for(Strategy x:program.getStrategies()){
            s.put(x.getName(),x);
        }
        Map<String,Match>m=new HashMap<String,Match>();
        
        for(Match x:program.getMatches()){
            m.put(x.getName(),x);
        }
        
        List<BattleResult>out=new ArrayList<BattleResult>();
        
        for(RunInstruction r:program.getMain()){
            for(String id:r.getMatchIds()){
                Match x=m.get(id);
                if(x==null){
                    throw new IllegalStateException("Partida inexistente: "+id);
                }
                out.add(new BattleEngine().run(x,s.get(x.getPlayerOne()),s.get(x.getPlayerTwo()),r.getSeed()));
            }
        }
        
        return out;
    }
}
