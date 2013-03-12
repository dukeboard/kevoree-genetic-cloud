package org.kevoree.genetic.cloud.reasoner;

import org.kevoree.genetic.cloud.reasoner.fitness.ConsumptionFitness;
import org.kevoree.genetic.cloud.reasoner.population.CloudPopulationFactory;
import org.kevoree.genetic.framework.KevoreeGeneticEngine;
import org.kevoree.genetic.framework.KevoreeSolution;

import java.util.List;

public class RunnerOptimizeConsumption {

    public static void main(String[] args) throws Exception {
        KevoreeGeneticEngine engine = new KevoreeGeneticEngine()
                .setPopulationFactory(new CloudPopulationFactory())

                .addFitnessFuntion(new ConsumptionFitness());

        engine.setMaxGeneration(25);
        long currentTime = System.currentTimeMillis();
        List<KevoreeSolution> result = engine.solve();
        System.out.println("Found solutions in " + (System.currentTimeMillis() - currentTime) + " ms");
        for (KevoreeSolution solution : result) {
            solution.print(System.out);
        }
    }


}
