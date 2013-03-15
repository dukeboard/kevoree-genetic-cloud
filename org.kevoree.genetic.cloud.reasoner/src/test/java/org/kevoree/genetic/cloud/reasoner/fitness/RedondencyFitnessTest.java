package org.kevoree.genetic.cloud.reasoner.fitness;

import org.junit.Test;
import org.kevoree.*;
import org.kevoree.genetic.cloud.reasoner.population.CloudPopulationFactory;
import org.kevoree.impl.DefaultKevoreeFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 13/03/13
 * Time: 09:15
 */
public class RedondencyFitnessTest {

    private RedondencyFitness fitness = new RedondencyFitness();
    /*
    @Test
    public void testRedondency() {

        fitness = fitness.addType("ItemDB").addType("LoadBalancer").addType("PaymentDB").addType("UserDB").addType("WebFrontend");


        CloudPopulationFactory factory = new CloudPopulationFactory();
        List<ContainerRoot> models = factory.createPopulation();
        ContainerRoot model = models.get(0);
        double d = fitness.evaluate(model);
        assert (d == 100.0d); //Check bad score because no redondency
        populateComponentOnce(model);
        double d2 = fitness.evaluate(model);
        assert (d2 <67d && d2 >66); //Check bad score because no redondency
    }

    public void populateComponentOnce(ContainerRoot model) {
        KevoreeFactory kfactory = new DefaultKevoreeFactory();
        int i = 0;
        for (ContainerNode n : model.getNodes()) {
            ContainerNode node = kfactory.createContainerNode();
            node.setName("XenCustomerNode_" + i);
            node.setTypeDefinition(model.findTypeDefinitionsByID("XenCustomerNode"));
            model.addNodes(node);
            n.addHosts(node);
            i++;
            for (TypeDefinition td : model.getTypeDefinitions()) {
                if (td instanceof ComponentType) {
                    ComponentInstance cinstance = kfactory.createComponentInstance();
                    cinstance.setTypeDefinition(td);
                    cinstance.setName(td.getName() + "_c_" + "-" + i);
                    node.addComponents(cinstance);
                }
            }
        }
    }  */

}
