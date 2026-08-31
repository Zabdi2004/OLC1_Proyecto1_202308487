package battlescript.model;

/**
 * Contiene la configuración de puntuación de una partida.
 *
 * Define cuántos puntos se obtienen o pierden por las diferentes
 * acciones realizadas durante el combate.
 */
public class Scoring {

    //Puntos obtenidos por:
    
    private final int damagePoint; //daño

    private final int healingPoint; //curación

    private final int successfulDefense; //defensa exitosa

    private final int victoryBonus; //adicionales por ganar la partida

    // Puntos descontados por realizar una acción fallida.
    private final int failedActionPenalty;

    //Constructor utilizado por el Parser.
    public Scoring(int damagePoint, int healingPoint, int successfulDefense, int victoryBonus, int failedActionPenalty) {
        this.damagePoint = damagePoint;
        this.healingPoint = healingPoint;
        this.successfulDefense = successfulDefense;
        this.victoryBonus = victoryBonus;
        this.failedActionPenalty = failedActionPenalty;
    }

    /**
     * Constructor por defecto.
     *
     * Proporciona los valores de puntuación establecidos
     * por defecto cuando no se especifica un bloque scoring.
     */
    public Scoring() {
        this(1, 1, 20, 100, 10);
    }

    //Getters
    public int getDamagePoint() { return damagePoint; }
    public int getHealingPoint() { return healingPoint; }
    public int getSuccessfulDefense() { return successfulDefense; }
    public int getVictoryBonus() { return victoryBonus; }
    public int getFailedActionPenalty() { return failedActionPenalty; }
}