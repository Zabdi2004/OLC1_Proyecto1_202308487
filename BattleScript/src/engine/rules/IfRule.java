package engine.rules;

import battlescript.model.Action;
import engine.context.Context;
import engine.expressions.Expression;

/**
 * Estructura:
 * if condicion
 * then accion
 */
public class IfRule implements Rule {

    // Expresión que representa la condición del if.
    private final Expression condition;

    // Acción que se ejecutará si la condición es verdadera.
    private final Action action;


    //Constructor de una regla condicional.
    public IfRule(Expression condition, Action action) {
        this.condition = condition;
        this.action = action;
    }


    // Obtiene la expresión de la condición.
    public Expression getCondition() {return condition;}

    // Obtiene la acción asociada a la regla.
    public Action getAction() {return action;}


    /**
     * Evalúa la condición utilizando el estado actual de la batalla.
     *
     * Si la condición es verdadera, devuelve la acción asociada, sino = null
     */
    @Override
    public Action select(Context context) {

        // Si no existe una condición, la regla no puede cumplirse.
        if (condition == null) { return null; }

        // Evalúa la condición con el estado actual de la batalla.
        Object result = condition.evaluate(context);

        // Solo selecciona la acción cuando el resultado es true.
        if (result instanceof Boolean && (Boolean) result) { return action; }

        // La condición no se cumplió.
        return null;
    }
}