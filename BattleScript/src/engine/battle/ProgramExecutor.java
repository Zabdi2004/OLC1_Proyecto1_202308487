package engine.battle;

import battlescript.model.Match;
import battlescript.model.ProgramStore;
import battlescript.model.RunInstruction;
import battlescript.model.Strategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Se encarga de ejecutar las instrucciones contenidas
 * en el bloque main del programa 
 * 
 * Su función es:
 *
 * 1. Obtener las instrucciones run del bloque main.
 * 2. Buscar las partidas indicadas en cada instrucción.
 * 3. Obtener las estrategias de los jugadores.
 * 4. Crear y ejecutar el BattleEngine.
 * 5. Guardar los resultados de cada partida.
 */
public class ProgramExecutor {

    public List<BattleResult> execute(ProgramStore program) {

        // Lista donde se almacenarán los resultados de las partidas.
        List<BattleResult> results = new ArrayList<>();

        //Verificar que el bloque main exista
        if (program.getMainBlock() == null) {
            throw new RuntimeException(
                "No se encontró el bloque main"
            );
        }

        //Recorre cada instrucción run definida dentro del main.
        for (RunInstruction run : program.getMainBlock().getRunInstructions()) {

            // Obtiene la semilla utilizada para la ejecución.
            int seed = run.getSeed();

            /*
             * Una misma instrucción run puede contener varias
             * partidas, por lo que se recorren sus nombres.
             */
            for (String matchName : run.getMatchIds()) {
                // Busca la partida dentro del programa.
                Match match = program.getMatch(matchName);

                //verificar que la partida exista
                if (match == null) {

                    throw new RuntimeException(
                        "Partida no encontrada: " + matchName
                    );
                }

                /*
                 * Obtiene las estrategias correspondientes a los
                 * dos jugadores definidos en la partida.
                 */
                Strategy p1 = program.getStrategy(match.getPlayerOne());

                Strategy p2 = program.getStrategy(match.getPlayerTwo());

                //Verifica que ambas estrategias existan.
                if (p1 == null || p2 == null) {
                    throw new RuntimeException(
                        "Estrategia no encontrada en la partida "
                        + matchName
                    );
                }
                
                /*
                 * Se crea el motor de batalla y se ejecuta
                 * la partida.
                 *
                 * Aquí ProgramExecutor deja de encargarse
                 * de la ejecución y BattleEngine toma el control
                 * de la lógica del combate.
                 */
                BattleResult result =
                    new BattleEngine().run(
                        match,
                        p1,
                        p2,
                        seed
                    );

                // Guarda el resultado de la partida.
                results.add(result);
            }
        }
        // Devuelve los resultados de todas las partidas ejecutadas.
        return results;
    }
}