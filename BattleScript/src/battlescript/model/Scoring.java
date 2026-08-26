package battlescript.model;

public final class Scoring {
    private final int damagePoint, 
            healingPoint, 
            successfulDefense, 
            victoryBonus, 
            failedActionPenalty;
    public Scoring(int damagePoint, int healingPoint, int successfulDefense, int victoryBonus, int failedActionPenalty) {
        if (damagePoint <= 0 || healingPoint < 0 || successfulDefense < 0 || victoryBonus < 0 || failedActionPenalty < 0){
            throw new IllegalArgumentException("Valores de puntuación inválidos");
        }
        this.damagePoint = damagePoint; 
        this.healingPoint = healingPoint; 
        this.successfulDefense = successfulDefense;
        this.victoryBonus = victoryBonus; 
        this.failedActionPenalty = failedActionPenalty;
    }
    public int getDamagePoint() { return damagePoint; }
    public int getHealingPoint() { return healingPoint; }
    public int getSuccessfulDefense() { return successfulDefense; } 
    public int getVictoryBonus() { return victoryBonus; }
    public int getFailedActionPenalty() { return failedActionPenalty; }
}
