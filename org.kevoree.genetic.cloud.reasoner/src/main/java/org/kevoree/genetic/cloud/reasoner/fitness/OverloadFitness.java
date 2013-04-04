package org.kevoree.genetic.cloud.reasoner.fitness;

import org.kevoree.ComponentInstance;
import org.kevoree.ContainerNode;
import org.kevoree.ContainerRoot;
import org.kevoree.genetic.cloud.reasoner.util.PropertyCachedResolver;
import org.kevoree.genetic.framework.KevoreeFitnessFunction;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 15/03/13
 * Time: 17:06
 */

public class OverloadFitness implements KevoreeFitnessFunction {

    private Integer percentLostByLoadBreak = 20;
    private String property = "vcpu_load";
    private PropertyCachedResolver resolver = new PropertyCachedResolver();

    @Override
    public double evaluate(ContainerRoot model) {
        Double allPercent = 0d;
        List<Object> virtualNodes = model.selectByQuery("nodes[*]");
        for (Object o : virtualNodes) {
            ContainerNode node = (ContainerNode) o;
            double localVCPULoad = 0;
            for (ComponentInstance c : node.getComponents()) {
                localVCPULoad += resolver.getDefault(c, property);
            }
            if(localVCPULoad > 1){
                allPercent += percentLostByLoadBreak * localVCPULoad;
            }
        }
        if (virtualNodes.isEmpty()) {
            return 0d;
        } else {
            return allPercent;
        }

    }

}
