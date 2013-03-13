package org.kevoree.genetic.cloud.reasoner.fitness;

import org.kevoree.ContainerNode;
import org.kevoree.ContainerRoot;
import org.kevoree.genetic.framework.KevoreeFitnessFunction;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 13/03/13
 * Time: 16:21
 */
public class EmptySlotFitness implements KevoreeFitnessFunction {

    private Integer globalRedondency = 5; //MAGIC NUMBER !!!

    @Override
    public double evaluate(ContainerRoot model) {
        int nbFreeSlot = 0;
        int nbTotalSlot = 0;
        for (ContainerNode node : model.getNodes()) {
            for(ContainerNode subNode : node.getHosts()){
                nbTotalSlot += globalRedondency;
                nbFreeSlot += (globalRedondency - subNode.getComponents().size());
            }
        }
        if (nbTotalSlot == 0) {
            return 100d;
        }
        return nbFreeSlot;
    }

    @Override
    public String getName() {
        return "EmptySlot_Fitness";
    }
}
