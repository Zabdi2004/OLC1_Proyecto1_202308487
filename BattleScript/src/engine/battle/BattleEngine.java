package engine.battle;

import battlescript.model.*; 
import engine.context.Context; 
import java.util.*;

public final class BattleEngine {
    public BattleResult run(Match match, Strategy first, Strategy second, int seed) {
        Fighter a=new Fighter(first), 
                b=new Fighter(second); 
        Random ra=new Random(seed), 
                rb=new Random(seed+1L); 
        List<String> log=new ArrayList<String>(); 
        int completed=0;
        for (int round=0; round<match.getRounds() && a.alive() && b.alive(); round++) {
            completed=round+1; 
            Action aa=a.strategy.selectAction(context(a,b,round,match.getRounds(),
                    round==0?0:ra.nextDouble())); 
            Action ab=b.strategy.selectAction(context(b,a,round,match.getRounds(),
                    round==0?0:rb.nextDouble())); 
            executePair(a,aa,b,ab,match,log); 
        }
        String winner=winner(a,b); 
        if (!"Empate".equals(winner)) { 
            Fighter f=winner.equals(a.strategy.getName())?a:b;
            f.score+=match.getScoring().getVictoryBonus(); 
            if(f.health*4<=f.maxHealth) {
                f.score+=match.getBonuses().getLowHealthVictory();
            } 
        }
        log.add("Resultado: "+winner); 
        return new BattleResult(match.getName(),winner,completed,log);
    }
    private Context context(Fighter self,Fighter other,int round,int total,double random) { 
        Context c=new Context(); 
        c.setSelfHealth(self.health);
        c.setOpponentHealth(other.health);
        c.setSelfResource(self.resource);
        c.setOpponentResource(other.resource);
        c.setSelfScore(self.score);
        c.setOpponentScore(other.score);
        c.setRoundNumber(round);
        c.setTotalRounds(total);
        c.setRandom(random);
        c.setSelfHistory(self.history);
        c.setOpponentHistory(other.history);
        return c; 
    }
    private void executePair(Fighter a,Action aa,Fighter b,Action ab,Match match,List<String> log) { 
        boolean first=aa.getPriority()>ab.getPriority() || (aa.getPriority()==ab.getPriority() && a.speed>=b.speed); 
        if(first){
            execute(a,aa,b,match,log);
            if(b.alive())execute(b,ab,a,match,log);
        }
        else{
            execute(b,ab,a,match,log);
            if(a.alive())execute(a,aa,b,match,log);
        } 
        a.defending=false;
        b.defending=false; 
    }
    private void execute(Fighter self,Action action,Fighter other,Match match,List<String> log) { 
        if(self.resource<action.getResourceCost()){
            self.score=Math.max(0,self.score-match.getScoring().getFailedActionPenalty());
            log.add(self.strategy.getName()+": acción fallida "+action);return;
        } 
        self.resource-=action.getResourceCost(); 
        self.history.add(action); 
        if(action.isDefense()){
            self.defending=true;
        } else if(action==Action.WAR_CRY){
            self.warCry=true;
        } 
        else if(action.isHealing()){
            int gained=Math.min(25,self.maxHealth-self.health);
            self.health+=gained;
            self.score+=gained*match.getScoring().getHealingPoint();
        } 
        else if(action.isRecovery()){
            self.resource=Math.min(self.maxResource,self.resource+25);
        }
        else if(action.isOffensive()){
            int damage=action.getPower()
                    +(action.getOwner()==ClassType.MAGE?self.magicPower:self.physicalAttack)
                    +(self.warCry?10:0)
                    -(action.getOwner()==ClassType.MAGE?other.magicResistance:other.armor);
            self.warCry=false;
            damage=Math.max(1,damage);
            if(other.defending){
                damage=(int)Math.floor(damage*.5);
                if(damage>0){
                    other.score+=match.getScoring().getSuccessfulDefense();
                }
            }
            other.health=Math.max(0,other.health-damage);
            self.score+=damage*match.getScoring().getDamagePoint();
        } 
        awardCombo(self,match);
        log.add(self.strategy.getName()+" usa "+action); 
    }
    
    private void awardCombo(Fighter f,Match m){
        List<Action> combo=f.strategy.getClassType()==ClassType.MAGE? m.getBonuses().getMageCombo(): m.getBonuses().getWarriorCombo();
        if(!combo.isEmpty()&&f.history.size()>=combo.size()&&f.history.subList(f.history.size()
                -combo.size(),f.history.size()).equals(combo)){
            f.score+=f.strategy.getClassType()==ClassType.MAGE? m.getBonuses().getMageComboPoints() : m.getBonuses().getWarriorComboPoints();
        }
    }
    private String winner(Fighter a,Fighter b){
        if(!a.alive()&&!b.alive()){
            return "Empate";
        }
        if(!a.alive()){
            return b.strategy.getName();
        }
        if(!b.alive()){
            return a.strategy.getName();
        }
        if(a.score!=b.score){
            return a.score>b.score?a.strategy.getName():b.strategy.getName();
        }
        if(a.health!=b.health){
            return a.health>b.health?a.strategy.getName():b.strategy.getName();
        }
        if(a.resource!=b.resource){
            return a.resource>b.resource?a.strategy.getName():b.strategy.getName();
        }
        return "Empate";
    }
}
