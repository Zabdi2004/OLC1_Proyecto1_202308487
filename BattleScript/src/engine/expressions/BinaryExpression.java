package engine.expressions;

import engine.context.Context;
import java.util.Objects;

//Representa una expresión binaria la cual combina dos expresiones utilizando un operador.
public final class BinaryExpression implements Expression {
    
    // Expresión que se encuentra a la izquierda|derecha del operador.
    private final Expression left, right; 
    
    // Operador que indica qué operación debe realizarse.
    private final String operator;
    
    //Construye una expresión binaria.
    public BinaryExpression(Expression left, String operator, Expression right) {
        this.left = left; this.operator = operator; this.right = right; 
    }
    
    //Evalúa ambos lados y aplica el operador correspondiente.
    @Override public Object evaluate(Context context) {
        
        // Primero se obtiene el valor de la expresión izquierda.
        Object a = left.evaluate(context);
        // Ambas expresiones deben producir valores booleanos.
        if ("&&".equals(operator)) return asBoolean(a) && asBoolean(right.evaluate(context)); //AND
        if ("||".equals(operator)) return asBoolean(a) || asBoolean(right.evaluate(context)); //OR
        
        // Se evalúa el lado derecho
        Object b = right.evaluate(context);
        if ("==".equals(operator)) return Objects.equals(a, b); //Igualdad
        if ("!=".equals(operator)) return !Objects.equals(a, b); //Desigualdad
        if (a instanceof Number && b instanceof Number) {
            double x = ((Number) a).doubleValue(), y = ((Number) b).doubleValue();
            if ("<".equals(operator)) return x < y; 
            if (">".equals(operator)) return x > y;
            if ("<=".equals(operator)) return x <= y; 
            if (">=".equals(operator)) return x >= y;
        }
        throw new IllegalArgumentException("Comparación inválida: " + operator);
    }
    
    //Se convierte de Object a Boolean para comprobar que los operadores AND y OR
    //sean condiciones booleanas
    private static boolean asBoolean(Object value) {
        if (!(value instanceof Boolean)){
            throw new IllegalArgumentException("Se esperaba una condición booleana");
        }
        return (Boolean) value;
    }
}
