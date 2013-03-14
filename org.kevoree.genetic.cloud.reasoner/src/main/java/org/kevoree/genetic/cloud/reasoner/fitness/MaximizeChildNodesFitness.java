package org.kevoree.genetic.cloud.reasoner.fitness;

import org.kevoree.ContainerNode;
import org.kevoree.ContainerRoot;
import org.kevoree.DictionaryValue;
import org.kevoree.TypeDefinition;
import org.kevoree.genetic.framework.KevoreeFitnessFunction;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 13/03/13
 * Time: 16:21
 */
public class MaximizeChildNodesFitness implements KevoreeFitnessFunction {

    private final String nbSubNodes = "vcpu";
    private HashMap<TypeDefinition, Integer> vCPUCache = new HashMap<TypeDefinition, Integer>();

    @Override
    public double evaluate(ContainerRoot model) {
        double nbChildNodes = 0;
        double vcpuCapacity = 0;
        for (ContainerNode node : model.getNodes()) {
            nbChildNodes += node.getHosts().size();
            Integer vcpu = resolveDictionaryValue(node.getTypeDefinition());
            if(vcpu != null){
                vcpuCapacity += vcpu;
            }
        }
        if (vcpuCapacity == 0) {
            return 0d;
        }
        double vcpuPercent = (nbChildNodes / vcpuCapacity) * 100;
        return 100-vcpuPercent;
    }

    @Override
    public String getName() {
        return "MaximizeChildNodes_Fitness";
    }

    protected Integer resolveDictionaryValue(TypeDefinition td) {
        Integer cacheValue = vCPUCache.get(td);
        if (cacheValue == null) {
            for (DictionaryValue v : td.getDictionaryType().getDefaultValues()) {
                if (v.getAttribute().getName().equals(nbSubNodes)) {
                    cacheValue = Integer.parseInt(v.getValue());
                }
            }
        }
        if (cacheValue == null) {
            cacheValue = 0;
        }
        vCPUCache.put(td, cacheValue);
        return cacheValue;
    }

}
