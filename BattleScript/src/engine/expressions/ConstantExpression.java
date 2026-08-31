package engine.expressions;

import engine.context.Context;

//Representa una expresión constante.
public final class ConstantExpression implements Expression {
    //Una constante es un valor que no depende del estado de la batalla.
    private final Object value;
    
    //crea una expresión constante
    public ConstantExpression(Object value) { this.value = value; }
    //Devuelve el valor almacenado
    @Override public Object evaluate(Context context) { return value; }
}
