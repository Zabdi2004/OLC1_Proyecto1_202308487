package engine.battle;

import battlescript.model.*;
import engine.context.Context;
import engine.expressions.Expression;
import engine.rules.ElseRule;
import engine.rules.IfRule;
import engine.rules.Rule;

import java.util.*;

public class BattleEngine {

    public BattleResult run(Match match, Strategy p1, Strategy p2, int seed) {
        Fighter f1 = new Fighter(p1);
        Fighter f2 = new Fighter(p2);

        Random r1 = new Random(seed);
        Random r2 = new Random(seed + 1L);

        int rounds = match.getRounds();
        List<String> log = new ArrayList<>();

        for (int round = 0; round < rounds && f1.isAlive() && f2.isAlive(); round++) {
            // Crear contexto
            Context ctx1 = buildContext(f1, f2, round, rounds, r1.nextDouble());
            Context ctx2 = buildContext(f2, f1, round, rounds, r2.nextDouble());

            // Seleccionar acciones
            Action action1 = selectAction(p1, ctx1);
            Action action2 = selectAction(p2, ctx2);

            // Determinar orden
            boolean firstActsFirst = getPriority(action1) > getPriority(action2) ||
                    (getPriority(action1) == getPriority(action2) && f1.getSpeed() >= f2.getSpeed());

            // Ejecutar en orden
            if (firstActsFirst) {
                executeAction(f1, f2, action1, match, log);
                if (f2.isAlive()) {
                    executeAction(f2, f1, action2, match, log);
                }
            } else {
                executeAction(f2, f1, action2, match, log);
                if (f1.isAlive()) {
                    executeAction(f1, f2, action1, match, log);
                }
            }

            // Resetear defensas y war cry
            f1.setDefending(false);
            f2.setDefending(false);
            f1.setWarCryBonus(false);
            f2.setWarCryBonus(false);

            // Verificar combos
            checkCombo(f1, match.getBonuses());
            checkCombo(f2, match.getBonuses());
        }

        String winner = determineWinner(f1, f2);
        return new BattleResult(match.getName(), winner, rounds, String.join("\n", log));
    }

    private Context buildContext(Fighter self, Fighter other, int round, int totalRounds, double random) {
        Context ctx = new Context();
        ctx.setSelfHealth(self.getHealth());
        ctx.setOpponentHealth(other.getHealth());
        ctx.setSelfResource(self.getResource());
        ctx.setOpponentResource(other.getResource());
        ctx.setSelfScore(self.getScore());
        ctx.setOpponentScore(other.getScore());
        ctx.setRoundNumber(round);
        ctx.setTotalRounds(totalRounds);
        ctx.setRandom(random);
        ctx.setSelfHistory(self.getHistory());
        ctx.setOpponentHistory(other.getHistory());
        return ctx;
    }

    private Action selectAction(Strategy strategy, Context ctx) {
        for (Rule rule : strategy.getRules()) {
            if (rule instanceof IfRule) {
                IfRule ifRule = (IfRule) rule;
                Expression cond = ifRule.getCondition();
                if (cond != null && Boolean.TRUE.equals(cond.evaluate(ctx))) {
                    return ifRule.getAction();
                }
            } else if (rule instanceof ElseRule) {
                return ((ElseRule) rule).getAction();
            }
        }
        return strategy.getInitialAction();
    }

    private int getPriority(Action action) {
        return action.getPriority();
    }

    private void executeAction(Fighter self, Fighter other, Action action, Match match, List<String> log) {
        Scoring scoring = match.getScoring();

        // Verificar recursos
        if (!self.canPay(action.getResourceCost())) {
            self.addScore(-scoring.getFailedActionPenalty());
            log.add(self.getStrategy().getName() + " no tiene recursos para " + action);
            return;
        }
        self.payResource(action.getResourceCost());

        // Ejecutar según tipo
        if (action.isOffensive()) {
            int damage = calculateDamage(self, other, action);
            if (other.isDefending()) {
                damage = (int) Math.floor(damage * 0.5);
                other.addScore(scoring.getSuccessfulDefense());
            }
            other.applyDamage(damage);
            self.addScore(damage * scoring.getDamagePoint());
            log.add(self.getStrategy().getName() + " usa " + action + " causando " + damage + " daño");
        } else if (action.isDefense()) {
            self.setDefending(true);
            log.add(self.getStrategy().getName() + " se defiende");
        } else if (action.isHealing()) {
            int healed = Math.min(action.getPower(), self.getMaxHealth() - self.getHealth());
            self.heal(healed);
            self.addScore(healed * scoring.getHealingPoint());
            log.add(self.getStrategy().getName() + " se cura " + healed + " HP");
        } else if (action.isRecovery()) {
            self.recoverResource(action.getPower());
            log.add(self.getStrategy().getName() + " recupera " + action.getPower() + " de recurso");
        } else if (action == Action.WAR_CRY) {
            self.setWarCryBonus(true);
            log.add(self.getStrategy().getName() + " usa Grito de Guerra");
        }

        self.addToHistory(action);

        // Bonus por victoria inmediata
        if (!other.isAlive()) {
            self.addScore(scoring.getVictoryBonus());
            log.add(self.getStrategy().getName() + " ha derrotado a " + other.getStrategy().getName());
        }
    }

    private int calculateDamage(Fighter attacker, Fighter other, Action action) {
        int base = action.getPower();
        int attackBonus = 0;
        if (attacker.hasWarCryBonus()) {
            attackBonus = 10;
        }

        if (attacker.getStrategy().getClassType() == ClassType.MAGE) {
            int damage = base + attacker.getMagicPower() - other.getMagicResistance() + attackBonus;
            return Math.max(1, damage);
        } else { // Guerrero
            int damage = base + attacker.getPhysicalAttack() - other.getArmor() + attackBonus;
            return Math.max(1, damage);
        }
    }

    private void checkCombo(Fighter fighter, Bonuses bonuses) {
        List<Action> history = fighter.getHistory();
        if (history.size() < 3) return;

        List<Action> lastThree = history.subList(history.size() - 3, history.size());
        ClassType type = fighter.getStrategy().getClassType();

        if (type == ClassType.MAGE && bonuses.getMageCombo() != null) {
            if (lastThree.equals(bonuses.getMageCombo())) {
                fighter.addScore(bonuses.getMageComboPoints());
            }
        } else if (type == ClassType.WARRIOR && bonuses.getWarriorCombo() != null) {
            if (lastThree.equals(bonuses.getWarriorCombo())) {
                fighter.addScore(bonuses.getWarriorComboPoints());
            }
        }
    }

    private String determineWinner(Fighter f1, Fighter f2) {
        if (!f1.isAlive() && !f2.isAlive()) return "Empate por muerte simultánea";
        if (!f1.isAlive()) return f2.getStrategy().getName();
        if (!f2.isAlive()) return f1.getStrategy().getName();

        // Por puntuación
        if (f1.getScore() > f2.getScore()) return f1.getStrategy().getName();
        if (f2.getScore() > f1.getScore()) return f2.getStrategy().getName();

        // Por vida
        if (f1.getHealth() > f2.getHealth()) return f1.getStrategy().getName();
        if (f2.getHealth() > f1.getHealth()) return f2.getStrategy().getName();

        // Por recurso
        if (f1.getResource() > f2.getResource()) return f1.getStrategy().getName();
        if (f2.getResource() > f1.getResource()) return f2.getStrategy().getName();

        return "Empate";
    }
}