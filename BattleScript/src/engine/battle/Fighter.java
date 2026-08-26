package engine.battle;

import battlescript.model.*;
import java.util.*;

final class Fighter {
    final Strategy strategy; 
    final int maxHealth, 
            maxResource, 
            physicalAttack, 
            magicPower, 
            armor, 
            magicResistance, 
            speed;
    
    int health, 
            resource, 
            score; 
    boolean defending, 
            warCry; 
    
    final List<Action> history = new ArrayList<Action>();
    Fighter(Strategy strategy) { 
        this.strategy=strategy; 
        boolean mage=strategy.getClassType()==ClassType.MAGE; 
        maxHealth=mage?100:140; 
        maxResource=mage?120:100; 
        physicalAttack=mage?5:22; 
        magicPower=mage?25:0; 
        armor=mage?8:20; 
        magicResistance=mage?18:8; 
        speed=mage?14:10; 
        health=maxHealth; 
        resource=maxResource; 
    }
    boolean alive() { 
        return health>0; 
    }
}
