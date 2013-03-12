package org.kevoree.genetic.cloud.reasoner.operators;

import org.junit.Test;
import org.kevoree.ContainerNode;
import org.kevoree.ContainerRoot;
import org.kevoree.genetic.cloud.reasoner.org.kevoree.genetic.cloud.operators.AddVirtualNodeOperator;
import org.kevoree.genetic.cloud.reasoner.population.CloudPopulationFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 12/03/13
 * Time: 15:50
 */
public class AddVirtualNodeOperatorTest {

    @Test
    public void testAddVirtualNodeOperator() {
        CloudPopulationFactory factory = new CloudPopulationFactory();
        List<ContainerRoot> models = factory.createPopulation();
        AddVirtualNodeOperator addVirtual = new AddVirtualNodeOperator();
        addVirtual.setSelectorQuery("nodes[{ typeDefinition.name = *InfraNode }]");

        ContainerRoot model = models.get(0);

        int nbNodes = model.getNodes().size();
        for (int i = 0; i < 19; i++) {
            model = addVirtual.mutate(model);
            for (Object n : model.selectByQuery("nodes[{ typeDefinition.name = *InfraNode }]")) {
                ContainerNode node = (ContainerNode) n;
                System.out.print("/" + node.getHosts().size());
            }
            System.out.println();
            assert (nbNodes + 1 == model.getNodes().size());  //check that the operator add a virtual node
            nbNodes++;
        }

        int nbNodes2 = model.getNodes().size();
        for (int i = 0; i < 10; i++) {
            model = addVirtual.mutate(model);
            assert (nbNodes2 == model.getNodes().size());  //check that the operator does not add a virtual node
        }

    }

}
