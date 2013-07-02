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
    public static ContainerRoot worstModel;

    public static void main(String[] args) throws Exception {

        SLAModel SLAModel = new SLAModel();
        SLAModel.putVCPULoad(ItemDB.class.getSimpleName(), 1.2); //ItemDB  need 2Ghz
        SLAModel.putVCPULoad(LoadBalancer.class.getSimpleName(), 0.4); //LoadBalancer  need 0.3Ghz
        SLAModel.putVCPULoad(PaymentDB.class.getSimpleName(), 0.6); //PaymentDB  need 0.6Ghz
        SLAModel.putVCPULoad(UserDB.class.getSimpleName(), 0.4); //UserDB  need 0.4Ghz
        SLAModel.putVCPULoad(WebFrontend.class.getSimpleName(), 1.2); //WebFrontend  need 2Ghz+


        SLAModel.putSecurityLevel(ItemDB.class.getSimpleName(), 2.0);
        SLAModel.putSecurityLevel(LoadBalancer.class.getSimpleName(), 0.0);
        SLAModel.putSecurityLevel(PaymentDB.class.getSimpleName(), 4.0);
        SLAModel.putSecurityLevel(UserDB.class.getSimpleName(), 3.0);
        SLAModel.putSecurityLevel(WebFrontend.class.getSimpleName(), 1.0);


        RandomSolutionFactory solutionFactory = new RandomSolutionFactory(SLAModel);

        KevoreeCompositeFitnessFunction compositeFunction = new KevoreeCompositeFitnessFunction();

        HashMap<String, KevoreeFitnessFunction> fintessNames = new HashMap<String, KevoreeFitnessFunction>();
         /* Configure fitness */
        ConsumptionFitness consumptionFitness = new ConsumptionFitness();
        fintessNames.put(consumptionFitness.getClass().getSimpleName(), consumptionFitness);
        compositeFunction.addFitness(consumptionFitness);
        AbstractSLAKevoreeFitnessFunction completenessFitness = new CompletenessFitness().setSlaModel(SLAModel);
        fintessNames.put(completenessFitness.getClass().getSimpleName(), completenessFitness);
        compositeFunction.addFitness(completenessFitness);
        SecurityFitness securityFitness = new SecurityFitness();
        fintessNames.put(securityFitness.getClass().getSimpleName(), securityFitness);
        compositeFunction.addFitness(securityFitness);
        OverloadFitness overload = new OverloadFitness();
        fintessNames.put(overload.getClass().getSimpleName(), overload);
        compositeFunction.addFitness(overload);
        SLAPerformanceFitness slaPerformanceFitness = new SLAPerformanceFitness();
        fintessNames.put(slaPerformanceFitness.getClass().getSimpleName(), slaPerformanceFitness);
        compositeFunction.addFitness(slaPerformanceFitness);
        fintessNames.put("mean", compositeFunction);

        SolutionPloter ploter = new SolutionPloter();
        LinkedList<GenResult> result = new LinkedList<GenResult>();

        // for(int i = 0; i < 3 ; i++) {
        performRound(solutionFactory, result, fintessNames, 8, 500, SLAModel, ploter);
        //SLAModel.scale(4d);
        //performRound(solutionFactory, result, timeResults, fintessNames, 4, 200);
        //SLAModel.scale(12d);
        //    performRound(solutionFactory, result, timeResults, fintessNames, 12, 200);
        //  System.out.println("========");
        //}
        //System.out.println("Found " + result.size() + " solutions in " + (System.currentTimeMillis() - currentTime) + " ms");

    }


    private static void performRound(RandomSolutionFactory solutionFactory, LinkedList<GenResult> result, HashMap<String, KevoreeFitnessFunction> fintessNames, int factor, int iterations, SLAModel slaModel, SolutionPloter ploter) {
        HashMap<String, LinkedList<Measure>> timeResults = new HashMap<String, LinkedList<Measure>>();

        slaModel.scale(new Double(factor));
        solutionFactory.scale(factor);
        for (KevoreeFitnessFunction fit : fintessNames.values()) {
            if (fit instanceof AbstractSLAKevoreeFitnessFunction) {
                ((AbstractSLAKevoreeFitnessFunction) fit).setSlaModel(slaModel);
            }
        }

        result.clear();
        bestModel = null;
        worstModel = null;

        long timestamp3 = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            ContainerRoot solution = solutionFactory.createPopulation().get(0);
            result.addLast(new GenResult(System.currentTimeMillis() - timestamp3, solution));
        }
        long timestamp4 = System.currentTimeMillis();
        System.out.println("Generated models for " + (5 * factor) + " nodes in " + (timestamp4 - timestamp3) + "ms ");
        computeTimes(timeResults, result, fintessNames, "" + (5 * factor), ploter);
        long timestamp5 = System.currentTimeMillis();
        System.out.println("Computing metrics in " + (timestamp5 - timestamp4) + "ms");
        for (KevoreeFitnessFunction fit : fintessNames.values()) {
            System.out.println(fit.getClass().getSimpleName() + "->" + fit.evaluate(bestModel));
        }

        ploter.setPrefixe("random_" + factor);
        ploter.plotResult(timeResults);

    }


    private static void computeTimes(HashMap<String, LinkedList<Measure>> timeResults, LinkedList<GenResult> results, HashMap<String, KevoreeFitnessFunction> fintessNames, String iteration, SolutionPloter ploter) {

        //For each model, evaluate the fitnesses
        double lastBest = Double.MAX_VALUE;
        double lastWorst = Double.MIN_VALUE;
        LinkedList<Measure> fitnessValues = timeResults.get("meanMax" + iteration);
        LinkedList<Measure> fitnessValues_min = timeResults.get("meanMin" + iteration);

        LinkedList<Measure> fitnessValues_rand = timeResults.get("random" + iteration);

        if (fitnessValues == null) {
            fitnessValues = new LinkedList<Measure>();
            timeResults.put("meanMax", fitnessValues);
        }
        if (fitnessValues_min == null) {
            fitnessValues_min = new LinkedList<Measure>();
            timeResults.put("meanMin", fitnessValues_min);
        }

        if (fitnessValues_rand == null) {
            fitnessValues_rand = new LinkedList<Measure>();
            timeResults.put("random", fitnessValues_rand);
            ploter.pointConfig.put("random", SolutionPloter.PointType.point);
        }

        for (GenResult res : results) {
            double evalOfMean = fintessNames.get("mean").evaluate(res.model);
            if (evalOfMean < lastBest) {
                lastBest = evalOfMean;
                bestModel = res.model;
            }
            if (evalOfMean > lastWorst) {
                lastWorst = evalOfMean;
                worstModel = res.model;
            }
            fitnessValues.addLast(new Measure(res.timestamp, lastBest));
            fitnessValues_min.addLast(new Measure(res.timestamp, lastWorst));

            fitnessValues_rand.addLast(new Measure(res.timestamp, evalOfMean));

        }

        System.out.println("Best Mean:" + lastBest);
        System.out.println("Worst Mean:" + lastWorst);
    }


}
