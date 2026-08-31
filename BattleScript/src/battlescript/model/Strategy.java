package battlescript.model;

import engine.rules.Rule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa una estrategia o personaje
 *
 * Una estrategia contiene:
 * - nombre
 * - tipo de personaje
 * - acción inicial
 * - conjunto de reglas que determinan sus acciones durante la batalla
 */
public class Strategy {
    private final String name;// Nombre de estrategia
    private final ClassType classType; // Tipo de personaje

    // Acción que ejecutará el personaje al comenzar una partida.
    private final Action initialAction;

    // Reglas utilizadas para decidir las acciones posteriores.
    private final List<Rule> rules;

    /**
     * Constructor utilizado por el Parser.
     *
     * El Parser crea una Strategy cuando encuentra una declaración
     * mage o warrior en el archivo
     */
    public Strategy(String name, ClassType classType, Action initialAction, List<Rule> rules) {
        this.name = name;
        this.classType = classType;
        this.initialAction = initialAction;

        // copia de la lista para evitar modificaciones externas
        this.rules = new ArrayList<>(rules);
    }

    /**
     * Constructor por defecto.
     *
     * Se utiliza cuando se necesita crear una estrategia
     * con valores iniciales válidos.
     */
    public Strategy() {
        this("", ClassType.WARRIOR, Action.SLASH, new ArrayList<>());
    }

    public String getName() { return name; }
    public ClassType getClassType() { return classType; }
    public Action getInitialAction() { return initialAction; }
    
    // getter de las reglas de la estrategia 
    public List<Rule> getRules() { return Collections.unmodifiableList(rules); }
}