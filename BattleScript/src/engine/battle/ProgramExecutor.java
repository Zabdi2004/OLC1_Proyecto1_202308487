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

        if (program.getMainBlock() == null) {
            throw new RuntimeException("No se encontró el bloque main");
        }

        for (RunInstruction run : program.getMainBlock().getRunInstructions()) {
            int seed = run.getSeed();
            for (String matchName : run.getMatchIds()) {
                Match match = program.getMatch(matchName);
                if (match == null) {
                    throw new RuntimeException("Partida no encontrada: " + matchName);
                }

                Strategy p1 = program.getStrategy(match.getPlayerOne());
                Strategy p2 = program.getStrategy(match.getPlayerTwo());
                if (p1 == null || p2 == null) {
                    throw new RuntimeException("Estrategia no encontrada en la partida " + matchName);
                }

                BattleResult result = new BattleEngine().run(match, p1, p2, seed);
                results.add(result);
            }
        }
        return results;
    }
}