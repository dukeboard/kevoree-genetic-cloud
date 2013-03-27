package org.kevoree.genetic.cloud.reasoner.fitness;

import org.kevoree.ComponentInstance;
import org.kevoree.ContainerRoot;
import org.kevoree.genetic.cloud.reasoner.SLAModel;
import org.kevoree.genetic.framework.KevoreeFitnessFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 14/03/13
 * Time: 19:42
 */
public class CompletenessFitness extends AbstractSLAKevoreeFitnessFunction {

    @Override
    public double evaluate(ContainerRoot model) {
        HashMap<String, Double> map = new HashMap<String, Double>();
        List<Object> components = model.selectByQuery("nodes[*]/components[*]");
        Double completeness = 100d;
        for (String tdName : slaModel.getTypes()) {
            map.put(tdName, 0d);
        }
        for (Object o : components) {
            ComponentInstance ci = (ComponentInstance) o;
            if (map.get(ci.getTypeDefinition().getName()) == 0d) {
                completeness = completeness - (100 / slaModel.getTypes().size());
                map.put(ci.getTypeDefinition().getName(), 1d);
            }
        }
        return Math.abs(completeness);
    }

}
