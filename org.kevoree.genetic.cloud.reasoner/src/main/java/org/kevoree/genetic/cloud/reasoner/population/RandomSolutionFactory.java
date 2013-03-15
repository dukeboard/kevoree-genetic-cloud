package org.kevoree.genetic.cloud.reasoner.population;

import org.kevoree.*;
import org.kevoree.genetic.cloud.reasoner.operators.AddVirtualNodeOperator;
import org.kevoree.genetic.framework.KevoreeMutationOperator;
import org.kevoree.impl.DefaultKevoreeFactory;
import org.kevoree.loader.ModelLoader;

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
    private static Integer numberOfInfraNode_lowPower = 3;
    private static Integer numberOfInfraNode_fullPower = 2;
    private Random rand = new Random();


    private List<String> types = new ArrayList<String>();

    public RandomSolutionFactory addType(String t) {
        types.add(t);
        return this;
    }

    public RandomSolutionFactory setAllTypes(List<String> _types) {
        types = _types;
        return this;
    }

    public List<String> getAllTypes(){
        return types;
    }




    public ContainerRoot createRandomSolution() {
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

        int vcpuMax = 0;

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

        KevoreeMutationOperator addVirtualNodeOperator = new AddVirtualNodeOperator().setSelectorQuery("nodes[{ typeDefinition.name = *InfraNode }]");
        int nbVNodes = rand.nextInt(vcpuMax);
        for(int i = 0; i < nbVNodes ; i++) {
            rootModel = addVirtualNodeOperator.mutate(rootModel);
        }

        List<Object> vNodes = rootModel.selectByQuery("nodes[{ typeDefinition.name = *CustomerNode }");
        int redondency = 5;
        for(String type : types) {
            int typeRed = rand.nextInt(redondency);

            for(int r = 0; r < typeRed; r++) {
                ContainerNode node = (ContainerNode)vNodes.get(rand.nextInt(vNodes.size()));
                TypeDefinition td = rootModel.findTypeDefinitionsByID(type);
                if (td != null) {
                    ComponentInstance inst = factory.createComponentInstance();
                    inst.setName(type + "_" + rand.nextInt());
                    inst.setTypeDefinition(td);
                    node.addComponents(inst);
                }
            }
        }

        return rootModel;

    }

}
