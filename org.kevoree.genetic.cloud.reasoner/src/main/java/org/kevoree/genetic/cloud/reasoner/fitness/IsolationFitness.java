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
        int VMCount = 0;
        for (Object obj: nodeList) {
            ContainerNode node = (ContainerNode) obj;




            VMCount += node.getHosts().size();
            for (ContainerNode subNode: node.getHosts()) {
                if (subNode.getComponents().size() > 1) {
                    fitnessValue += 1.0;
                }
            }
        }
        return fitnessValue / (VMCount * 64) * 100.0;
    }

    @Override
    public String getName() {
        return "Isolation_Fitness";
    }

}
