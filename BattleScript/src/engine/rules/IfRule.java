package engine.rules;

import battlescript.model.Action;
import engine.context.Context;
import engine.expressions.Expression;

public class IfRule implements Rule {
    private final Expression condition;
    private final Action action;

    public IfRule(Expression condition, Action action) {
        this.condition = condition;
        this.action = action;
    }

    public Expression getCondition() { return condition; }
    public Action getAction() { return action; }

    @Override
    public Action select(Context context) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}