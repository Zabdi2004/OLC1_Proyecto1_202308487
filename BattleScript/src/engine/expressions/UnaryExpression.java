package engine.expressions;

import engine.context.Context;

public final class UnaryExpression implements Expression {
    private final String operator; 
    private final Expression operand;
    public UnaryExpression(String operator, Expression operand) { 
        this.operator = operator; this.operand = operand; }
    
    @Override public Object evaluate(Context context) {
        Object value = operand.evaluate(context);
        if ("!".equals(operator) && value instanceof Boolean){
            return !((Boolean) value);}
        throw new IllegalArgumentException("Operación unaria inválida: " + operator);
    }
}
