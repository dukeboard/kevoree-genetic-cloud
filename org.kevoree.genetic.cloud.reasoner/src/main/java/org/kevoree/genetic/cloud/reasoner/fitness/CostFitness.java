package org.kevoree.genetic.cloud.reasoner.fitness;

import org.kevoree.ContainerNode;
import org.kevoree.ContainerRoot;
import org.kevoree.genetic.cloud.reasoner.util.PropertyCachedResolver;
import org.kevoree.genetic.framework.KevoreeFitnessFunction;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 26/03/13
 * Time: 18:51
 */

/* */
public class CostFitness implements KevoreeFitnessFunction {

    private PropertyCachedResolver resolver = new PropertyCachedResolver();
    private static final String dictionaryAttName = "cost";

    @Override
    public double evaluate(ContainerRoot containerRoot) {
        Double gCost = 0.0;
        for(ContainerNode loopNode : containerRoot.getNodes()){
            gCost += resolver.getDefault(loopNode,dictionaryAttName);
        }
        return 1/gCost;
    }
}
