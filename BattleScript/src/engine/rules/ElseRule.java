package engine.rules;

import battlescript.model.Action;
import engine.context.Context;

public class ElseRule implements Rule {

    // Acción que se ejecutará como alternativa.
    private final Action action;


    //Constructor de la regla else.
    public ElseRule(Action action) {this.action = action;}

    // Obtiene la acción asociada al else.
    public Action getAction() {return action;}

    /**
     * Selecciona directamente la acción del else.
     *
     * Como esta regla no tiene una condición,
     * siempre devuelve su acción.
     */
    @Override
    public Action select(Context context) {return action;}
}