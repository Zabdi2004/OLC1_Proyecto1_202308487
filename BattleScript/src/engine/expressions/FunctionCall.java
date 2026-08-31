package engine.expressions;

import battlescript.model.Action;
import engine.context.Context;
import java.util.List;

/**
 * Representa una llamada a una función propia del lenguaje 
 * 
 * Esta clase permite ejecutar funciones que trabajan principalmente
 * con los historiales de movimientos de los jugadores.
 *
 * También permite realizar llamadas a funciones anidadas. Por ejemplo:
 *
 * get_moves_count(
 *     get_last_n_moves(opponent_history, 3),
 *     SLASH)
 */
public class FunctionCall implements Expression {

    // Nombre de la función que se debe ejecutar.
    private final String name;

    /**
     * Representa el origen del historial que utilizará la función.
     * 1."self" o "opponent"
     * 2. Expression:
     *    Representa una expresión que al evaluarse devuelve
     *    una lista de movimientos.
     *    Permite utilizar funciones anidadas.
     */
    private final Object historySource;

    /**
     * Representa el segundo argumento de la función, cuando existe.
     * get_last_n_moves(opponent_history, 3)
     *                                    ^
     *                                    |
     */
    private final Expression argumentExpr;

    /**
     * Constructor utilizado cuando el historial se proporciona
     * directamente mediante self_history u opponent_history.
     */
    public FunctionCall(String name, String historyName, Expression argumentExpr) {
        this.name = name;
        this.historySource = historyName;
        this.argumentExpr = argumentExpr;
    }


    /**
     * Constructor utilizado cuando el historial proviene
     * de otra función o expresión.
     */
    public FunctionCall(String name, Expression historyExpr, Expression argumentExpr) {
        this.name = name;
        this.historySource = historyExpr;
        this.argumentExpr = argumentExpr;
    }


    /**
     * Constructor utilizado por funciones que solamente
     * necesitan recibir un historial.
     */
    public FunctionCall(String name, String historyName) {

        //Como esta función no tiene segundo argumento, se envía null. 
        this(name, historyName, null);
    }

    /**
     * Evalúa la función utilizando el estado actual de la batalla.
     *
     * El Context proporciona información dinámica de la batalla,
     * como los historiales de movimientos.
     *
     * Primero se obtiene el historial que utilizará la función
     * y después se ejecuta la operación correspondiente según
     * el nombre de la función.
     */
    @Override
    public Object evaluate(Context context) {

        try {
            
            // Variable donde se almacenará el historial que utilizará la función.
            List<Action> history;

            /**
             * historySource es un String.
             * como self_history u opponent_history.
             */
            if (historySource instanceof String) {

                /*
                 * Context.history() recibe "self" u "opponent"
                 * y devuelve la lista de movimientos correspondiente.
                 */
                history = context.history((String) historySource);

            /**
             * historySource es otra Expression.
             *
             * Esto sucede cuando una función utiliza como historial
             * el resultado de otra función.
             * get_moves_count(get_last_n_moves(opponent_history, 3),SLASH)
             *                         ^
             *                         |
             */
            } else if (historySource instanceof Expression) {

                /*
                 * Se evalúa la expresión interna: get_last_n_moves(opponent_history, 3)
                 * y se obtendría una lista de movimientos.
                 */
                Object result = ((Expression) historySource).evaluate(context);
                
                if (!(result instanceof List)) { return null; }
                
                //Se convierte el resultado a una lista de Action.
                history = (List<Action>) result;

            /*
             * Si historySource no es ni un String ni una Expression,
             * no existe un historial válido para ejecutar la función.
             */
            } else { return null; }
            
            switch (name) {
                case "last_move":

                    //Si el historial está vacío, no existe un último movimiento.
                    if (history.isEmpty()) { return null; }
                    
                    return history.get( history.size() - 1 );
                    
                case "get_move":

                    /*
                     * Se evalúa el segundo argumento para obtener
                     * el índice solicitado.
                     */
                    int index = (Integer) argumentExpr.evaluate(context);

                    //Se verifica que el índice exista dentro del historial.
                    if (index < 0 || index >= history.size()) { return null; }

                    //Se devuelve el movimiento ubicado en la posición solicitada.
                    return history.get(index);
                    
                case "get_moves_count": {

                    //Se evalúa el segundo argumento
                    Object argVal = argumentExpr.evaluate(context);
                    
                    Action action;

                    if (argVal instanceof Action) {
                        
                        action = (Action) argVal;

                    /*
                     * Si por alguna razón el argumento llega como
                     * String, se convierte al Action correspondiente.
                     */
                    
                    } else if (argVal instanceof String) {
                        action = Action.fromString((String) argVal);

                    /*
                     * Si el segundo argumento no representa una acción,
                     * no se puede realizar el conteo.
                     */
                    } else {

                        throw new RuntimeException("Argumento inválido para get_moves_count: " + argVal );
                    }

                    /*
                     * Se recorren todos los movimientos del historial
                     * y se cuentan aquellos que son iguales a la acción
                     * solicitada.
                     */
                    int count = 0;

                    for (Action move : history) {
                        if (move == action) {
                            count++;
                        }
                    }

                    return count;
                }

                case "get_last_n_moves": {

                    /**
                     * Se obtiene la cantidad de movimientos
                     * que se desea recuperar.
                     * get_last_n_moves(opponent_history, 3)
                     *                                    ^
                     */
                    int n = (Integer) argumentExpr.evaluate(context);
                    
                    if (n <= 0) {
                        /**Si la cantidad a evaluar es menor a cero*
                         * Retornamos una lista vacia para evitar errores
                         */
                        return new java.util.ArrayList<Action>();
                    }

                    /*
                     * Se calcula desde qué posición se deben tomar
                     * los movimientos.
                     * history = [A, B, C, D, E]
                     * n = 3
                     * history.size() = 5
                     * inicio = 5 - 3 = 2
                     * Se toman:
                     * [C, D, E]
                     */
                    
                    int inicio = Math.max(0, history.size() - n);
                    //Math.max() evita obtener un índice negativo cuando 
                    //todavía existen menos de n movimientos.

                    /*
                     * Se crea una nueva lista con los últimos n
                     * movimientos disponibles.
                     *
                     * Si existen menos de n movimientos,
                     * devuelve todos los disponibles.
                     * history = [A, B]
                     * n = 3
                     * resultado = [A, B]
                     */
                    return new java.util.ArrayList<Action>(history.subList(inicio,history.size()));
                }


                /*
                 * Si el nombre de la función no coincide con ninguna
                 * de las funciones soportadas, se devuelve null.
                 */
                default: return null;
            }

        } catch (Exception e) {

            /*
             * Si ocurre un error durante la evaluación,
             * se captura la excepción para evitar que el programa
             * se detenga abruptamente.
             */
            
            e.printStackTrace();
            
            return null;
        }
    }
}