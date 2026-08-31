package engine.battle;

import battlescript.model.Match;
import battlescript.model.ProgramStore;
import battlescript.model.RunInstruction;
import battlescript.model.Strategy;

import java.util.ArrayList;
import java.util.List;

public class ProgramExecutor {

    public List<BattleResult> execute(ProgramStore program) {

        List<BattleResult> results = new ArrayList<>();

        if (program == null) {
            return results;
        }

        if (program.getMainBlock() == null) {
            return results;
        }

        /*
         * Recorre cada instrucción RUN del bloque main.
         */
        for (RunInstruction run :
                program.getMainBlock().getRunInstructions()) {

            if (run == null) {
                continue;
            }

            int seed = run.getSeed();

            /*
             * Una instrucción RUN puede contener
             * varias partidas.
             */
            for (String matchName : run.getMatchIds()) {

                if (matchName == null) {
                    continue;
                }

                /*
                 * Buscar la partida.
                 */
                Match match =
                    program.getMatch(matchName);

                /*
                 * Si la partida no existe, se omite y
                 * se continúa con la siguiente.
                 */
                if (match == null) {
                    continue;
                }

                /*
                 * Obtener las estrategias de los jugadores.
                 */
                Strategy p1 =
                    program.getStrategy(match.getPlayerOne());

                Strategy p2 =
                    program.getStrategy(match.getPlayerTwo());

                /*
                 * Si alguna estrategia no existe,
                 * esta partida no puede ejecutarse.
                 *
                 * Pero NO detenemos las demás partidas.
                 */
                if (p1 == null || p2 == null) {
                    continue;
                }

                try {

                    /*
                     * Ejecutar solamente la partida
                     * que tiene todas sus dependencias.
                     */
                    BattleResult result =
                        new BattleEngine().run(
                            match,
                            p1,
                            p2,
                            seed
                        );

                    results.add(result);

                } catch (RuntimeException exception) {

                    /*
                     * Si una partida falla durante su ejecución,
                     * no se detienen las demás partidas.
                     */
                    continue;
                }
            }
        }
        return results;
    }
}