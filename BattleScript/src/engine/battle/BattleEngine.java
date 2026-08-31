package engine.battle;

import battlescript.model.*;
import engine.context.Context;
import engine.rules.Rule;
import java.util.*;


/**
 * Motor principal encargado de ejecutar una batalla
 *
 * Se encarga de:
 *
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
         *
         * Esto permite que el resultado de una batalla pueda
         * reproducirse utilizando la misma semilla.
         */
        Random r1 = new Random(seed);
        Random r2 = new Random(seed + 1L);

        // Obtiene la cantidad máxima de rondas de la partida.
        int rounds = match.getRounds();

        //Guarda los mensajes que describen lo ocurrido durante la batalla.
        List<String> log = new ArrayList<>();

        /*
         * Ejecuta las rondas mientras:
         *
         * - No se haya alcanzado el límite de rondas.
         * - El primer jugador siga vivo.
         * - El segundo jugador siga vivo.
         */
        for (int round = 0; round < rounds && f1.isAlive() && f2.isAlive(); round++) {
            // creacion del contexto
            Context ctx1 = buildContext(f1, f2, round, rounds, r1.nextDouble());
            Context ctx2 = buildContext(f2, f1, round, rounds, r2.nextDouble());

            // seleccion de acciones
            /*
             * Cada estrategia analiza su contexto y selecciona
             * la acción que debe realizar durante esta ronda.
             */
            Action action1 = selectAction(p1, ctx1);
            Action action2 = selectAction(p2, ctx2);

            // determinacion del orden

            /*
             * El jugador cuya acción tenga mayor prioridad
             * actúa primero.
             *
             * Si ambas acciones tienen la misma prioridad,
             * se compara la velocidad de los combatientes.
             */
            boolean firstActsFirst =
                    getPriority(action1) > getPriority(action2)
                    ||
                    (
                        getPriority(action1) == getPriority(action2)
                        && f1.getSpeed() >= f2.getSpeed()
                    );

            // ejecucion de las acciones
            if (firstActsFirst) {

                // El primer jugador ejecuta su acción.
                executeAction(f1, f2, action1, match, log);

                /*
                 * El segundo jugador solo puede actuar si
                 * continúa con vida después del ataque.
                 */
                if (f2.isAlive()) {
                    executeAction( f2, f1, action2, match, log );
                }
            } else {
                //En este caso el segundo jugador actúa primero.
                executeAction(f2, f1, action2, match, log);

                /*
                 * El primer jugador solo puede actuar si
                 * continúa con vida.
                 */
                if (f1.isAlive()) {
                    executeAction(f1, f2, action1, match, log);
                }
            }

            // reinicio de efectos temporales

            /*
             * La defensa solamente dura durante la ronda actual.
             * se desactiva al terminar la ronda.
             */
            f1.setDefending(false);
            f2.setDefending(false);

            // El bono de War Cry también es temporal.
            f1.setWarCryBonus(false);
            f2.setWarCryBonus(false);

            // comprobacion de combos

            /*
             * Después de ejecutar las acciones se revisa si alguno
             * de los jugadores completó un combo.
             */
            checkCombo(f1, match.getBonuses());

            checkCombo(f2, match.getBonuses());
        }

        // fin de la batalla

        /*
         * Una vez terminadas las rondas o cuando uno de los
         * combatientes muere, se determina el ganador.
         */
        String winner = determineWinner(f1, f2);

        /*
         * Se construye el resultado final de la batalla.
         *
         * String.join("\n", log) convierte la lista de mensajes
         * en un solo texto separado por saltos de línea.
         */
        return new BattleResult(match.getName(), winner, rounds, String.join("\n", log));
    }

    /**
     * Construye el contexto que utilizará una estrategia para tomar una decisión.
     */
    private Context buildContext(Fighter self, Fighter other, int round, int totalRounds, double random) {
        // Crea un contexto vacío.
        Context ctx = new Context();

        // DATOS DEL JUGADOR ACTUAL
        ctx.setSelfHealth(self.getHealth());
        ctx.setSelfResource(self.getResource());
        ctx.setSelfScore(self.getScore());
        ctx.setSelfHistory(self.getHistory());

        // DATOS DEL OPONENTE
        ctx.setOpponentHealth(other.getHealth());
        ctx.setOpponentResource(other.getResource());
        ctx.setOpponentScore(other.getScore());
        ctx.setOpponentHistory(other.getHistory());

        // DATOS DE LA PARTIDA
        ctx.setRoundNumber(round);
        ctx.setTotalRounds(totalRounds);

        /*
         * Guarda el número aleatorio que podrá ser utilizado
         * mediante la expresión "random" del lenguaje.
         */
        ctx.setRandom(random);
        
        return ctx;
    }
    
    /**
     * Selecciona la acción que debe ejecutar una estrategia.
     *
     * Las reglas se recorren en el mismo orden en que fueron
     * declaradas dentro del archivo
     *
     * Cada regla es responsable de determinar si puede
     * seleccionar una acción.
     */
    private Action selectAction(Strategy strategy, Context ctx) {

        // Recorre las reglas en el mismo orden en que fueron
        // definidas en el archivo BattleScript.
        for (Rule rule : strategy.getRules()) {

            // Cada regla se encarga de evaluar su propia condición
            // y devuelve una acción únicamente cuando corresponde.
            Action action = rule.select(ctx);

            // Si la regla seleccionó una acción, se utiliza inmediatamente.
            if (action != null) {
                return action;
            }
        }

        // Si ninguna regla produjo una acción, se utiliza
        // la acción inicial de la estrategia.
        return strategy.getInitialAction();
    }


    /**
     * Obtiene la prioridad de una acción.
     *
     * La prioridad se utiliza para determinar qué jugador
     * ejecutará primero su acción.
     */
    private int getPriority(Action action) {

        return action.getPriority();
    }

    //Ejecuta una acción realizada por un combatiente.
    private void executeAction( Fighter self, Fighter other, Action action, Match match, List<String> log) {
        
        // Obtiene la configuración de puntuación de la partida.
        Scoring scoring = match.getScoring();

        // VERIFICACIÓN DE RECURSOS
        /*
         * Antes de ejecutar una acción se comprueba si el jugador
         * tiene suficiente recurso para pagar su costo.
         */
        if (!self.canPay(action.getResourceCost())) {
            /*
             * Si no puede pagar, recibe la penalización
             * definida por la partida.
             */
            self.addScore(-scoring.getFailedActionPenalty()
            );

            /*
             * Registra en el historial de la batalla que
             * la acción no pudo realizarse.
             */
            log.add(self.getStrategy().getName() + " no tiene recursos para " + action);

            //No se continúa ejecutando la acción.
            return;
        }

        //Si tiene suficiente recurso, se descuenta el costo de la acción.
        self.payResource(action.getResourceCost());

        // ACCIÓN OFENSIVA
        if (action.isOffensive()) {

            // Calcula el daño que producirá la acción.
            int damage = calculateDamage(self, other, action);

            //Si el oponente está defendiendo, el daño se reduce a la mitad.
            if (other.isDefending()) {
                damage = (int) Math.floor(damage * 0.5);
                
                //Se otorgan los puntos correspondientes a una defensa exitosa.
                other.addScore(scoring.getSuccessfulDefense());
            }

            //Aplica el daño al oponente.
            other.applyDamage(damage);

            //El atacante recibe puntos según el daño causado.
            self.addScore(damage * scoring.getDamagePoint());

            //Registra la acción en el log.
            log.add(
                    self.getStrategy().getName()
                    + " usa "
                    + action
                    + " causando "
                    + damage
                    + " daño"
            );

        // ACCIÓN DEFENSIVA
        } else if (action.isDefense()) {
            //Activa el estado de defensa del jugador.
            self.setDefending(true);

            log.add(self.getStrategy().getName() + " se defiende");

        // ACCIÓN DE CURACIÓN
        } else if (action.isHealing()) {

            /*
             * Calcula cuánto puede curarse el jugador.
             *
             * Math.min evita que la vida supere el máximo permitido.
             */
            int healed = Math.min(action.getPower(), self.getMaxHealth() - self.getHealth());

            // Aplica la curación.
            self.heal(healed);
            
            //Otorga puntos según la cantidad de vida recuperada.
            self.addScore(healed * scoring.getHealingPoint());
            
            log.add(self.getStrategy().getName() + " se cura " + healed + " HP");

        // RECUPERACIÓN DE RECURSOS
        } else if (action.isRecovery()) {

            // Recupera la cantidad de recurso indicada por el poder de la acción.
            self.recoverResource(action.getPower());
            
            log.add(
                    self.getStrategy().getName()
                    + " recupera "
                    + action.getPower()
                    + " de recurso"
            );

        // WAR CRY
        } else if (action == Action.WAR_CRY) {
            
            /*
             * Activa el bono adicional de ataque.
             *
             * El bono se utilizará durante la acción ofensiva
             * correspondiente y será eliminado al terminar la ronda.
             */
            self.setWarCryBonus(true);

            log.add(self.getStrategy().getName() + " usa Grito de Guerra");
        }
        
        // Guarda la acción ejecutada en el historial del jugador.
        self.addToHistory(action);
        
        // BONO POR VICTORIA
        //Después de ejecutar la acción se comprueba si el oponente murió.
        if (!other.isAlive()) {
            //Si murió, se otorga el bono de victoria.
            self.addScore(scoring.getVictoryBonus());

            log.add(
                    self.getStrategy().getName()
                    + " ha derrotado a "
                    + other.getStrategy().getName()
            );
        }
    }

    /**
     * Calcula el daño producido por una acción ofensiva.
     *
     * Para un mago:
     * daño = poder de acción + poder mágico - resistencia mágica + bono de War Cry
     *
     * Para un guerrero:
     * daño = poder de acción + ataque físico - armadura + bono de War Cry
     */
    private int calculateDamage(Fighter attacker, Fighter other, Action action) 
    {
        // Poder base de la acción.
        int base = action.getPower();

        //Inicialmente no existe bono adicional de ataque.
        int attackBonus = 0;

        //Si el jugador tiene activo War Cry, recibe 10 puntos adicionales de ataque.
        if (attacker.hasWarCryBonus()) { attackBonus = 10; }

        //Los magos utilizan estadísticas mágicas.
        if (attacker.getStrategy().getClassType() == ClassType.MAGE ) {
            int damage =
                    base
                    + attacker.getMagicPower()
                    - other.getMagicResistance()
                    + attackBonus;

            //Daño minimo 1
            return Math.max(1, damage);
            
        } else {

            //Los guerreros utilizan estadísticas físicas.
            int damage =
                    base
                    + attacker.getPhysicalAttack()
                    - other.getArmor()
                    + attackBonus;

            return Math.max(1, damage);
        }
    }

    /**
     * Comprueba si el jugador completó alguno de los combos definidos en la partida.
     *
     * Para comprobar un combo se toman los últimos tres movimientos
     * realizados por el jugador.
     */
    private void checkCombo(Fighter fighter, Bonuses bonuses) {

        //Obtiene el historial completo de movimientos.
        List<Action> history = fighter.getHistory();

        //Si todavía no existen tres movimientos, no puede existir un combo.
        if (history.size() < 3) { return; }

        //Obtiene únicamente los últimos tres movimientos.
        List<Action> lastThree =
                history.subList(history.size() - 3, history.size());

        // Obtiene el tipo de personaje.
        ClassType type = fighter.getStrategy().getClassType();

        // COMBO DEL MAGO
        if (type == ClassType.MAGE && bonuses.getMageCombo() != null) {

            //Compara los últimos tres movimientos con el combo definido para el mago.
            if (lastThree.equals(bonuses.getMageCombo())) 
            {
                // Otorga los puntos del combo.
                fighter.addScore(bonuses.getMageComboPoints());
            }

        // COMBO DEL GUERRERO
        } else if (type == ClassType.WARRIOR && bonuses.getWarriorCombo() != null) 
        {
            //Compara los últimos tres movimientos con el combo definido para el guerrero.
            if (lastThree.equals(bonuses.getWarriorCombo())) 
            {
                // Otorga los puntos del combo.
                fighter.addScore(bonuses.getWarriorComboPoints());
            }
        }
    }

    /**
     * Determina el ganador de la batalla.
     * Criterios:
     * 1. Si uno de los jugadores murió.
     * 2. Mayor puntuación.
     * 3. Mayor cantidad de vida.
     * 4. Mayor cantidad de recurso.
     * 5. Empate.
     */
    private String determineWinner(Fighter f1, Fighter f2) {

        // MUERTE DE LOS JUGADORES
        //Si ambos mueren, la batalla termina en empate por muerte simultánea.
        if (!f1.isAlive() && !f2.isAlive()) {
            return "Empate por muerte simultánea";
        }

        //Si solamente f1 murió, gana f2.
        if (!f1.isAlive()) { return f2.getStrategy().getName(); }
        //Si solamente f2 murió, gana f1.
        if (!f2.isAlive()) { return f1.getStrategy().getName(); }

        // DESEMPATE POR PUNTUACIÓN
        if (f1.getScore() > f2.getScore()) { return f1.getStrategy().getName(); }
        if (f2.getScore() > f1.getScore()) { return f2.getStrategy().getName(); }

        // DESEMPATE POR VIDA
        if (f1.getHealth() > f2.getHealth()) { return f1.getStrategy().getName(); }
        if (f2.getHealth() > f1.getHealth()) { return f2.getStrategy().getName(); }
        
        // DESEMPATE POR RECURSO
        if (f1.getResource() > f2.getResource()) { return f1.getStrategy().getName(); }
        if (f2.getResource() > f1.getResource()) { return f2.getStrategy().getName(); }
        
        // Si todos los criterios son iguales, termina en empate.
        return "Empate";
    }
}