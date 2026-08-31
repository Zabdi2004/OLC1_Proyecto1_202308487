package engine.expressions;

import engine.context.Context;

//Una expresion unaria utiliza un único operador como "!"
public final class UnaryExpression implements Expression {
    
    // Operador unario
    private final String operator; 
    
    // Expresión sobre la que se aplicará el operador.
    private final Expression operand;
    
    //Construye la expresión unaria
    public UnaryExpression(String operator, Expression operand) { 
        this.operator = operator; this.operand = operand; }
    
    //Evalúa el operando y aplica el operador correspondiente.
    @Override public Object evaluate(Context context) {
        //// Primero se obtiene el resultado de la expresión interna.
        Object value = operand.evaluate(context);
        
        // El operador ! invierte un valor booleano.
        if ("!".equals(operator) && value instanceof Boolean){
            return !((Boolean) value);}
        throw new IllegalArgumentException("Operación unaria inválida: " + operator);
    }
}
