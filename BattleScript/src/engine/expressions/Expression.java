package engine.expressions;

import engine.context.Context;

public interface Expression {
    Object evaluate(Context context);
    /*
    *Una expresión representa algo que puede producir un valor
    *cuando se evalúa durante la ejecución del programa     
    */
}