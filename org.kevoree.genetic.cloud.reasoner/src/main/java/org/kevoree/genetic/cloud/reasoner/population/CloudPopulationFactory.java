package org.kevoree.genetic.cloud.reasoner.population;

import org.kevoree.*;
import org.kevoree.cloner.ModelCloner;
import org.kevoree.genetic.framework.KevoreePopulationFactory;
import org.kevoree.impl.DefaultKevoreeFactory;
import org.kevoree.loader.ModelLoader;
import org.kevoree.loader.XMIModelLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CloudPopulationFactory implements KevoreePopulationFactory {

    private Integer numberOfInfraNode_lowPower = 3;
    private Integer numberOfInfraNode_fullPower = 2;

    protected Integer populationSize = 10;

    public CloudPopulationFactory scale(int factor) {
        numberOfInfraNode_lowPower = 3 * factor;
        numberOfInfraNode_fullPower = 2 * factor;
        return this;
    }

    @Override
    public List<ContainerRoot> createPopulation() {
        ArrayList<ContainerRoot> population = new ArrayList<ContainerRoot>();
        KevoreeFactory factory = new DefaultKevoreeFactory();
        ModelLoader loader = new XMIModelLoader();
        ModelCloner cloner = new ModelCloner();
        ContainerRoot rootModel = (ContainerRoot) loader.loadModelFromStream(this.getClass().getResourceAsStream("/KEV-INF/lib.kev")).get(0);
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

        //Fill the whole population
        for (int i = 0; i < populationSize; i++) {
            population.add(cloner.cloneMutableOnly(rootModel, false));
        }
        return population;
    }
}
