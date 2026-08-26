package battlescript.model;

import engine.context.Context;
import engine.rules.Rule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Strategy {
    private final String name;
    private final ClassType classType;
    private final Action initialAction;
    private final List<Rule> rules;

    public Strategy(String name, ClassType classType, Action initialAction, List rules) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la estrategia es obligatorio");
        }
        if (initialAction == null || initialAction.getOwner() != classType) {
            throw new IllegalArgumentException("La acción inicial no corresponde a la clase");
        }
        this.name = name;
        this.classType = classType;
        this.initialAction = initialAction;
        this.rules = new ArrayList<Rule>(rules);
    }

    public String getName() { 
        return name; 
    }
    public ClassType getClassType() { 
        return classType; 
    }
    public Action getInitialAction() { 
        return initialAction; 
    }
    public List<Rule> getRules() { 
        return Collections.unmodifiableList(rules); 
    }

    public Action selectAction(Context context) {
        if (context.getRoundNumber() == 0) {
            return initialAction;
        }
        for (Rule rule : rules) {
            Action action = rule.select(context);
            if (action != null) {
                return action;
            }
        }
        throw new IllegalStateException("La estrategia " + name + " no tiene acción por defecto");
    }
}
