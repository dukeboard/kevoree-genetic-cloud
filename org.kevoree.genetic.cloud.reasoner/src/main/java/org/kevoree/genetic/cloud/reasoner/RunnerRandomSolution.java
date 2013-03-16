package org.kevoree.genetic.cloud.reasoner;

import org.kevoree.ContainerRoot;
import org.kevoree.genetic.cloud.reasoner.fitness.*;
import org.kevoree.genetic.cloud.reasoner.plot.SolutionPloter;
import org.kevoree.genetic.cloud.reasoner.population.RandomSolutionFactory;
import org.kevoree.genetic.cloud.reasoner.util.GenResult;
import org.kevoree.genetic.cloud.reasoner.util.Measure;
import org.kevoree.genetic.framework.KevoreeCompositeFitnessFunction;
import org.kevoree.genetic.framework.KevoreeFitnessFunction;
import java.util.HashMap;
import java.util.LinkedList;

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
        //RedondencyFitness redondencyFitness = new RedondencyFitness().setAllTypes(solutionFactory.getAllTypes());
        //fintessNames.put(redondencyFitness.getName(), redondencyFitness);
        //compositeFunction.addFitness(redondencyFitness);
        //CompletenessFitness completenessFitness = new CompletenessFitness().setAllTypes(solutionFactory.getAllTypes());
        //fintessNames.put(completenessFitness.getName(), completenessFitness);
        //compositeFunction.addFitness(completenessFitness);
        SecurityFitness securityFitness = new SecurityFitness();
        fintessNames.put(securityFitness.getName(), securityFitness);
        compositeFunction.addFitness(securityFitness);
        fintessNames.put("mean", compositeFunction);

        SolutionPloter ploter = new SolutionPloter();
        LinkedList<GenResult> result = new LinkedList<GenResult>();
        HashMap<String, LinkedList<Measure>> timeResults = new HashMap<String, LinkedList<Measure>>();

        performRound(solutionFactory, result, timeResults, fintessNames, 1, 200);
        performRound(solutionFactory, result, timeResults, fintessNames, 4, 200);
        performRound(solutionFactory, result, timeResults, fintessNames, 20, 200);


        ploter.plotResult(timeResults);

        //System.out.println("Found " + result.size() + " solutions in " + (System.currentTimeMillis() - currentTime) + " ms");

    }


    private static void performRound(RandomSolutionFactory solutionFactory, LinkedList<GenResult> result, HashMap<String, LinkedList<Measure>> timeResults, HashMap<String, KevoreeFitnessFunction> fintessNames, int factor, int iterations) {

        solutionFactory.setNumberOfInfraNode_fullPower(2 * factor);
        solutionFactory.setNumberOfInfraNode_fullPower(3 * factor);
        solutionFactory.createBaseModel();
        result.clear();
        //Generates 200 solutions
        //long currentTime = System.currentTimeMillis();

        long timestamp3 = System.currentTimeMillis();
        for(int i = 0 ; i < iterations ; i++) {
            ContainerRoot solution = solutionFactory.createRandomSolution();
            result.addLast(new GenResult(System.currentTimeMillis() - timestamp3, solution));
        }
        long timestamp4 = System.currentTimeMillis();
        System.out.println("Generated models for "+(5*factor)+" nodes in " + (timestamp4-timestamp3) + "ms");
        computeTimes(timeResults, result, fintessNames, "" + (5 * factor));
        long timestamp5 = System.currentTimeMillis();
        System.out.println("Computing metrics in " + (timestamp5-timestamp4) + "ms");
    }


    private static void computeTimes(HashMap<String, LinkedList<Measure>> timeResults, LinkedList<GenResult> results, HashMap<String, KevoreeFitnessFunction> fintessNames, String iteration) {

        //For each model, evaluate the fitnesses
        double lastMean = Double.MAX_VALUE;
        LinkedList<Measure> fitnessValues = timeResults.get("mean" + iteration);
        if(fitnessValues == null) {
            fitnessValues = new LinkedList<Measure>();
            timeResults.put("mean" + iteration, fitnessValues);
        }

        for(GenResult res : results) {
            double evalOfMean = fintessNames.get("mean").evaluate(res.model);
            if(evalOfMean < lastMean) {
                lastMean = evalOfMean;
            }
           // System.out.println("lastMean:" + lastMean);
            fitnessValues.addLast(new Measure(res.timestamp, lastMean));
            /*
            //For each fitness append the evaluation.
            for(String fitness : fintessNames.keySet()) {
                LinkedList<Double> fitnessValues = timeResults.get(fitness);
                if(fitnessValues == null) {
                    fitnessValues = new LinkedList<Double>();
                    timeResults.put(fitness, fitnessValues);
                }
                fitnessValues.addLast(fintessNames.get(fitness).evaluate(model));
            }
            */
        }
    }



}
