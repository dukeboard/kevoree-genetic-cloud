package org.kevoree.genetic.cloud.reasoner;

import org.kevoree.ContainerRoot;
import org.kevoree.genetic.cloud.library.onlineStore.*;
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

    public static ContainerRoot bestModel;

    public static void main(String[] args) throws Exception {

        SLAModel SLAModel = new SLAModel();
        SLAModel.putVCPULoad(ItemDB.class.getSimpleName(), 1.2); //ItemDB  need 2Ghz
        SLAModel.putVCPULoad(LoadBalancer.class.getSimpleName(), 0.4); //LoadBalancer  need 0.3Ghz
        SLAModel.putVCPULoad(PaymentDB.class.getSimpleName(), 0.6); //PaymentDB  need 0.6Ghz
        SLAModel.putVCPULoad(UserDB.class.getSimpleName(), 0.4); //UserDB  need 0.4Ghz
        SLAModel.putVCPULoad(WebFrontend.class.getSimpleName(), 1.2); //WebFrontend  need 2Ghz+

        RandomSolutionFactory solutionFactory = new RandomSolutionFactory(SLAModel);

        KevoreeCompositeFitnessFunction compositeFunction = new KevoreeCompositeFitnessFunction();

        HashMap<String, KevoreeFitnessFunction> fintessNames = new HashMap<String, KevoreeFitnessFunction>();
         /* Configure fitness */
        ConsumptionFitness consumptionFitness = new ConsumptionFitness();
        fintessNames.put(consumptionFitness.getName(), consumptionFitness);
        compositeFunction.addFitness(consumptionFitness);
        CompletenessFitness completenessFitness = new CompletenessFitness().setSlaModel(SLAModel);
        fintessNames.put(completenessFitness.getName(), completenessFitness);
        compositeFunction.addFitness(completenessFitness);
        SecurityFitness securityFitness = new SecurityFitness();
        fintessNames.put(securityFitness.getName(), securityFitness);
        compositeFunction.addFitness(securityFitness);
        OverloadFitness overload = new OverloadFitness();
        fintessNames.put(overload.getName(), overload);
        compositeFunction.addFitness(overload);
        SLAPerformanceFitness slaPerformanceFitness = new SLAPerformanceFitness();
        fintessNames.put(slaPerformanceFitness.getName(), slaPerformanceFitness);
        compositeFunction.addFitness(slaPerformanceFitness);
        fintessNames.put("mean", compositeFunction);


        SolutionPloter ploter = new SolutionPloter();
        LinkedList<GenResult> result = new LinkedList<GenResult>();
        HashMap<String, LinkedList<Measure>> timeResults = new HashMap<String, LinkedList<Measure>>();

       // for(int i = 0; i < 3 ; i++) {
        //SLAModel.scale(1d);
        //performRound(solutionFactory, result, timeResults, fintessNames, 1, 200);
       SLAModel.scale(4d);
           performRound(solutionFactory, result, timeResults, fintessNames, 4, 200);
        //SLAModel.scale(12d);
        //    performRound(solutionFactory, result, timeResults, fintessNames, 12, 200);
          //  System.out.println("========");
        //}
        ploter.setPrefixe("random_4");
         ploter.plotResult(timeResults);

        //System.out.println("Found " + result.size() + " solutions in " + (System.currentTimeMillis() - currentTime) + " ms");

    }


    private static void performRound(RandomSolutionFactory solutionFactory, LinkedList<GenResult> result, HashMap<String, LinkedList<Measure>> timeResults, HashMap<String, KevoreeFitnessFunction> fintessNames, int factor, int iterations) {

        solutionFactory.setNumberOfInfraNode_fullPower(2 * factor);
        solutionFactory.setNumberOfInfraNode_fullPower(3 * factor);
        solutionFactory.createBaseModel();
        result.clear();
        bestModel = null;
        //Generates 200 solutions
        //long currentTime = System.currentTimeMillis();

        long timestamp3 = System.currentTimeMillis();
        for(int i = 0 ; i < iterations ; i++) {
            ContainerRoot solution = solutionFactory.createRandomSolution();
            result.addLast(new GenResult(System.currentTimeMillis() - timestamp3, solution));
        }
        long timestamp4 = System.currentTimeMillis();
        System.out.println("Generated models for "+(5*factor)+" nodes in " + (timestamp4-timestamp3) + "ms ");
        computeTimes(timeResults, result, fintessNames, "" + (5 * factor));
        long timestamp5 = System.currentTimeMillis();
        System.out.println("Computing metrics in " + (timestamp5-timestamp4) + "ms");
        for (KevoreeFitnessFunction fit : fintessNames.values()) {
            System.out.println(fit.getName()+"->"+fit.evaluate(bestModel));
        }
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
                bestModel = res.model;
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

        System.out.println("Best Mean:" + lastMean);
    }



}
