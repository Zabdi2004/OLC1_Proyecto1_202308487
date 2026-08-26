package engine.expressions;

import engine.context.Context;
import java.util.Objects;

public final class BinaryExpression implements Expression {
    private final Expression left, right; 
    private final String operator;
    public BinaryExpression(Expression left, String operator, Expression right) {
        this.left = left; this.operator = operator; this.right = right; 
    }
    
    @Override public Object evaluate(Context context) {
        Object a = left.evaluate(context);
        if ("&&".equals(operator)) return asBoolean(a) && asBoolean(right.evaluate(context));
        if ("||".equals(operator)) return asBoolean(a) || asBoolean(right.evaluate(context));
        Object b = right.evaluate(context);
        if ("==".equals(operator)) return Objects.equals(a, b);
        if ("!=".equals(operator)) return !Objects.equals(a, b);
        if (a instanceof Number && b instanceof Number) {
            double x = ((Number) a).doubleValue(), y = ((Number) b).doubleValue();
            if ("<".equals(operator)) return x < y; 
            if (">".equals(operator)) return x > y;
            if ("<=".equals(operator)) return x <= y; 
            if (">=".equals(operator)) return x >= y;
        }
        throw new IllegalArgumentException("Comparación inválida: " + operator);
    }
    private static boolean asBoolean(Object value) {
        if (!(value instanceof Boolean)){
            throw new IllegalArgumentException("Se esperaba una condición booleana");
        }
        return (Boolean) value;
    }
}
