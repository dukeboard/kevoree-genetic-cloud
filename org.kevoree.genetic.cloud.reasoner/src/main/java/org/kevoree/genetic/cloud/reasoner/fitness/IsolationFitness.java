package org.kevoree.genetic.cloud.reasoner.fitness;

import org.kevoree.ContainerNode;
import org.kevoree.ContainerRoot;
import org.kevoree.genetic.framework.KevoreeFitnessFunction;

import java.util.List;

public class IsolationFitness implements KevoreeFitnessFunction {

    @Override
    public double evaluate(ContainerRoot model) {
        List<Object> nodeList = model.selectByQuery("nodes[{ typeDefinition.name = *InfraNode }]");
        double fitnessValue = 0.0;
        int totalComponent = 0;
        for (Object obj : nodeList) {
            ContainerNode node = (ContainerNode) obj;
            for (ContainerNode subNode : node.getHosts()) {
                totalComponent += subNode.getComponents().size();
                if (subNode.getComponents().size() > 1) {
                    fitnessValue += subNode.getComponents().size();
                }
            }
        }
        if (totalComponent == 0.0) {
            return 0.0;
        }
        return fitnessValue / totalComponent * 100.0;
    }

    @Override
    public String getName() {
        return "Isolation_Fitness";
    }

}
