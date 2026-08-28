package engine.analysis;

import analyzer.Error;
import battlescript.model.*;
import engine.rules.ElseRule;
import engine.rules.IfRule;
import engine.rules.Rule;

import java.util.*;

/**
 * Las validaciones incluyen:
 * - Nombres únicos para estrategias y partidas.
 * - Referencias a estrategias y partidas existentes.
 * - Acciones permitidas según la clase del estratega (mago/guerrero).
 * - Combos de acciones válidas para cada clase.
 * - Valores de puntuación positivos/no negativos.
 * - Semilla positiva y rondas > 0.
 * - Existencia del bloque main.
 */

public final class SemanticValidator {

    //Método principal de validación.
    public List<Error> validate(ProgramStore program) {
        List<Error> errors = new ArrayList<>();
        
        // Validación básica: si el programa es nulo, error inmediato.
        if (program == null) {
            errors.add(new Error("Semántico", "El programa es nulo", 0, 0));
            return errors;
        }

        // Validación del bloque main
        // Verificar que exista un bloque main y que contenga al menos una instrucción run.
        // Si no hay main o está vacío, no hay partidas que ejecutar.
        if (program.getMainBlock() == null || program.getMainBlock().getRunInstructions().isEmpty()) {
            errors.add(new Error("Semántico", "No se encontró el bloque 'main' o está vacío", 0, 0));
        }

        // Validación de estrategias
        // Usamos un Set para detectar nombres duplicados de estrategias.
        Set<String> strategyNames = new HashSet<>();
        for (Strategy s : program.getStrategies()) {
            if (s == null) continue; // si alguna estrategia es null, la saltamos.
            String name = s.getName();
            
            // Validar que el nombre no sea vacío o nulo.
            if (name == null || name.trim().isEmpty()) {
                errors.add(new Error("Semántico", "Estrategia con nombre vacío o nulo", 0, 0));
                continue;
            }
            
            // Si no se puede agregar al Set, significa que ya existe
            if (!strategyNames.add(name)) {
                errors.add(new Error("Semántico", "Estrategia duplicada: " + name, 0, 0));
            }
        }

        // validación de partidas
        Set<String> matchNames = new HashSet<>();
        for (Match m : program.getMatches()) {
            if (m == null) continue;
            String name = m.getName();
            
            // Validar nombre de partida.
            if (name == null || name.trim().isEmpty()) {
                errors.add(new Error("Semántico", "Partida con nombre vacío o nulo", 0, 0));
                continue;
            }
            
            // Detectar partidas duplicadas.
            if (!matchNames.add(name)) {
                errors.add(new Error("Semántico", "Partida duplicada: " + name, 0, 0));
            }

            // validación  de jugadores
            String p1 = m.getPlayerOne();
            String p2 = m.getPlayerTwo();
            
            if (p1 == null || p1.trim().isEmpty() || p2 == null || p2.trim().isEmpty()) {
                errors.add(new Error("Semántico", "La partida " + name + " tiene jugadores vacíos", 0, 0));
            } else {
                // Verificar que los nombres de los jugadores estén en el Set de estrategias.
                boolean p1Exists = strategyNames.contains(p1);
                boolean p2Exists = strategyNames.contains(p2);
                
                if (!p1Exists) {
                    errors.add(new Error("Semántico", "La partida " + name + " referencia a '" + p1 + "' que no es una estrategia válida", 0, 0));
                }
                if (!p2Exists) {
                    errors.add(new Error("Semántico", "La partida " + name + " referencia a '" + p2 + "' que no es una estrategia válida", 0, 0));
                }
            }

            // validar número de rondas (> 0)
            if (m.getRounds() <= 0) {
                errors.add(new Error("Semántico", "La partida " + name + " tiene un número de rondas inválido (debe ser > 0): " + m.getRounds(), 0, 0));
            }

            // validar sistema de puntuación (SCORING)
            Scoring scoring = m.getScoring();
            if (scoring == null) {
                errors.add(new Error("Semántico", "La partida " + name + " no tiene sistema de puntuación", 0, 0));
            } else {
                // Extraer valores para validarlos.
                int dp = scoring.getDamagePoint();
                int hp = scoring.getHealingPoint();
                int sd = scoring.getSuccessfulDefense();
                int vb = scoring.getVictoryBonus();
                int fp = scoring.getFailedActionPenalty();
                
                // Según el enunciado: damage_point debe ser > 0; los demás >= 0.
                if (dp <= 0) {
                    errors.add(new Error("Semántico", "La partida " + name + ": damage_point debe ser > 0 (actual: " + dp + ")", 0, 0));
                }
                if (hp < 0) {
                    errors.add(new Error("Semántico", "La partida " + name + ": healing_point no puede ser negativo (actual: " + hp + ")", 0, 0));
                }
                if (sd < 0) {
                    errors.add(new Error("Semántico", "La partida " + name + ": successful_defense no puede ser negativo (actual: " + sd + ")", 0, 0));
                }
                if (vb < 0) {
                    errors.add(new Error("Semántico", "La partida " + name + ": victory_bonus no puede ser negativo (actual: " + vb + ")", 0, 0));
                }
                if (fp < 0) {
                    errors.add(new Error("Semántico", "La partida " + name + ": failed_action_penalty no puede ser negativo (actual: " + fp + ")", 0, 0));
                }
            }

            // validar bonificaciones (COMBOS)
            Bonuses bonuses = m.getBonuses();
            if (bonuses != null) {
                //Validar combo de mago
                List<Action> mageCombo = bonuses.getMageCombo();
                if (mageCombo != null && !mageCombo.isEmpty()) {
                    int index = 0;
                    for (Action action : mageCombo) {
                        // Cada acción del combo debe ser de tipo MAGE.
                        if (action.getOwner() != ClassType.MAGE) {
                            errors.add(new Error("Semántico", "La partida " + name + " contiene en 'mage_combo' la acción " + action + " que no es de mago (posición " + index + ")", 0, 0));
                        }
                        index++;
                    }
                }
                
                //Validar combo de guerrero
                List<Action> warriorCombo = bonuses.getWarriorCombo();
                if (warriorCombo != null && !warriorCombo.isEmpty()) {
                    int index = 0;
                    for (Action action : warriorCombo) {
                        // Cada acción del combo debe ser de tipo WARRIOR.
                        if (action.getOwner() != ClassType.WARRIOR) {
                            errors.add(new Error("Semántico", "La partida " + name + " contiene en 'warrior_combo' la acción " + action + " que no es de guerrero (posición " + index + ")", 0, 0));
                        }
                        index++;
                    }
                }
                
                // Validar que los puntos de combo no sean negativos
                if (bonuses.getMageComboPoints() < 0) {
                    errors.add(new Error("Semántico", "La partida " + name + ": mage_combo_points no puede ser negativo (actual: " + bonuses.getMageComboPoints() + ")", 0, 0));
                }
                if (bonuses.getWarriorComboPoints() < 0) {
                    errors.add(new Error("Semántico", "La partida " + name + ": warrior_combo_points no puede ser negativo (actual: " + bonuses.getWarriorComboPoints() + ")", 0, 0));
                }
                if (bonuses.getLowHealthVictory() < 0) {
                    errors.add(new Error("Semántico", "La partida " + name + ": low_health_victory no puede ser negativo (actual: " + bonuses.getLowHealthVictory() + ")", 0, 0));
                }
            }
        }

        // validar acciones de las estrategias
        // Por cada estrategia, validamos que las acciones (initial, reglas IF y ELSE)
        // sean compatibles con su clase (MAGE o WARRIOR).
        for (Strategy s : program.getStrategies()) {
            if (s == null) continue;
            String strategyName = s.getName();
            ClassType type = s.getClassType();

            // Validar acción inicial
            Action initialAction = s.getInitialAction();
            if (initialAction == null) {
                errors.add(new Error("Semántico", "La estrategia " + strategyName + " no tiene acción inicial", 0, 0));
            } else if (initialAction.getOwner() != type) {
                // La acción inicial debe pertenecer a la clase del estratega.
                errors.add(new Error("Semántico", "La estrategia " + strategyName + " tiene acción inicial '" + initialAction + "' que no es de su clase (" + type + ")", 0, 0));
            }

            //Validar reglas
            List<Rule> rules = s.getRules();
            if (rules == null) continue; // Si no hay reglas, no hay nada que validar.
            
            int ruleIndex = 0;
            for (Rule rule : rules) {
                if (rule instanceof IfRule) {
                    IfRule ifRule = (IfRule) rule;
                    Action action = ifRule.getAction();
                    
                    // Verificar que la regla tenga una acción.
                    if (action == null) {
                        errors.add(new Error("Semántico", "La estrategia " + strategyName + " tiene una regla IF sin acción (índice " + ruleIndex + ")", 0, 0));
                    } else if (action.getOwner() != type) {
                        // La acción del IF debe ser de la misma clase.
                        errors.add(new Error("Semántico", "La estrategia " + strategyName + " tiene acción '" + action + "' en regla IF que no es de su clase (" + type + ")", 0, 0));
                    }
                    
                    // Además, la condición no debe ser nula (por seguridad).
                    if (ifRule.getCondition() == null) {
                        errors.add(new Error("Semántico", "La estrategia " + strategyName + " tiene una regla IF sin condición (índice " + ruleIndex + ")", 0, 0));
                    }
                    
                } else if (rule instanceof ElseRule) {
                    ElseRule elseRule = (ElseRule) rule;
                    Action action = elseRule.getAction();
                    
                    if (action == null) {
                        errors.add(new Error("Semántico", "La estrategia " + strategyName + " tiene una regla ELSE sin acción (índice " + ruleIndex + ")", 0, 0));
                    } else if (action.getOwner() != type) {
                        // La acción del ELSE debe ser de la misma clase.
                        errors.add(new Error("Semántico", "La estrategia " + strategyName + " tiene acción '" + action + "' en regla ELSE que no es de su clase (" + type + ")", 0, 0));
                    }
                    
                } else {
                    // Si la regla no es IF ni ELSE, es un error (no debería ocurrir).
                    errors.add(new Error("Semántico", "La estrategia " + strategyName + " contiene una regla desconocida (índice " + ruleIndex + ")", 0, 0));
                }
                ruleIndex++;
            }
        }

        // validar instrucciones run (PUNTO DE ENTRADA)
        // Verificar que las partidas referenciadas en el main existan y que la semilla sea positiva.
        if (program.getMainBlock() != null) {
            for (RunInstruction run : program.getMainBlock().getRunInstructions()) {
                if (run == null) continue;
                
                // Validar semilla (debe ser > 0).
                if (run.getSeed() <= 0) {
                    errors.add(new Error("Semántico", "Semilla inválida en RUN: " + run.getSeed() + " (debe ser > 0)", 0, 0));
                }
                
                // Validar que cada partida referenciada exista.
                for (String matchId : run.getMatchIds()) {
                    if (!matchNames.contains(matchId)) {
                        errors.add(new Error("Semántico", "La instrucción RUN referencia una partida inexistente: " + matchId, 0, 0));
                    }
                }
            }
        }

        // Retornar la lista de errores encontrados (vacía si todo está correcto).
        return errors;
    }
}