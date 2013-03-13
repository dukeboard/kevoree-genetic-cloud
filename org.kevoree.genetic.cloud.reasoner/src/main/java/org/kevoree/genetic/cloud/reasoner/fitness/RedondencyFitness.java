package org.kevoree.genetic.cloud.reasoner.fitness;

import org.kevoree.*;
import org.kevoree.genetic.framework.KevoreeFitnessFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 12/03/13
 * Time: 15:01
 */
public class RedondencyFitness implements KevoreeFitnessFunction {

    private Integer globalRedondency = 5; //MAGIC NUMBER !!!

    private List<String> types = new ArrayList<String>();

    public RedondencyFitness addType(String t) {
        types.add(t);
        return this;
    }

    public RedondencyFitness setAllTypes(List<String> _types) {
        types = _types;
        return this;
    }

    @Override
    public double evaluate(ContainerRoot model) {
        HashMap<String, Integer> counter = new HashMap<String, Integer>();
        //INITIATE with all TD of components
        for (String tdName : types) {
            counter.put(tdName, 0);
        }
        for (ContainerNode node : model.getNodes()) {
            for (ComponentInstance instance : node.getComponents()) {
                Integer val = counter.get(instance.getTypeDefinition().getName());
                if (val != null) {
                    val = val + 1;
                    counter.put(instance.getTypeDefinition().getName(), val);
                }
            }
        }
        Integer maxRedondency = globalRedondency * counter.size();
        if (maxRedondency == 0) {
            return 0.0d;
        }
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
