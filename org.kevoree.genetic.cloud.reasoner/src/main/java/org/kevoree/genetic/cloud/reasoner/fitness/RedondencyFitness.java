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

    private List<String> types = new ArrayList<String>();

    public RedondencyFitness addType(String t) {
        types.add(t);
        return this;
    }

    public RedondencyFitness setAllTypes(List<String> _types) {
        types = _types;
        return this;
    }

    private Integer maxRedondency = 5;

    public Integer getMaxRedondency() {
        return maxRedondency;
    }

    public RedondencyFitness setMaxRedondency(Integer maxRedondency) {
        this.maxRedondency = maxRedondency;
        return this;
    }

    @Override
    public double evaluate(ContainerRoot model) {
        HashMap<String, Double> map = new HashMap<String, Double>();
        List<Object> components = model.selectByQuery("nodes[{name=*}/hosts[{name=*}]/components[{name=*}]");
        Double redondency = 100d;
        for (String tdName : types) {
            map.put(tdName, 0d);
        }
        for (Object o : components) {
            ComponentInstance ci = (ComponentInstance) o;
            map.put(ci.getTypeDefinition().getName(), map.get(ci.getTypeDefinition().getName()) + 1);
        }
        for (Object key : map.keySet()) {
            Double nbVal = map.get(key);
            if(nbVal > 1){
                Double localPercent = nbVal / maxRedondency * 100d;
                redondency = redondency - (localPercent / types.size());
            }
        }
        return Math.abs(redondency);



          /*

        HashMap<String, Integer> counter = new HashMap<String, Integer>();

        Integer totalVCPUcapacity = 0;
        //INITIATE with all TD of components
        for (String tdName : types) {
            counter.put(tdName, 0);
        }
        Integer inodeCounter = 0;
        for (ContainerNode node : model.getNodes()) {
            if (node.getHost() == null) {
                inodeCounter = inodeCounter + 1;
            }

            Integer vcpu = resolveDictionaryValue(node.getTypeDefinition());
            if(vcpu != null){
                totalVCPUcapacity += vcpu;
            }

            List<String> alreadyFoundOnNode = new ArrayList<String>();
            for (ComponentInstance instance : node.getComponents()) {
                Integer val = counter.get(instance.getTypeDefinition().getName());
                if (val != null) {
                    if (!alreadyFoundOnNode.contains(instance.getTypeDefinition().getName())) {
                        val = val + 1;
                        alreadyFoundOnNode.add(instance.getTypeDefinition().getName());
                        counter.put(instance.getTypeDefinition().getName(), val);
                    }
                }
            }
        }
        //Maximal redondency, one component on each CustomerNode
        double maxRedondency = 3;//(totalVCPUcapacity/2) * inodeCounter;
        if (maxRedondency == 0) {
            return 0.0d;
        }
        double globalScore = 0d;
        for (String key : counter.keySet()) {
            double localCounter = counter.get(key);
            double localPercentage = (localCounter / maxRedondency) * 100;
            globalScore = globalScore + (100-localPercentage);
        }
        return globalScore / counter.size();*/
    }

    @Override
    public String getName() {
        return "Redundancy_Fitness";
    }

    /*
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
    }  */

}
