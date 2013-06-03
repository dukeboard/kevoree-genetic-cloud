package org.kevoree.genetic.cloud.reasoner.population;

import org.kevoree.*;
import org.kevoree.cloner.ModelCloner;
import org.kevoree.genetic.cloud.reasoner.SLAModel;
import org.kevoree.genetic.cloud.reasoner.operators.AddVirtualNodeOperator;
import org.kevoree.genetic.framework.KevoreeMutationOperator;
import org.kevoree.impl.DefaultKevoreeFactory;
import org.kevoree.loader.ModelLoader;
import org.kevoree.loader.XMIModelLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 15/03/13
 * Time: 11:12
 */
public class RandomSolutionFactory {

    private KevoreeFactory factory = new DefaultKevoreeFactory();
    private Integer numberOfInfraNode_lowPower = 3;
    private Integer numberOfInfraNode_fullPower = 2;
    private Random rand = new Random();
    private ContainerRoot rootModel;
    private int vcpuMax = 0;
    private SLAModel slaModel;

    public RandomSolutionFactory(SLAModel slaModel) {
        this.slaModel = slaModel;
    }


    public void setNumberOfInfraNode_lowPower(Integer numberOfInfraNode_lowPower) {
       this.numberOfInfraNode_lowPower = numberOfInfraNode_lowPower;
    }

    public void setNumberOfInfraNode_fullPower(Integer numberOfInfraNode_fullPower) {
        this.numberOfInfraNode_fullPower = numberOfInfraNode_fullPower;
    }

    public void createBaseModel() {
        ModelLoader loader = new XMIModelLoader();
        rootModel = (ContainerRoot) loader.loadModelFromStream(this.getClass().getResourceAsStream("/KEV-INF/lib.kev")).get(0);
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
        System.out.println("Generation of light nodes");
        for (int i = 0; i < numberOfInfraNode_lowPower; i++) {
            ContainerNode node = factory.createContainerNode();
            node.setName("ARMINode_" + i);
            TypeDefinition td = rootModel.findTypeDefinitionsByID("ARMInfraNode");
            for (DictionaryValue v : td.getDictionaryType().getDefaultValues()) {
                if (v.getAttribute().getName().equals("vcpu")) {
                    vcpuMax += Integer.parseInt(v.getValue());
                }
            }
            node.setTypeDefinition(td);
            rootModel.addNodes(node);
        }
        System.out.println("Generation of full power nodes");
        //Fill Customer FullPowerNode
        for (int i = 0; i < numberOfInfraNode_fullPower; i++) {
            ContainerNode node = factory.createContainerNode();
            node.setName("XeonINode_" + i);
            TypeDefinition td = rootModel.findTypeDefinitionsByID("XeonInfraNode");
            for (DictionaryValue v : td.getDictionaryType().getDefaultValues()) {
                if (v.getAttribute().getName().equals("vcpu")) {
                    vcpuMax += Integer.parseInt(v.getValue());
                }
            }
            node.setTypeDefinition(td);
            rootModel.addNodes(node);
        }
    }


    public ContainerRoot createRandomSolution() {
        ModelCloner cloner = new ModelCloner();
        ContainerRoot model = cloner.cloneMutableOnly(rootModel, false);

        KevoreeMutationOperator addVirtualNodeOperator = new AddVirtualNodeOperator().setSelectorQuery("nodes[{ typeDefinition.name = *InfraNode }]");
        int nbVNodes = 1 + rand.nextInt(vcpuMax-1);
        for(int i = 0; i < nbVNodes ; i++) {
            model = addVirtualNodeOperator.mutate(model);
        }

        List<Object> vNodes = model.selectByQuery("nodes[{ typeDefinition.name = *CustomerNode }]");
        int redondency = 5;
        for(String type : slaModel.getTypes()) {
            int typeRed = rand.nextInt(redondency);

            for(int r = 0; r < typeRed; r++) {
                int nodeNum = rand.nextInt(vNodes.size());
                ContainerNode node = (ContainerNode)vNodes.get(nodeNum);
                TypeDefinition td = model.findTypeDefinitionsByID(type);
                if (td != null) {
                    ComponentInstance inst = factory.createComponentInstance();
                    inst.setName(type + "_" + rand.nextInt());
                    inst.setTypeDefinition(td);
                    node.addComponents(inst);
                }
            }
        }

        return model;

    }

}
