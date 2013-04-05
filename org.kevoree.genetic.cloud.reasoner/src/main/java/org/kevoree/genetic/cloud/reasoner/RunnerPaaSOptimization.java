package org.kevoree.genetic.cloud.reasoner;

import org.kevoree.genetic.cloud.library.onlineStore.*;
import org.kevoree.genetic.cloud.reasoner.fitness.*;
import org.kevoree.genetic.cloud.reasoner.operators.AddRandomComponentOperator;
import org.kevoree.genetic.cloud.reasoner.plot.SolutionPloter;
import org.kevoree.genetic.cloud.reasoner.population.EmptyPopulationFactory;
import org.kevoree.genetic.framework.KevoreeGeneticEngine;
import org.kevoree.genetic.framework.KevoreeSolution;
import org.kevoree.genetic.library.operator.*;

import java.util.List;

public class RunnerPaaSOptimization {

    public static String prefixe = "flat";

    public static void main(String[] args) throws Exception {

        SLAModel SLAModel = new SLAModel();
        SLAModel.putVCPULoad(ItemDB.class.getSimpleName(), 1.2); //ItemDB  need 2Ghz
        SLAModel.putVCPULoad(LoadBalancer.class.getSimpleName(), 0.4); //LoadBalancer  need 0.3Ghz
        SLAModel.putVCPULoad(PaymentDB.class.getSimpleName(), 0.6); //PaymentDB  need 0.6Ghz
        SLAModel.putVCPULoad(UserDB.class.getSimpleName(), 0.4); //UserDB  need 0.4Ghz
        SLAModel.putVCPULoad(WebFrontend.class.getSimpleName(), 1.2); //WebFrontend  need 2Ghz

        SLAModel.setMaxCost(15d);

        SLAModel.putSecurityLevel(ItemDB.class.getSimpleName(), 2.0);
        SLAModel.putSecurityLevel(LoadBalancer.class.getSimpleName(), 0.0);
        SLAModel.putSecurityLevel(PaymentDB.class.getSimpleName(), 4.0);
        SLAModel.putSecurityLevel(UserDB.class.getSimpleName(), 3.0);
        SLAModel.putSecurityLevel(WebFrontend.class.getSimpleName(), 1.0);


        KevoreeGeneticEngine engine = new KevoreeGeneticEngine().setPopulationFactory(new EmptyPopulationFactory());

        /* Add a random component and associate one a random node */
        AddRandomComponentOperator operator = new AddRandomComponentOperator();
        operator.setSelectorQuery("nodes[*]");
        operator.setSlaModel(SLAModel);
        engine.addOperator(operator);
        /* Add a node to the root container */
        engine.addOperator(new AddNodeOperator().setNodeTypeName("XenCustomerNode").setSuccessor(new AddRandomComponentOperator().setSlaModel(SLAModel)));
        /* Delete a random node */
        engine.addOperator(new RemoveNodeOperator().setSelectorQuery("nodes[*]").setSelectionStrategy(AbstractKevoreeOperator.TargetSelectionStrategy.random));
        /* Random remove a component */
        engine.addOperator(new RemoveComponentOperator().setSelectorQuery("nodes[*]/components[*]"));
        /* Random move a component from a node to another */
        engine.addOperator(new MoveComponentOperator().setTargetNodesQuery("nodes[*]").setSelectorQuery("nodes[*]/components[*]"));

        /* Configure fitness */
        engine.addFitnessFuntion(new CompletenessFitness().setSlaModel(SLAModel));
        engine.addFitnessFuntion(new CostFitness().setSlaModel(SLAModel));
        engine.addFitnessFuntion(new SecurityFitness().setSlaModel(SLAModel));
        engine.addFitnessFuntion(new OverloadFitness());
        engine.addFitnessFuntion(new SLAPerformanceFitness().setSlaModel(SLAModel));
        //engine.addFitnessFuntion(new DistanceFitness());

        engine.setMaxGeneration(2000);
        engine.setDominanceDelta(0.5d);
        SolutionPloter ploter = new SolutionPloter();
        ploter.setPrefixe(prefixe);
        engine.setInstrument(ploter);

        long currentTime = System.currentTimeMillis();
        List<KevoreeSolution> result = engine.solve();
        System.out.println("Found solutions in " + (System.currentTimeMillis() - currentTime) + " ms");
        SolutionFilter filter = new SolutionFilter();

        ploter.plotResults();

        for (KevoreeSolution solution : filter.order(filter.filterSolution(result))) {
            solution.print(System.out);
        }
    }
}
