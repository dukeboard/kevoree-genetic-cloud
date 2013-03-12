package org.kevoree.genetic.cloud.reasoner.fitness;

import org.junit.Test;
import org.kevoree.ContainerNode;
import org.kevoree.ContainerRoot;
import org.kevoree.KevoreeFactory;
import org.kevoree.genetic.cloud.reasoner.population.CloudPopulationFactory;
import org.kevoree.impl.DefaultKevoreeFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 12/03/13
 * Time: 10:09
 */
public class ConsumptionFitnessTest {

    @Test
    public void testConsumption() {
        CloudPopulationFactory factory = new CloudPopulationFactory();
        KevoreeFactory kfactory = new DefaultKevoreeFactory();
        List<ContainerRoot> models = factory.createPopulation();
        ConsumptionFitness fitness = new ConsumptionFitness();
        ContainerRoot model = models.get(0);
        double d = fitness.evaluate(model);
        assert (d == 0.0d);
          /* Fill all IaaSNode with subNodes */
        for (ContainerNode n : model.getNodes()) {
            ContainerNode node = kfactory.createContainerNode();
            node.setName("XenCustomerNode");
            node.setTypeDefinition(model.findTypeDefinitionsByID("XenCustomerNode"));
            model.addNodes(node);
            n.addHosts(node);
        }
        double d2 = fitness.evaluate(model);
        assert (d2 == 100.0d);
        /* Remove half of the customer nodes */
        int i = 0;
        for (ContainerNode n : model.getNodes()) {
            if (!n.getHosts().isEmpty()) {
                i = i + 1;
                if (i % 2 != 0) {
                    n.removeAllHosts();
                }
            }
        }
        double d3 = fitness.evaluate(model);
        assert (d3 < 55d && d3 > 50d);
    }

}
