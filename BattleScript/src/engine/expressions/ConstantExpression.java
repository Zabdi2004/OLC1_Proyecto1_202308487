package engine.expressions;

import engine.context.Context;

public final class ConstantExpression implements Expression {
    private final Object value;
    public ConstantExpression(Object value) { this.value = value; }
    @Override public Object evaluate(Context context) { return value; }
}
