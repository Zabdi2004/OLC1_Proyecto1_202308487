package engine.analysis;

import analyzer.Error;
import battlescript.model.*;
import java.util.*;

public final class SemanticValidator {
    public List<Error> validate(ProgramStore program) {
        List<Error> errors = new ArrayList<Error>(); 
        Set<String> strategies = new HashSet<String>(); 
        Set<String> matches = new HashSet<String>();
        
        for (Strategy s : program.getStrategies()) { 
            if (!strategies.add(s.getName())){ 
                error(errors,"Estrategia duplicada: "+s.getName());
            } 
        }
        for (Match m : program.getMatches()) {
            if (!matches.add(m.getName())) {
                error(errors,"Partida duplicada: "+m.getName());
            } 
            if (!strategies.contains(m.getPlayerOne()) || !strategies.contains(m.getPlayerTwo())) {
                error(errors,"La partida "+m.getName()+" referencia jugadores inexistentes");
            }
        }
        for (RunInstruction run : program.getMain()){ 
            for (String id : run.getMatchIds()){ 
                if (!matches.contains(id)){ 
                    error(errors,"La instrucción run referencia una partida inexistente: "+id);
                }
            }
        }
        return errors;
    }
    private void error(List<Error> errors, String message) { 
        errors.add(new Error("Semántico", message, 0, 0)); 
    }
}
