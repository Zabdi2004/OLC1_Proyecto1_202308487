package battlescript.model;

/**
 * Representa una partida definida
 *
 * Una partida contiene los 2 jugadores, el número de rondas,
 * las reglas de puntuación y los bonos disponibles.
 */
public class Match {

    // Nombre de partida.
    private final String name;

    // Nombre de la estrategia del primer jugador.
    private final String playerOne;

    // Nombre de la estrategia del segundo jugador.
    private final String playerTwo;

    // Cantidad máxima de rondas de la partida.
    private final int rounds;

    // Configuración de puntos y penalizaciones de la partida.
    private final Scoring scoring;

    // Bonos adicionales que pueden obtener los jugadores.
    private final Bonuses bonuses;

    /**
     * Constructor utilizado por el Parser.
     * Recibe toda la información que fue obtenida al analizar 
     * una declaración match del archivo BattleScript.
     */
    public Match(
            String name,
            String playerOne,
            String playerTwo,
            int rounds,
            Scoring scoring,
            Bonuses bonuses) {

        this.name = name;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.rounds = rounds;
        this.scoring = scoring;
        this.bonuses = bonuses;
    }

    // Constructor por defecto. Pa crear un Match con valores iniciales válidos.
    public Match() {
        this("", "", "", 0, new Scoring(), new Bonuses());
    }

    public String getName() { return name; }
    public String getPlayerOne() { return playerOne; }
    public String getPlayerTwo() { return playerTwo; }
    public int getRounds() { return rounds; }
    public Scoring getScoring() { return scoring; }
    public Bonuses getBonuses() { return bonuses; }
}