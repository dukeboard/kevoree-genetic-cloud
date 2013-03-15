package org.kevoree.genetic.cloud.reasoner;

import org.kevoree.genetic.cloud.reasoner.fitness.*;
import org.kevoree.genetic.cloud.reasoner.operators.AddVirtualNodeOperator;
import org.kevoree.genetic.cloud.reasoner.operators.OptimizeRedondencyOperator;
import org.kevoree.genetic.cloud.reasoner.operators.RandomAddComponentOperator;
import org.kevoree.genetic.cloud.reasoner.population.CloudPopulationFactory;
import org.kevoree.genetic.framework.KevoreeGeneticEngine;
import org.kevoree.genetic.framework.KevoreeSolution;
import org.kevoree.genetic.library.operator.RemoveChildNode;
import org.kevoree.genetic.library.operator.RemoveComponent;

import java.util.List;

public class RunnerRandomSolution {

    public static void main(String[] args) throws Exception {
        KevoreeGeneticEngine engine = new KevoreeGeneticEngine()
                .setPopulationFactory(new CloudPopulationFactory());

        /* Configure operator */
        RandomAddComponentOperator operator = new RandomAddComponentOperator();
        operator.addType("ItemDB").addType("LoadBalancer").addType("PaymentDB").addType("UserDB").addType("WebFrontend");
        operator.setSelectorQuery("nodes[{ typeDefinition.name = *CustomerNode }]");
        engine.addOperator(operator);
        engine.addOperator(new AddVirtualNodeOperator().setSelectorQuery("nodes[{ typeDefinition.name = *InfraNode }]"));
        engine.addOperator(new RemoveComponent().setSelectorQuery("nodes[{name=*}]/hosts[{name=*}]/components[{name=*}]"));
        engine.addOperator(new RemoveChildNode().setSelectorQuery("nodes[{ typeDefinition.name = *CustomerNode }]"));
        //engine.addOperator(new MoveNode().setSelectorQuery("nodes[{ typeDefinition.name = *CustomerNode }]"));

        /* Configure fitness */
        engine.addFitnessFuntion(new ConsumptionFitness());
        engine.addFitnessFuntion(new IsolationFitness());
        engine.addFitnessFuntion(new RedondencyFitness().setAllTypes(operator.getAllTypes()));
        engine.addFitnessFuntion(new MaximizeChildNodesFitness());
        engine.addFitnessFuntion(new CompletenessFitness().setAllTypes(operator.getAllTypes()));


        engine.setMaxGeneration(1000);
        long currentTime = System.currentTimeMillis();
        List<KevoreeSolution> result = engine.solve();
        System.out.println("Found "+result.size()+" solutions in " + (System.currentTimeMillis() - currentTime) + " ms");
        SolutionFilter filter = new SolutionFilter();
        for (KevoreeSolution solution : filter.order(filter.filterSolution(result))) {
            solution.print(System.out);
        }
    }


}
