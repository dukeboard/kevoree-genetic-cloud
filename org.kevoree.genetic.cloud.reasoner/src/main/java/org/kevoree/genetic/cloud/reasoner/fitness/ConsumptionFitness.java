package org.kevoree.genetic.cloud.reasoner.fitness;

import org.kevoree.ContainerNode;
import org.kevoree.ContainerRoot;
import org.kevoree.TypeDefinition;
import org.kevoree.genetic.cloud.reasoner.util.PropertyCachedResolver;
import org.kevoree.genetic.framework.KevoreeFitnessFunction;

import java.util.HashMap;
import java.util.List;


/*
 *  */
public class ConsumptionFitness implements KevoreeFitnessFunction {

    private static final String dictionaryAttName = "consumption";
    private HashMap<TypeDefinition, Double> consumptionCache = new HashMap<TypeDefinition, Double>();
    private PropertyCachedResolver resolver = new PropertyCachedResolver();

    @Override
    public double evaluate(ContainerRoot model) {
        List<Object> infraNodeList = model.selectByQuery("nodes[{ typeDefinition.name = *InfraNode }]");
        double maxCost = 0.0;
        double cost = 0.0;
        for (Object obj : infraNodeList) {
            ContainerNode infraNode = (ContainerNode) obj;
            double tempCost = resolver.getDefault(infraNode, dictionaryAttName);
            maxCost += tempCost;
            if (infraNode.getHosts().size() > 0) {
                cost += tempCost;
            }
        }
        if (maxCost == 0.0) {
            return maxCost;
        } else {
            return cost / maxCost * 100.0;
        }
    }

}
