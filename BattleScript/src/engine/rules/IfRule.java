package engine.rules;

import battlescript.model.Action;
import engine.context.Context;
import engine.expressions.Expression;

public final class IfRule implements Rule {
    private final Expression condition; private final Action action;
    public IfRule(Expression condition, Action action) { this.condition = condition; this.action = action; }
    @Override public Action select(Context context) {
        Object result = condition.evaluate(context);
        if (!(result instanceof Boolean)) throw new IllegalStateException("La condición de if debe ser booleana");
        return (Boolean) result ? action : null;
    }
}
