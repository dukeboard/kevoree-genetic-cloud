package org.kevoree.genetic.cloud.reasoner;

import org.kevoree.genetic.cloud.library.onlineStore.*;
import org.kevoree.genetic.cloud.reasoner.fitness.*;
import org.kevoree.genetic.cloud.reasoner.operators.AddVirtualNodeOperator;
import org.kevoree.genetic.cloud.reasoner.operators.AddRandomComponentOperator;
import org.kevoree.genetic.cloud.reasoner.operators.MoveVirtualNodeOperator;
import org.kevoree.genetic.cloud.reasoner.plot.SolutionPloter;
import org.kevoree.genetic.cloud.reasoner.population.CloudPopulationFactory;
import org.kevoree.genetic.framework.KevoreeCompositeFitnessFunction;
import org.kevoree.genetic.framework.KevoreeFitnessFunction;
import org.kevoree.genetic.framework.KevoreeGeneticEngine;
import org.kevoree.genetic.framework.KevoreeSolution;
import org.kevoree.genetic.library.operator.RemoveChildNodeOperator;
import org.kevoree.genetic.library.operator.RemoveComponentOperator;

import java.util.List;

public class RunnerOptimizeConsumption {

    public static Boolean compositeFitness = false;

    public static Double scaleFactor = 12d;

    public static String prefixe = "multi";

    public static void main(String[] args) throws Exception {

        SLAModel SLAModel = new SLAModel();
        SLAModel.putVCPULoad(ItemDB.class.getSimpleName(), 1.2); //ItemDB  need 2Ghz
        SLAModel.putVCPULoad(LoadBalancer.class.getSimpleName(), 0.4); //LoadBalancer  need 0.3Ghz
        SLAModel.putVCPULoad(PaymentDB.class.getSimpleName(), 0.6); //PaymentDB  need 0.6Ghz
        SLAModel.putVCPULoad(UserDB.class.getSimpleName(), 0.4); //UserDB  need 0.4Ghz
        SLAModel.putVCPULoad(WebFrontend.class.getSimpleName(), 1.2); //WebFrontend  need 2Ghz

        SLAModel = SLAModel.scale(scaleFactor);

        SLAModel.putSecurityLevel(ItemDB.class.getSimpleName(), 2.0);
        SLAModel.putSecurityLevel(LoadBalancer.class.getSimpleName(), 0.0);
        SLAModel.putSecurityLevel(PaymentDB.class.getSimpleName(), 4.0);
        SLAModel.putSecurityLevel(UserDB.class.getSimpleName(), 3.0);
        SLAModel.putSecurityLevel(WebFrontend.class.getSimpleName(), 1.0);

        KevoreeGeneticEngine engine = new KevoreeGeneticEngine()
                .setPopulationFactory(new CloudPopulationFactory().scale(scaleFactor.intValue()));

        /* Configure operator */
        AddRandomComponentOperator operator = new AddRandomComponentOperator();
        operator.setSelectorQuery("nodes[{ typeDefinition.name = *CustomerNode }]");
        operator.setSlaModel(SLAModel);
        engine.addOperator(operator);

        engine.addOperator(new AddVirtualNodeOperator().setSelectorQuery("nodes[{ typeDefinition.name = *InfraNode }]").setSuccessor(new AddRandomComponentOperator().setSlaModel(SLAModel)));
        engine.addOperator(new RemoveComponentOperator().setSelectorQuery("nodes[*]/hosts[*]/components[*]"));
        engine.addOperator(new RemoveChildNodeOperator().setSelectorQuery("nodes[{ typeDefinition.name = *CustomerNode }]"));
        engine.addOperator(new MoveVirtualNodeOperator().setTargetNodesQuery("nodes[{ typeDefinition.name = *InfraNode }]").setSelectorQuery("nodes[{ typeDefinition.name = *CustomerNode }]"));

        /* Configure fitness */


        if(compositeFitness){
            KevoreeCompositeFitnessFunction composite = new KevoreeCompositeFitnessFunction();
            composite.addFitness(new ConsumptionFitness());
            composite.addFitness(new CompletenessFitness().setSlaModel(SLAModel));
            composite.addFitness(new SecurityFitness().setSlaModel(SLAModel));
            composite.addFitness(new OverloadFitness());
            composite.addFitness(new SLAPerformanceFitness().setSlaModel(SLAModel));
            engine.addFitnessFuntion(composite);
        } else {
            engine.addFitnessFuntion(new ConsumptionFitness());
            engine.addFitnessFuntion(new CompletenessFitness().setSlaModel(SLAModel));
            engine.addFitnessFuntion(new SecurityFitness().setSlaModel(SLAModel));
            engine.addFitnessFuntion(new OverloadFitness());
            engine.addFitnessFuntion(new SLAPerformanceFitness().setSlaModel(SLAModel));
        }




        engine.setMaxGeneration(2000);
        SolutionPloter ploter = new SolutionPloter().setBestSolutionNumber(1);
        ploter.setPrefixe(prefixe);
        engine.setInstrument(ploter);

        long currentTime = System.currentTimeMillis();
        List<KevoreeSolution> result = engine.solve();
        System.out.println("Found solutions in " + (System.currentTimeMillis() - currentTime) + " ms");
        SolutionFilter filter = new SolutionFilter();

        ploter.plotResults();

        if(compositeFitness){
            KevoreeCompositeFitnessFunction composite = new KevoreeCompositeFitnessFunction();
            composite.addFitness(new ConsumptionFitness());
            composite.addFitness(new CompletenessFitness().setSlaModel(SLAModel));
            composite.addFitness(new SecurityFitness().setSlaModel(SLAModel));
            composite.addFitness(new OverloadFitness());
            composite.addFitness(new SLAPerformanceFitness().setSlaModel(SLAModel));
            for (KevoreeSolution solution : filter.order(result)) {
                for (KevoreeFitnessFunction fit : composite.getFitnesses()) {
                    System.out.println(fit.getClass().getSimpleName()+"->"+fit.evaluate(solution.getModel()));
                }
                solution.print(System.out);
            }
        } else {
            for (KevoreeSolution solution : filter.order(filter.filterSolution(result))) {
                solution.print(System.out);
            }
        }

    }


}
