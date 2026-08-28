package engine.expressions;

import battlescript.model.Action;
import engine.context.Context;
import java.util.List;

public class FunctionCall implements Expression {
    private final String name;
    private final String historyName;
    private final Object argument; // puede ser Integer o Action o null

    // Constructor para funciones con argumento (get_move, get_moves_count, get_last_n_moves)
    public FunctionCall(String name, String historyName, Object argument) {
        this.name = name;
        this.historyName = historyName;
        this.argument = argument;
    }

    // Constructor para last_move (sin argumento)
    public FunctionCall(String name, String historyName) {
        this(name, historyName, null);
    }

    @Override
    public Object evaluate(Context context) {
        List<Action> history = context.history(historyName);

        switch (name) {
            case "last_move":
                if (history.isEmpty()) {
                    throw new RuntimeException("Historial vacío en last_move");
                }
                return history.get(history.size() - 1);

            case "get_move":
                int index = (Integer) argument;
                if (index < 0 || index >= history.size()) {
                    throw new RuntimeException("Índice fuera de rango en get_move");
                }
                return history.get(index);

            case "get_moves_count":
                Action action = (Action) argument;
                long count = history.stream().filter(a -> a == action).count();
                return (int) count;

            case "get_last_n_moves":
                int n = (Integer) argument;
                if (n <= 0 || n > history.size()) {
                    throw new RuntimeException("n inválido en get_last_n_moves");
                }
                return history.subList(history.size() - n, history.size());

            default:
                throw new RuntimeException("Función desconocida: " + name);
        }
    }
}