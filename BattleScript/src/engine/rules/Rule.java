package engine.rules;

import battlescript.model.Action;
import engine.context.Context;

/**
 * Representa una regla de decisión
 *
 * Una regla recibe el estado actual de la batalla mediante
 * un Context y devuelve una acción.
 *
 * Las principales implementaciones son:
 * - IfRule: evalúa una condición antes de seleccionar una acción.
 * - ElseRule: representa la acción alternativa.
 */
public interface Rule { Action select(Context context); }