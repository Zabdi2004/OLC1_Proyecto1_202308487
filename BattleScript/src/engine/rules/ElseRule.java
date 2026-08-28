package engine.rules;

import battlescript.model.Action;
import engine.context.Context;

public class ElseRule implements Rule {
    private final Action action;

    public ElseRule(Action action) { this.action = action; }
    public Action getAction() { return action; }

    @Override
    public Action select(Context context) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}