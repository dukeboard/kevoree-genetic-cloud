package org.kevoree.genetic.cloud.reasoner;

import org.kevoree.ContainerRoot;
import org.kevoree.genetic.KevoreeEngineInstrument;
import org.kevoree.genetic.cloud.reasoner.fitness.*;
import org.kevoree.genetic.cloud.reasoner.operators.AddVirtualNodeOperator;
import org.kevoree.genetic.cloud.reasoner.operators.OptimizeRedondencyOperator;
import org.kevoree.genetic.cloud.reasoner.operators.RandomAddComponentOperator;
import org.kevoree.genetic.cloud.reasoner.plot.SolutionPloter;
import org.kevoree.genetic.cloud.reasoner.population.CloudPopulationFactory;
import org.kevoree.genetic.cloud.reasoner.population.RandomSolutionFactory;
import org.kevoree.genetic.framework.KevoreeCompositeFitnessFunction;
import org.kevoree.genetic.framework.KevoreeFitnessFunction;
import org.kevoree.genetic.framework.KevoreeGeneticEngine;
import org.kevoree.genetic.framework.KevoreeSolution;
import org.kevoree.genetic.library.operator.RemoveChildNode;
import org.kevoree.genetic.library.operator.RemoveComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class RunnerRandomSolution {

    public static void main(String[] args) throws Exception {

        RandomSolutionFactory solutionFactory = new RandomSolutionFactory();
        solutionFactory.addType("ItemDB").addType("LoadBalancer").addType("PaymentDB").addType("UserDB").addType("WebFrontend");

        KevoreeCompositeFitnessFunction compositeFunction = new KevoreeCompositeFitnessFunction();

        HashMap<String, KevoreeFitnessFunction> fintessNames = new HashMap<String, KevoreeFitnessFunction>();
         /* Configure fitness */
        ConsumptionFitness consumptionFitness = new ConsumptionFitness();
        fintessNames.put(consumptionFitness.getName(), consumptionFitness);
        compositeFunction.addFitness(consumptionFitness);
        RedondencyFitness redondencyFitness = new RedondencyFitness().setAllTypes(solutionFactory.getAllTypes());
        fintessNames.put(redondencyFitness.getName(), redondencyFitness);
        compositeFunction.addFitness(redondencyFitness);
        CompletenessFitness completenessFitness = new CompletenessFitness().setAllTypes(solutionFactory.getAllTypes());
        fintessNames.put(completenessFitness.getName(), completenessFitness);
        compositeFunction.addFitness(completenessFitness);
        SecurityFitness securityFitness = new SecurityFitness();
        fintessNames.put(securityFitness.getName(), securityFitness);
        compositeFunction.addFitness(securityFitness);
        fintessNames.put("mean", compositeFunction);

        SolutionPloter ploter = new SolutionPloter();

        List<ContainerRoot> result = new ArrayList<ContainerRoot>() ;
        long currentTime = System.currentTimeMillis();
        for(int i = 0 ; i < 200 ; i++) {
            ContainerRoot solution = solutionFactory.createRandomSolution();
            result.add(solution);
        }

        HashMap<String, LinkedList<Double>> timeResults = new HashMap<String, LinkedList<Double>>();
        for(ContainerRoot model : result) {
            for(String fitness : fintessNames.keySet()) {
                LinkedList<Double> fitnessValues = timeResults.get(fitness);
                if(fitnessValues == null) {
                    fitnessValues = new LinkedList<Double>();
                    timeResults.put(fitness, fitnessValues);
                }
                fitnessValues.addLast(fintessNames.get(fitness).evaluate(model));
            }
        }

        ploter.plotResult(timeResults);

        System.out.println("Found " + result.size() + " solutions in " + (System.currentTimeMillis() - currentTime) + " ms");

    }


}
