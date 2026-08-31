package battlescript.model;

import java.util.*;

/**
 * Singleton
 * Almacén central de todos los elementos definidos en el programa.
 *
 * Guarda las estrategias, partidas y el bloque main.
 */

//FINAL: nadie puede heredar de esta clase
public final class ProgramStore {

    // Estrategias registradas utilizando su nombre como clave.
    private final Map<String, Strategy> strategies = new HashMap<>();

    // Partidas registradas utilizando su nombre como clave.
    private final Map<String, Match> matches = new HashMap<>();

    // Bloque main del programa.
    private MainBlock mainBlock = new MainBlock();

    // Única instancia de ProgramStore.
    private static final ProgramStore INSTANCE = new ProgramStore();

    /**
     * Constructor privado.
     *
     * Al ser privado, ninguna otra clase puede crear directamente
     * una instancia de ProgramStore.
     */
    private ProgramStore() {}

    /**
     * Obtiene la única instancia existente de ProgramStore.
     */
    public static ProgramStore getInstance() {
        return INSTANCE;
    }

    public void clear() {
        strategies.clear();
        matches.clear();
        mainBlock = new MainBlock();
    }

    // ~ ~ ~ ~ ~ Métodos que usa el Parser ~ ~ ~ ~ ~ 
    //Registra todas las estrategias obtenidas por el Parser
    public void setStrategies(List<Strategy> list) {
        strategies.clear();
        for (Strategy s : list) {
            strategies.put(s.getName(), s);
        }
    }

    //Registra todas las partidas obtenidas por el Parser.
    public void setMatches(List<Match> list) {
        matches.clear();
        for (Match m : list) {
            matches.put(m.getName(), m);
        }
    }

    //Guarda las instrucciones del bloque main.
    public void setMain(List<RunInstruction> list) {
        mainBlock = new MainBlock(list);
    }

    // ~ ~ ~ ~ ~  Métodos para agregar elementos individualmente ~ ~ ~ ~ ~ 
     // Agrega una estrategia utilizando su nombre como ID
    public void addStrategy(Strategy s) {
        strategies.put(s.getName(), s);
    }

    //Agrega una partida utilizando su nombre como ID
    public void addMatch(Match m) {
        matches.put(m.getName(), m);
    }

    //Agrega una instrucción run al bloque main.
    public void addRunInstruction(RunInstruction ri) {
        mainBlock.addRunInstruction(ri);
    }

    // ~ ~ ~ ~ ~  Getters  ~ ~ ~ ~ ~ 
    
    //obtiene estrategia por nombre
    public Strategy getStrategy(String name) {
        return strategies.get(name);
    }

    //obtiene partida por nombre
    public Match getMatch(String name) {
        return matches.get(name);
    }

    //obtiene todas las estrategias registradas
    public List<Strategy> getStrategies() {
        return new ArrayList<>(strategies.values());
    }

    //obtiene todas las partidas registradas
    public List<Match> getMatches() {
        return new ArrayList<>(matches.values());
    }

    //obtiene el objeto que representa el bloque main
    public MainBlock getMainBlock() {
        return mainBlock;
    }

    //obtiene las instrucciones run del bloque main
    public List<RunInstruction> getMain() {
        return mainBlock.getRunInstructions();
    }
}