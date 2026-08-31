package battlescript.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa una instrucción run del bloque main.
 *
 * Una instrucción run indica qué partidas deben ejecutarse
 * y qué semilla aleatoria debe utilizarse durante la ejecución.
 */
public final class RunInstruction {

    // Identificadores de las partidas
    private final List<String> matchIds;

    // Semilla utilizada para controlar la generación de valores aleatorios.
    private final int seed;

    /**
     * Crea una instrucción run.
     *
     * Se valida que exista al menos una partida y que la semilla
     * sea un número positivo.
     */
    public RunInstruction(List<String> matchIds, int seed) {

        if (matchIds == null || matchIds.isEmpty() || seed <= 0) {
            throw new IllegalArgumentException(
                "La instrucción run requiere partidas y una semilla positiva"
            );
        }

        // Se crea una copia para proteger la lista original.
        this.matchIds = new ArrayList<>(matchIds);

        this.seed = seed;
    }

    //Obtiene los identificadores de las partidas que se ejecutarán.
    public List<String> getMatchIds() {
        return Collections.unmodifiableList(matchIds);
    }

    // Obtiene la semilla utilizada para la ejecución.
    public int getSeed() {
        return seed;
    }
}