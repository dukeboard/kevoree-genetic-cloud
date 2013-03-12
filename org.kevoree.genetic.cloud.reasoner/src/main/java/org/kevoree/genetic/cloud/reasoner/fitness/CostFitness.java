package org.kevoree.genetic.cloud.reasoner.fitness;

import org.kevoree.*;
import org.kevoree.genetic.framework.KevoreeFitnessFunction;

import java.util.HashMap;
import java.util.List;

public class CostFitness implements KevoreeFitnessFunction {

    private HashMap<TypeDefinition, Double> consumptionCache = new HashMap<TypeDefinition, Double>();
    private static final String dictionaryAttName = "cost";

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
            return 0.0;
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
        return "Cost_Fitness";
    }
}
