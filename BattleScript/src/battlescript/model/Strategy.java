package battlescript.model;

import engine.rules.Rule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa la estrategia de un personaje
 *
 * Una estrategia define:
 *
 * - El nombre del personaje.
 * - El tipo de personaje: mago o guerrero.
 * - La acción inicial que realizará.
 * - La lista de reglas que utilizará para decidir sus acciones.
 */
public class Strategy {

    // Nombre de la estrategia o personaje
    private final String name;

    private final ClassType classType;

    // Acción que se ejecuta al inicio de la batalla
    private final Action initialAction;

    // Reglas que determinan las acciones posteriores del personaje.
    private final List<Rule> rules;

    /**
     * Constructor utilizado principalmente por el Parser
     * para construir una estrategia a partir del programa leído.
     */
    public Strategy(String name, ClassType classType, Action initialAction, List<Rule> rules) {
        this.name = name;
        this.classType = classType;
        this.initialAction = initialAction;

        //Se crea una nueva lista a partir de la recibida.
        this.rules = new ArrayList<>(rules);
    }

    //Constructor vacío.
    public Strategy() {
        this("", ClassType.WARRIOR, Action.SLASH, new ArrayList<>());
    }

    // Obtiene el nombre de la estrategia.
    public String getName() { return name; }

    // Obtiene el tipo de personaje.
    public ClassType getClassType() { return classType; }
    
    // Obtiene la acción inicial.
    public Action getInitialAction() { return initialAction; }

    //Obtiene las reglas de la estrategia.
    public List<Rule> getRules() { return Collections.unmodifiableList(rules); }
}