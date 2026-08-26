package engine.expressions;

import engine.context.Context;

public final class VariableExpression implements Expression {
    private final String name;
    public VariableExpression(String name) { this.name = name; }
    @Override public Object evaluate(Context c) {
        if ("self_health".equals(name)) return c.getSelfHealth();
        if ("opponent_health".equals(name)) return c.getOpponentHealth();
        if ("self_resource".equals(name)) return c.getSelfResource();
        if ("opponent_resource".equals(name)) return c.getOpponentResource();
        if ("self_score".equals(name)) return c.getSelfScore();
        if ("opponent_score".equals(name)) return c.getOpponentScore();
        if ("round_number".equals(name)) return c.getRoundNumber();
        if ("total_rounds".equals(name)) return c.getTotalRounds();
        if ("random".equals(name)) return c.getRandom();
        throw new IllegalArgumentException("Variable no reconocida: " + name);
    }
}
