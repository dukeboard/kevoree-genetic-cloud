package org.kevoree.genetic.cloud.reasoner;

import org.kevoree.genetic.cloud.reasoner.fitness.ConsumptionFitness;
import org.kevoree.genetic.cloud.reasoner.fitness.IsolationFitness;
import org.kevoree.genetic.cloud.reasoner.fitness.MaximizeChildNodesFitness;
import org.kevoree.genetic.cloud.reasoner.fitness.RedondencyFitness;
import org.kevoree.genetic.cloud.reasoner.operators.AddVirtualNodeOperator;
import org.kevoree.genetic.cloud.reasoner.operators.OptimizeRedondencyOperator;
import org.kevoree.genetic.cloud.reasoner.population.CloudPopulationFactory;
import org.kevoree.genetic.framework.KevoreeGeneticEngine;
import org.kevoree.genetic.framework.KevoreeSolution;
import org.kevoree.genetic.library.operator.RemoveChildNode;

import java.util.List;

public class RunnerOptimizeConsumption {

    public static void main(String[] args) throws Exception {
        KevoreeGeneticEngine engine = new KevoreeGeneticEngine()
                .setPopulationFactory(new CloudPopulationFactory());

        /* Configure operator */
        OptimizeRedondencyOperator operator = new OptimizeRedondencyOperator();
        operator.addType("ItemDB").addType("LoadBalancer").addType("PaymentDB").addType("UserDB").addType("WebFrontend");
        operator.setSelectorQuery("nodes[{ typeDefinition.name = *CustomerNode }]");
        engine.addOperator(operator);
        engine.addOperator(new AddVirtualNodeOperator().setSelectorQuery("nodes[{ typeDefinition.name = *InfraNode }]"));
        //engine.addOperator(new RemoveComponent().setSelectorQuery("nodes[{name=*}]/hosts[{name=*}]/components[{name=*}]"));
        engine.addOperator(new RemoveChildNode().setSelectorQuery("nodes[{ typeDefinition.name = *CustomerNode }]"));
        //engine.addOperator(new MoveNode().setSelectorQuery("nodes[{ typeDefinition.name = *CustomerNode }]"));

        /* Configure fitness */
        engine.addFitnessFuntion(new ConsumptionFitness());
        engine.addFitnessFuntion(new IsolationFitness());
        engine.addFitnessFuntion(new RedondencyFitness().setAllTypes(operator.getAllTypes()));
        engine.addFitnessFuntion(new MaximizeChildNodesFitness());


        engine.setMaxGeneration(1000);
        long currentTime = System.currentTimeMillis();
        List<KevoreeSolution> result = engine.solve();
        System.out.println("Found solutions in " + (System.currentTimeMillis() - currentTime) + " ms");
        for (KevoreeSolution solution : result) {
            solution.print(System.out);
        }
    }


}
