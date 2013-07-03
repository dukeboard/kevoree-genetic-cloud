package org.kevoree.genetic.cloud.reasoner;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 16/03/13
 * Time: 19:23
 */
public class RunnerCompositeOptimized {

    public static void main(String[] args) throws Exception {
        RunnerOptimizeConsumption.compositeFitness=true;
        RunnerOptimizeConsumption.prefixe = "composite";
        RunnerOptimizeConsumption.scaleFactor = 5d;
        RunnerOptimizeConsumption.main(args);

    }
}