package engine.rules;

import battlescript.model.Action;
import engine.context.Context;

public final class ElseRule implements Rule {
    private final Action action;
    public ElseRule(Action action) { this.action = action; }
    @Override public Action select(Context context) { return action; }
}
