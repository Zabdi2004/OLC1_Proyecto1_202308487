package engine.battle;

import battlescript.model.*;
import engine.context.Context;
import engine.rules.Rule;
import java.util.*;

/**
 * Motor principal encargado de ejecutar una batalla.
 *
 * Se encarga de:
 * - Crear los combatientes.
 * - Ejecutar las rondas.
 * - Crear el contexto de cada jugador.
 * - Seleccionar las acciones mediante las reglas.
 * - Determinar el orden de ejecución.
 * - Ejecutar las acciones.
 * - Calcular daño, curaciones y puntuaciones.
 * - Verificar combos.
 * - Determinar al ganador.
 */
public class BattleEngine {

    // Ejecuta una partida completa
    public BattleResult run(Match match, Strategy p1, Strategy p2, int seed) {

        // Se crean los dos combatientes utilizando las estrategias recibidas.
        Fighter f1 = new Fighter(p1);
        Fighter f2 = new Fighter(p2);

        /*
         * Cada jugador tiene su propio generador de números aleatorios.
         *
         * Se utiliza la semilla original para el primer jugador
         * y seed + 1 para el segundo.
         */
        Random r1 = new Random(seed);
        Random r2 = new Random(seed + 1L);

        // Obtiene la cantidad máxima de rondas.
        int rounds = match.getRounds();

        // Guarda los mensajes de la batalla.
        List<String> log = new ArrayList<>();

        // Encabezado de la partida.
        log.add("");
        log.add("Partida " + match.getName() + " (seed " + seed + ")");
        log.add("");

        /*
         * Ejecuta las rondas mientras:
         *
         * - No se haya alcanzado el límite de rondas.
         * - Ambos combatientes sigan vivos.
         */
        for (int round = 0;
                round < rounds && f1.isAlive() && f2.isAlive();
                round++) {

            log.add("Ronda " + round + ":");

            /*
             * La ronda 0 no utiliza random.
             *
             * A partir de la ronda 1 se genera exactamente
             * un valor para cada jugador.
             */
            double random1 = 0;
            double random2 = 0;

            if (round == 0) {

                log.add("  Random: no aplica en la ronda inicial.");

            } else {

                random1 = r1.nextDouble();
                random2 = r2.nextDouble();

                log.add(
                        "  Random: "
                        + p1.getName()
                        + "="
                        + random1
                        + ", "
                        + p2.getName()
                        + "="
                        + random2
                );
            }

            // Creación de los contextos.
            Context ctx1 = buildContext(
                    f1,
                    f2,
                    round,
                    rounds,
                    random1
            );

            Context ctx2 = buildContext(
                    f2,
                    f1,
                    round,
                    rounds,
                    random2
            );

            /*
             * Selección de acciones.
             *
             * En la ronda 0 se utiliza directamente initial.
             * Desde la ronda 1 se evalúan las reglas.
             */
            Action action1;
            Action action2;

            if (round == 0) {

                action1 = p1.getInitialAction();
                action2 = p2.getInitialAction();

            } else {

                action1 = selectAction(p1, ctx1);
                action2 = selectAction(p2, ctx2);
            }

            /*
             * Determinación del orden de ejecución.
             *
             * Primero se compara la prioridad de la acción.
             * En caso de empate se compara la velocidad.
             */
            boolean firstActsFirst =
                    getPriority(action1) > getPriority(action2)
                    ||
                    (
                        getPriority(action1) == getPriority(action2)
                        && f1.getSpeed() >= f2.getSpeed()
                    );

            /*
             * Ejecución de las acciones.
             *
             * executeAction devuelve una descripción detallada
             * de lo que ocurrió.
             */
            String result1;
            String result2 = null;

            if (firstActsFirst) {

                result1 = executeAction(
                        f1,
                        f2,
                        action1,
                        match
                );

                if (f2.isAlive()) {

                    result2 = executeAction(
                            f2,
                            f1,
                            action2,
                            match
                    );
                }

            } else {

                result2 = executeAction(
                        f2,
                        f1,
                        action2,
                        match
                );

                if (f1.isAlive()) {

                    result1 = executeAction(
                            f1,
                            f2,
                            action1,
                            match
                    );

                } else {

                    result1 = "Acción no ejecutada porque "
                            + f1.getStrategy().getName()
                            + " fue derrotado.";
                }
            }

            /*
             * Agrega al log las acciones en el orden en que fueron
             * seleccionadas originalmente.
             *
             * Si queremos representar estrictamente el orden de ejecución,
             * los resultados se colocan según quién actuó primero.
             */
            if (firstActsFirst) {

                log.add("  " + result1);

                if (result2 != null) {
                    log.add("  " + result2);
                }

            } else {

                if (result2 != null) {
                    log.add("  " + result2);
                }

                log.add("  " + result1);
            }

            // Reinicio de efectos temporales.

            // La defensa solamente dura durante la ronda actual.
            f1.setDefending(false);
            f2.setDefending(false);

            // Comprobación de combos.
            checkCombo(f1, match.getBonuses());
            checkCombo(f2, match.getBonuses());

            /*
             * Estado de ambos jugadores al terminar la ronda.
             */
            log.add("  Estado:");
            log.add("    " + formatFighterState(f1));
            log.add("    " + formatFighterState(f2));
            log.add("");
        }

        // Fin de la batalla.

        String winner = determineWinner(f1, f2);
        
        applyLowHealthVictoryBonus(
                f1,
                f2,
                winner,
                match.getBonuses()
        );
        
        log.add("Ganador: " + winner);

        /*
         * Se construye el resultado final.
         */
        return new BattleResult(
                match.getName(),
                winner,
                rounds,
                String.join("\n", log),

                f1.getStrategy().getName(),
                f1.getStrategy().getClassType().toString(),
                f1.getHealth(),
                f1.getResource(),
                f1.getScore(),

                f2.getStrategy().getName(),
                f2.getStrategy().getClassType().toString(),
                f2.getHealth(),
                f2.getResource(),
                f2.getScore()
        );
    }

    /**
     * Construye el contexto que utilizará una estrategia
     * para tomar una decisión.
     */
    private Context buildContext(
            Fighter self,
            Fighter other,
            int round,
            int totalRounds,
            double random
    ) {

        Context ctx = new Context();

        // Datos del jugador actual.
        ctx.setSelfHealth(self.getHealth());
        ctx.setSelfResource(self.getResource());
        ctx.setSelfScore(self.getScore());
        ctx.setSelfHistory(self.getHistory());

        // Datos del oponente.
        ctx.setOpponentHealth(other.getHealth());
        ctx.setOpponentResource(other.getResource());
        ctx.setOpponentScore(other.getScore());
        ctx.setOpponentHistory(other.getHistory());

        // Datos de la partida.
        ctx.setRoundNumber(round);
        ctx.setTotalRounds(totalRounds);

        // Valor de random.
        ctx.setRandom(random);

        return ctx;
    }

    /**
     * Selecciona la acción que debe ejecutar una estrategia.
     *
     * Las reglas se recorren en el mismo orden en que fueron
     * declaradas dentro del archivo.
     */
    private Action selectAction(Strategy strategy, Context ctx) {
        for (Rule rule : strategy.getRules()) {

            Action action = rule.select(ctx);

            if (action != null) {
                return action;
            }
        }
        return strategy.getInitialAction();
    }

    /**
     * Obtiene la prioridad de una acción.
     */
    private int getPriority(Action action) {
        return action.getPriority();
    }

    /**
     * Ejecuta una acción y devuelve una descripción detallada
     * del resultado.
     */
    private String executeAction(
            Fighter self,
            Fighter other,
            Action action,
            Match match
    ) {

        Scoring scoring = match.getScoring();

        /*
         * Guardamos los valores iniciales para poder determinar
         * cuánto daño, curación y recurso produjo la acción.
         */
        int healthBeforeSelf = self.getHealth();
        int healthBeforeOther = other.getHealth();
        int resourceBefore = self.getResource();

        /*
         * VERIFICACIÓN DE RECURSOS
         */
        if (!self.canPay(action.getResourceCost())) {

            self.addScore(
                    -scoring.getFailedActionPenalty()
            );

            return self.getStrategy().getName()
                    + ": "
                    + action
                    + " → Fallida | Recurso insuficiente";
        }

        // Se descuenta el costo.
        self.payResource(action.getResourceCost());

        /*
         * ACCIÓN OFENSIVA
         */
        if (action.isOffensive()) {

            int damage = calculateDamage(
                    self,
                    other,
                    action
            );

            if (self.hasWarCryBonus()) {
                self.setWarCryBonus(false);
            }

            /*
             * Si el oponente está defendiendo,
             * el daño se reduce a la mitad.
             */
            if (other.isDefending()) {

                damage = (int) Math.floor(damage * 0.5);

                other.addScore(
                        scoring.getSuccessfulDefense()
                );
            }

            other.applyDamage(damage);

            self.addScore(
                    damage * scoring.getDamagePoint()
            );

            self.addToHistory(action);

            /*
             * Bono por victoria.
             */
            if (!other.isAlive()) {

                self.addScore(
                        scoring.getVictoryBonus()
                );
            }

            return self.getStrategy().getName()
                    + ": "
                    + action
                    + " → Ejecutada | Daño: "
                    + damage
                    + " | Curación: 0"
                    + " | Recurso: 0";
        }

        /*
         * ACCIÓN DEFENSIVA
         */
        else if (action.isDefense()) {

            self.setDefending(true);

            self.addToHistory(action);

            return self.getStrategy().getName()
                    + ": "
                    + action
                    + " → Ejecutada | Daño: 0"
                    + " | Curación: 0"
                    + " | Recurso: 0";
        }

        /*
         * ACCIÓN DE CURACIÓN
         */
        else if (action.isHealing()) {

            int healed = Math.min(
                    action.getPower(),
                    self.getMaxHealth() - self.getHealth()
            );

            self.heal(healed);

            self.addScore(
                    healed * scoring.getHealingPoint()
            );

            self.addToHistory(action);

            return self.getStrategy().getName()
                    + ": "
                    + action
                    + " → Ejecutada | Daño: 0"
                    + " | Curación: "
                    + healed
                    + " | Recurso: 0";
        }

        /*
         * RECUPERACIÓN DE RECURSOS
         */
        else if (action.isRecovery()) {

            int resourceBeforeRecovery = self.getResource();

            self.recoverResource(action.getPower());

            int recovered =
                    self.getResource() - resourceBeforeRecovery;

            self.addToHistory(action);

            return self.getStrategy().getName()
                    + ": "
                    + action
                    + " → Ejecutada | Daño: 0"
                    + " | Curación: 0"
                    + " | Recurso: "
                    + recovered;
        }

        /*
         * WAR CRY
         */
        else if (action == Action.WAR_CRY) {

            self.setWarCryBonus(true);

            self.addToHistory(action);

            return self.getStrategy().getName()
                    + ": "
                    + action
                    + " → Ejecutada | Daño: 0"
                    + " | Curación: 0"
                    + " | Recurso: 0";
        }

        /*
         * Por seguridad, si aparece una acción no contemplada.
         */
        self.addToHistory(action);
        
        return self.getStrategy().getName()
                + ": "
                + action
                + " → Ejecutada | Daño: 0"
                + " | Curación: 0"
                + " | Recurso: 0";
    }

    /**
     * Formatea el estado actual de un combatiente.
     */
    private String formatFighterState(Fighter fighter) {

        return fighter.getStrategy().getName()
                + " → Vida: "
                + fighter.getHealth()
                + " | Recurso: "
                + fighter.getResource()
                + " | Puntos: "
                + fighter.getScore();
    }

    /**
     * Calcula el daño producido por una acción ofensiva.
     *
     * Para un mago:
     * daño = poder de acción + poder mágico
     *         - resistencia mágica + bono de War Cry
     *
     * Para un guerrero:
     * daño = poder de acción + ataque físico
     *         - armadura + bono de War Cry
     */
    private int calculateDamage(
            Fighter attacker,
            Fighter other,
            Action action
    ) {

        int base = action.getPower();

        int attackBonus = 0;

        if (attacker.hasWarCryBonus()) {
            attackBonus = 10;
        }

        // Mago.
        if (attacker.getStrategy().getClassType() == ClassType.MAGE) {

            int damage =
                    base
                    + attacker.getMagicPower()
                    - other.getMagicResistance()
                    + attackBonus;

            return Math.max(1, damage);

        } else {

            // Guerrero.
            int damage =
                    base
                    + attacker.getPhysicalAttack()
                    - other.getArmor()
                    + attackBonus;

            return Math.max(1, damage);
        }
    }

    /**
     * Comprueba si el jugador completó alguno de los combos
     * definidos en la partida.
     */
    private void checkCombo( Fighter fighter, Bonuses bonuses ) {

        // Si la partida no tiene bonos, no hay nada que comprobar.
        if (bonuses == null) {
            return;
        }

        List<Action> history = fighter.getHistory();

        // Se necesitan al menos 3 movimientos para formar un combo.
        if (history.size() < 3) {
            return;
        }

        // Obtiene los últimos 3 movimientos realizados.
        List<Action> lastThree = history.subList(
                history.size() - 3,
                history.size()
        );

        ClassType type = fighter.getStrategy().getClassType();

        // Combo del mago.
        if (type == ClassType.MAGE) {

            List<Action> combo = bonuses.getMageCombo();

            if (combo != null
                    && !combo.isEmpty()
                    && lastThree.equals(combo)) {

                Action finalAction = lastThree.get(lastThree.size() - 1);

                // El mismo movimiento final no puede otorgar
                // nuevamente el mismo bonus.
                if (!fighter.comboAlreadyAwarded(finalAction)) {

                    fighter.addScore(
                            bonuses.getMageComboPoints()
                    );

                    fighter.registerComboAward(finalAction);
                }
            }

        // Combo del guerrero.
        } else if (type == ClassType.WARRIOR) {

            List<Action> combo = bonuses.getWarriorCombo();

            if (combo != null
                    && !combo.isEmpty()
                    && lastThree.equals(combo)) {

                Action finalAction = lastThree.get(lastThree.size() - 1);

                // El mismo movimiento final no puede otorgar
                // nuevamente el mismo bonus.
                if (!fighter.comboAlreadyAwarded(finalAction)) {

                    fighter.addScore(
                            bonuses.getWarriorComboPoints()
                    );

                    fighter.registerComboAward(finalAction);
                }
            }
        }
    }

    /**
     * Determina el ganador de la batalla.
     *
     * Criterios:
     * 1. Si uno de los jugadores murió.
     * 2. Mayor puntuación.
     * 3. Mayor cantidad de vida.
     * 4. Mayor cantidad de recurso.
     * 5. Empate.
     */
    
    /**
    * Aplica el bono por victoria con poca vida.
    *
    * El bono se otorga al ganador si termina la batalla
    * con 25% o menos de su vida máxima.
    */
    private void applyLowHealthVictoryBonus(Fighter f1, Fighter f2, String winner, Bonuses bonuses) {
        
        if (bonuses == null) {
            return;
        }
        
        int bonus = bonuses.getLowHealthVictory();

        if (bonus <= 0) {
            return;
        }

        if (winner.equals(f1.getStrategy().getName())
                && f1.getHealth() <= f1.getMaxHealth() * 0.25) {
            f1.addScore(bonus);
        
        } else if (winner.equals(f2.getStrategy().getName())
                && f2.getHealth() <= f2.getMaxHealth() * 0.25) {
            f2.addScore(bonus);
        }
    }
    
    private String determineWinner(
            Fighter f1,
            Fighter f2
    ) {

        // Muerte de ambos.
        if (!f1.isAlive() && !f2.isAlive()) {
            return "Empate por muerte simultánea";
        }

        // Muerte de f1.
        if (!f1.isAlive()) {
            return f2.getStrategy().getName();
        }

        // Muerte de f2.
        if (!f2.isAlive()) {
            return f1.getStrategy().getName();
        }

        // Desempate por puntuación.
        if (f1.getScore() > f2.getScore()) {
            return f1.getStrategy().getName();
        }

        if (f2.getScore() > f1.getScore()) {
            return f2.getStrategy().getName();
        }

        // Desempate por vida.
        if (f1.getHealth() > f2.getHealth()) {
            return f1.getStrategy().getName();
        }

        if (f2.getHealth() > f1.getHealth()) {
            return f2.getStrategy().getName();
        }

        // Desempate por recurso.
        if (f1.getResource() > f2.getResource()) {
            return f1.getStrategy().getName();
        }

        if (f2.getResource() > f1.getResource()) {
            return f2.getStrategy().getName();
        }

        // Empate.
        return "Empate";
    }
}