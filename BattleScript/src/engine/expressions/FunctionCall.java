package engine.expressions;

import battlescript.model.Action;
import engine.context.Context;
import java.util.List;

public class FunctionCall implements Expression {
    private final String name;
    private final String historyName;
    private final Expression argumentExpr; // ahora es Expression, no Object

    // Constructor para funciones con un argumento (expresión)
    public FunctionCall(String name, String historyName, Expression argumentExpr) {
        this.name = name;
        this.historyName = historyName;
        this.argumentExpr = argumentExpr;
    }

    // Constructor para last_move (sin argumento)
    public FunctionCall(String name, String historyName) {
        this(name, historyName, null);
    }

    @Override
    public Object evaluate(Context context) {
        try {
            List<Action> history = context.history(historyName);

            switch (name) {
                case "last_move":
                    if (history.isEmpty()) return null;
                    return history.get(history.size() - 1);

                case "get_move":
                    int index = (Integer) argumentExpr.evaluate(context);
                    if (index < 0 || index >= history.size()) return null;
                    return history.get(index);

                case "get_moves_count": {
                    // Evaluar la expresión que se pasó como argumento
                    Object argVal = argumentExpr.evaluate(context);
                    Action action;
                    if (argVal instanceof Action) {
                        action = (Action) argVal;
                    } else if (argVal instanceof String) {
                        action = Action.fromString((String) argVal);
                    } else {
                        throw new RuntimeException("Argumento inválido para get_moves_count: " + argVal);
                    }
                    long count = history.stream().filter(a -> a == action).count();
                    return (int) count;
                }

                case "get_last_n_moves": {
                    int n = (Integer) argumentExpr.evaluate(context);
                    if (n <= 0 || n > history.size()) return null;
                    return history.subList(history.size() - n, history.size());
                }

                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}