package engine.expressions;

import battlescript.model.Action;
import engine.context.Context;
import java.util.ArrayList;
import java.util.List;

public final class FunctionCall implements Expression {
    private final String name, history; 
    private final Object argument;
    public FunctionCall(String name, String history) { 
        this(name, history, null); }
    public FunctionCall(String name, String history, int argument) { 
        this(name, history, Integer.valueOf(argument)); }
    public FunctionCall(String name, String history, Action argument) { 
        this(name, history, (Object) argument); }
    private FunctionCall(String name, String history, Object argument) { 
        this.name = name; this.history = history; this.argument = argument; }
    
    @Override public Object evaluate(Context context) {
        List<Action> values = context.history(history);
        if ("last_move".equals(name)) { 
            if (values.isEmpty()) {
                throw new IllegalStateException("El historial está vacío");} 
            return values.get(values.size() - 1); 
        }
        if ("get_move".equals(name)) { 
            int index = (Integer) argument; 
            if (index < 0 || index >= values.size()) {
                throw new IllegalStateException("Índice de historial inválido");} 
            return values.get(index); 
        }
        if ("get_moves_count".equals(name)) { 
            int count = 0; 
            for (Action value : values){
                if (value == argument) count++;
            } 
            return count; 
        }
        if ("get_last_n_moves".equals(name)) { 
            int count = (Integer) argument; 
            if (count <= 0 || count > values.size()) {
                throw new IllegalStateException("Cantidad de movimientos inválida");
            } 
            return new ArrayList<Action>(values.subList(values.size() - count, values.size())); 
        }
        
        throw new IllegalArgumentException("Función no reconocida: " + name);
    }
}
