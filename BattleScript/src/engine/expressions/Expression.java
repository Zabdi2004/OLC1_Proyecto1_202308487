package engine.expressions;

import engine.context.Context;

public interface Expression {
    Object evaluate(Context context);
}