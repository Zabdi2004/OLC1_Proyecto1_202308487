package battlescript.model;

import engine.rules.Rule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Strategy {
    private final String name;
    private final ClassType classType;
    private final Action initialAction;
    private final List<Rule> rules;

    // Constructor que usa el Parser
    public Strategy(String name, ClassType classType, Action initialAction, List<Rule> rules) {
        this.name = name;
        this.classType = classType;
        this.initialAction = initialAction;
        this.rules = new ArrayList<>(rules);
    }

    // Constructor por defecto (si lo necesitas)
    public Strategy() {
        this("", ClassType.WARRIOR, Action.SLASH, new ArrayList<>());
    }

    public String getName() { return name; }
    public ClassType getClassType() { return classType; }
    public Action getInitialAction() { return initialAction; }
    public List<Rule> getRules() { return Collections.unmodifiableList(rules); }
}