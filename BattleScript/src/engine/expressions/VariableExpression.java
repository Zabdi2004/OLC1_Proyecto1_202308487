package engine.expressions;

import engine.context.Context;

/**
 * Representa una variable propia del lenguaje
 *
 * Las variables no almacenan directamente su valor.
 * En lugar de eso, consultan el Context cuando son evaluadas,
 * porque su valor puede cambiar durante la batalla.
*/

public final class VariableExpression implements Expression {
    //Nombre de la variable definida
    private final String name;
    
    //Crea una expresión que representa una variable
    public VariableExpression(String name) { this.name = name; }
    
    /**
     * Obtiene el valor actual de la variable desde el Context.
     * Esto permite que una misma expresión pueda producir
     * diferentes valores en diferentes rondas.
     */
    @Override public Object evaluate(Context c) {
        // Vida actual del personaje que está ejecutando la estrategia.
        if ("self_health".equals(name)) return c.getSelfHealth();
        
        // Vida actual del oponente.
        if ("opponent_health".equals(name)) return c.getOpponentHealth();
        
        // Recurso actual del personaje.
        if ("self_resource".equals(name)) return c.getSelfResource();
        
        // Recurso actual del oponente.
        if ("opponent_resource".equals(name)) return c.getOpponentResource();
        
        // Puntuación actual del personaje
        if ("self_score".equals(name)) return c.getSelfScore();
        
        // Puntuación actual del oponente.
        if ("opponent_score".equals(name)) return c.getOpponentScore();
        
        // Número de ronda actual.
        if ("round_number".equals(name)) return c.getRoundNumber();
        
        // Número total de rondas de la partida.
        if ("total_rounds".equals(name)) return c.getTotalRounds();
        
        // Valor aleatorio generado para la ejecución.
        if ("random".equals(name)) return c.getRandom();
        throw new IllegalArgumentException("Variable no reconocida: " + name);
    }
}
