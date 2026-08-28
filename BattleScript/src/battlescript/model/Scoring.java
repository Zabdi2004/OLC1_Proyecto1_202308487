package battlescript.model;

public class Scoring {
    private final int damagePoint;
    private final int healingPoint;
    private final int successfulDefense;
    private final int victoryBonus;
    private final int failedActionPenalty;

    // Constructor que usa el Parser
    public Scoring(int damagePoint, int healingPoint, int successfulDefense, int victoryBonus, int failedActionPenalty) {
        this.damagePoint = damagePoint;
        this.healingPoint = healingPoint;
        this.successfulDefense = successfulDefense;
        this.victoryBonus = victoryBonus;
        this.failedActionPenalty = failedActionPenalty;
    }

    // Constructor por defecto (para el parser si no encuentra)
    public Scoring() {
        this(1, 1, 20, 100, 10);
    }

    public int getDamagePoint() { return damagePoint; }
    public int getHealingPoint() { return healingPoint; }
    public int getSuccessfulDefense() { return successfulDefense; }
    public int getVictoryBonus() { return victoryBonus; }
    public int getFailedActionPenalty() { return failedActionPenalty; }
}