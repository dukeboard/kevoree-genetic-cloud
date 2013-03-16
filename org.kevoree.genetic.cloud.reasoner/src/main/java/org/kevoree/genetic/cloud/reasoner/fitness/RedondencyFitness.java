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

    private Integer maxRedondency = 3;

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
        List<Object> components = model.selectByQuery("nodes[*]/hosts[*]/components[*]");
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
    }

    @Override
    public String getName() {
        return "Redundancy_Fitness";
    }

}
