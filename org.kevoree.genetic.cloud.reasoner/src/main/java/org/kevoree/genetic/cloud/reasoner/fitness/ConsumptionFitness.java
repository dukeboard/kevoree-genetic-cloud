package org.kevoree.genetic.cloud.reasoner.fitness;

import org.kevoree.ContainerNode;
import org.kevoree.ContainerRoot;
import org.kevoree.DictionaryValue;
import org.kevoree.TypeDefinition;
import org.kevoree.genetic.framework.KevoreeFitnessFunction;

import java.util.HashMap;
import java.util.List;


/*
 *  */
public class ConsumptionFitness implements KevoreeFitnessFunction {

    private static final String dictionaryAttName = "consumption";
    private HashMap<TypeDefinition, Double> consumptionCache = new HashMap<TypeDefinition, Double>();

    @Override
    public double evaluate(ContainerRoot model) {
        List<Object> infraNodeList = model.selectByQuery("nodes[{ typeDefinition.name = *InfraNode }]");
        double maxCost = 0.0;
        double cost = 0.0;
        for (Object obj : infraNodeList) {
            ContainerNode infraNode = (ContainerNode) obj;
            double tempCost = resolveDictionaryValue(infraNode.getTypeDefinition());
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

    private double resolveDictionaryValue(TypeDefinition td) {
        Double cacheValue = consumptionCache.get(td);
        if (cacheValue == null) {
            for (DictionaryValue v : td.getDictionaryType().getDefaultValues()) {
                if (v.getAttribute().getName().equals(dictionaryAttName)) {
                    cacheValue = Double.parseDouble(v.getValue());
                }
            }
        }
        if (cacheValue == null) {
            cacheValue = 0d;
        }
        consumptionCache.put(td, cacheValue);
        return cacheValue;
    }

    @Override
    public String getName() {
        return "Consumption_Fitness";
    }

}
