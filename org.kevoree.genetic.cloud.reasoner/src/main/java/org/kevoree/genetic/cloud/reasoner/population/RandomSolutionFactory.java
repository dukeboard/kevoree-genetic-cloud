package org.kevoree.genetic.cloud.reasoner.population;

import org.kevoree.*;
import org.kevoree.genetic.cloud.reasoner.SLAModel;
import org.kevoree.genetic.cloud.reasoner.operators.AddVirtualNodeOperator;
import org.kevoree.genetic.framework.KevoreeMutationOperator;
import org.kevoree.impl.DefaultKevoreeFactory;

import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 15/03/13
 * Time: 11:12
 */
public class RandomSolutionFactory extends CloudPopulationFactory {

    private KevoreeFactory factory = new DefaultKevoreeFactory();
    private Random rand = new Random();
    private SLAModel slaModel;

    public RandomSolutionFactory(SLAModel slaModel) {
        this.slaModel = slaModel;
        this.populationSize = 1;
    }

    public RandomSolutionFactory(SLAModel slaModel, Integer popSize) {
        this.slaModel = slaModel;
        this.populationSize = popSize;
    }


    @Override
    public List<ContainerRoot> createPopulation() {
        List<ContainerRoot> models = super.createPopulation();
        models.set(0, doRandom(models.get(0)));
        return models;
    }

    public ContainerRoot doRandom(ContainerRoot model) {
        KevoreeMutationOperator addVirtualNodeOperator = new AddVirtualNodeOperator().setSelectorQuery("nodes[{ typeDefinition.name = *InfraNode }]");

        for (ContainerNode node : model.getNodes()) {
            int vcpuMax = 0;
            for (DictionaryValue v : node.getTypeDefinition().getDictionaryType().getDefaultValues()) {
                if (v.getAttribute().getName().equals("vcpu")) {
                    vcpuMax += Integer.parseInt(v.getValue());
                }
            }
            if (vcpuMax > 0) {
                int nbVNodes = rand.nextInt(vcpuMax);
                for (int i = 0; i < nbVNodes; i++) {
                    model = addVirtualNodeOperator.mutate(model);
                }
            }
        }

        List<Object> vNodes = model.selectByQuery("nodes[{ typeDefinition.name = *CustomerNode }]");
        int redondency = 5;
        for (String type : slaModel.getTypes()) {
            int typeRed = rand.nextInt(redondency);

            for (int r = 0; r < typeRed; r++) {
                int nodeNum = rand.nextInt(vNodes.size());
                ContainerNode node = (ContainerNode) vNodes.get(nodeNum);
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
