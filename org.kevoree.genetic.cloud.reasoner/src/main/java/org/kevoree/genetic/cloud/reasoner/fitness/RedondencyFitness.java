package org.kevoree.genetic.cloud.reasoner.fitness;

import org.kevoree.*;
import org.kevoree.genetic.framework.KevoreeFitnessFunction;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 12/03/13
 * Time: 15:01
 */
public class RedondencyFitness implements KevoreeFitnessFunction {

    private Integer globalRedondency = 5; //MAGIC NUMBER !!!

    @Override
    public double evaluate(ContainerRoot model) {
        HashMap<String, Integer> counter = new HashMap<String, Integer>();
        //INITIATE with all TD of components
        for (TypeDefinition td : model.getTypeDefinitions()) {
            if (td instanceof ComponentType) {
                counter.put(td.getName(), 0);
            }
        }
        for (ContainerNode node : model.getNodes()) {
            for (ComponentInstance instance : node.getComponents()) {
                Integer val = counter.get(instance.getTypeDefinition().getName());
                val = val + 1;
                counter.put(instance.getTypeDefinition().getName(), val);
            }
        }

        System.out.println(counter);

        Integer maxRedondency = globalRedondency * counter.size();
        Integer globalScore = 0;
        for (String key : counter.keySet()) {
            globalScore += (globalRedondency - counter.get(key));
        }
        return globalScore / maxRedondency * 100;
    }

    @Override
    public String getName() {
        return "Redundancy_Fitness";
    }

}
