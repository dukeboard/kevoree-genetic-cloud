package org.kevoree.genetic.cloud.reasoner.population;

import org.kevoree.*;
import org.kevoree.impl.DefaultKevoreeFactory;
import org.kevoree.loader.ModelLoader;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 15/03/13
 * Time: 11:12
 */
public class RandomSolutionFactory {

    private KevoreeFactory factory = new DefaultKevoreeFactory();
    private static Integer numberOfInfraNode_lowPower = 3;
    private static Integer numberOfInfraNode_fullPower = 2;
    private Random rand = new Random();

    public void createRandomSolution() {
        ModelLoader loader = new ModelLoader();
        ContainerRoot rootModel = loader.loadModelFromStream(this.getClass().getResourceAsStream("/KEV-INF/lib.kev")).get(0);
        /* Fix Immutable */
        for (TypeDefinition td : rootModel.getTypeDefinitions()) {
            td.setRecursiveReadOnly();
        }
        for (DeployUnit du : rootModel.getDeployUnits()) {
            du.setRecursiveReadOnly();
        }
        for (Repository r : rootModel.getRepositories()) {
            r.setRecursiveReadOnly();
        }
        //Init Infra
        //Fill Customer LowPowerNode
        for (int i = 0; i < numberOfInfraNode_lowPower; i++) {
            ContainerNode node = factory.createContainerNode();
            node.setName("ARMINode_" + i);
            node.setTypeDefinition(rootModel.findTypeDefinitionsByID("ARMInfraNode"));
            rootModel.addNodes(node);
        }
        //Fill Customer FullPowerNode
        for (int i = 0; i < numberOfInfraNode_fullPower; i++) {
            ContainerNode node = factory.createContainerNode();
            node.setName("XeonINode_" + i);
            node.setTypeDefinition(rootModel.findTypeDefinitionsByID("XeonInfraNode"));
            rootModel.addNodes(node);
        }

    }

}
