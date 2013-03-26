package org.kevoree.genetic.cloud.reasoner;

import org.kevoree.genetic.cloud.library.onlineStore.*;
import org.kevoree.genetic.cloud.reasoner.fitness.*;
import org.kevoree.genetic.cloud.reasoner.operators.AddRandomComponentOperatorNoOverLoad;
import org.kevoree.genetic.cloud.reasoner.operators.AddVirtualNodeOperator;
import org.kevoree.genetic.cloud.reasoner.operators.MoveVirtualNodeOperator;
import org.kevoree.genetic.cloud.reasoner.plot.SolutionPloter;
import org.kevoree.genetic.cloud.reasoner.population.CloudPopulationFactory;
import org.kevoree.genetic.framework.KevoreeCompositeFitnessFunction;
import org.kevoree.genetic.framework.KevoreeFitnessFunction;
import org.kevoree.genetic.framework.KevoreeGeneticEngine;
import org.kevoree.genetic.framework.KevoreeSolution;
import org.kevoree.genetic.library.operator.RemoveChildNode;
import org.kevoree.genetic.library.operator.RemoveComponent;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 16/03/13
 * Time: 18:27
 */
public class RunnerMonoSlaPerformance {


    public static void main(String[] args) throws Exception {

        SLAModel SLAModel = new SLAModel();
        SLAModel.putVCPULoad(ItemDB.class.getSimpleName(), 1.2); //ItemDB  need 2Ghz
        SLAModel.putVCPULoad(LoadBalancer.class.getSimpleName(), 0.4); //LoadBalancer  need 0.3Ghz
        SLAModel.putVCPULoad(PaymentDB.class.getSimpleName(), 0.6); //PaymentDB  need 0.6Ghz
        SLAModel.putVCPULoad(UserDB.class.getSimpleName(), 0.4); //UserDB  need 0.4Ghz
        SLAModel.putVCPULoad(WebFrontend.class.getSimpleName(), 1.2); //WebFrontend  need 2Ghz

        KevoreeGeneticEngine engine = new KevoreeGeneticEngine()
                .setPopulationFactory(new CloudPopulationFactory());

        /* Configure operator */
        AddRandomComponentOperatorNoOverLoad operator = new AddRandomComponentOperatorNoOverLoad();
        operator.setSelectorQuery("nodes[{ typeDefinition.name = *CustomerNode }]");
        operator.setSlaModel(SLAModel);
        engine.addOperator(operator);

        engine.addOperator(new AddVirtualNodeOperator().setSelectorQuery("nodes[{ typeDefinition.name = *InfraNode }]").setSuccessor(new AddRandomComponentOperatorNoOverLoad().setSlaModel(SLAModel)));
        engine.addOperator(new RemoveComponent().setSelectorQuery("nodes[*]/hosts[*]/components[*]"));
        engine.addOperator(new RemoveChildNode().setSelectorQuery("nodes[{ typeDefinition.name = *CustomerNode }]"));
        engine.addOperator(new MoveVirtualNodeOperator().setTargetNodesQuery("nodes[{ typeDefinition.name = *InfraNode }]").setSelectorQuery("nodes[{ typeDefinition.name = *CustomerNode }]"));

        /* Configure fitness */
        engine.addFitnessFuntion(new SLAPerformanceFitness().setSlaModel(SLAModel));

        engine.setMaxGeneration(1000);
        SolutionPloter ploter = new SolutionPloter();
        engine.setInstrument(ploter);

        long currentTime = System.currentTimeMillis();
        List<KevoreeSolution> result = engine.solve();
        System.out.println("Found solutions in " + (System.currentTimeMillis() - currentTime) + " ms");
        SolutionFilter filter = new SolutionFilter();

        ploter.plotResults();

        KevoreeCompositeFitnessFunction composite = new KevoreeCompositeFitnessFunction();
        composite.addFitness(new CompletenessFitness().setSlaModel(SLAModel));
        composite.addFitness(new ConsumptionFitness());
        composite.addFitness(new OverloadFitness());
        composite.addFitness(new SecurityFitness());

        for (KevoreeSolution solution : filter.order(result)) {
            for (KevoreeFitnessFunction fit : composite.getFitnesses()) {
                 System.out.println(fit.getClass().getSimpleName()+"->"+fit.evaluate(solution.getModel()));
            }
            solution.print(System.out);
        }


    }


}
