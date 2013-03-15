package org.kevoree.genetic.cloud.reasoner.fitness;

import org.junit.Test;
import org.kevoree.ComponentInstance;
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
 * Time: 17:25
 */
public class IsolationFitnessTest {

    @Test
    public void testIsolationFitness() {
        CloudPopulationFactory factory = new CloudPopulationFactory();
        List<ContainerRoot> models = factory.createPopulation();
        test(models.get(0), -1, 75.0);
        test(models.get(1), 1, 0.0);
        test(models.get(2), 2, 100.0);
    }

    private void test(ContainerRoot model, int nbComponentPerNode, double expectedResult) {
        KevoreeFactory kfactory = new DefaultKevoreeFactory();
        int i = 0;
        for (ContainerNode n : model.getNodes()) {
            ContainerNode node = kfactory.createContainerNode();
            node.setName("XenCustomerNode_" + i);
            node.setTypeDefinition(model.findTypeDefinitionsByID("XenCustomerNode"));
            model.addNodes(node);
            n.addHosts(node);
            i++;
            if (nbComponentPerNode == -1) {
                for (int i2 = 0; i2 < (i % 2) + 1; i2++) {
                    ComponentInstance cinstance = kfactory.createComponentInstance();
                    cinstance.setTypeDefinition(model.findTypeDefinitionsByID("UserDB"));
                    cinstance.setName("c_" + i2 + "-" + i);
                    node.addComponents(cinstance);
                }
            } else {
                for (int i2 = 0; i2 < nbComponentPerNode; i2++) {
                    ComponentInstance cinstance = kfactory.createComponentInstance();
                    cinstance.setTypeDefinition(model.findTypeDefinitionsByID("UserDB"));
                    cinstance.setName("c_" + i2 + "-" + i);
                    node.addComponents(cinstance);
                }
            }
        }  /*
        IsolationFitness fitness = new IsolationFitness();
        double d = fitness.evaluate(model);
        assert (d == expectedResult);*/
    }


}
