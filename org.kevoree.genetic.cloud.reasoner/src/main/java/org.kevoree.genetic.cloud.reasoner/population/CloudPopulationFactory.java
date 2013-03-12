package org.kevoree.genetic.cloud.reasoner.population;

import org.kevoree.*;
import org.kevoree.cloner.ModelCloner;
import org.kevoree.genetic.framework.KevoreePopulationFactory;
import org.kevoree.impl.DefaultKevoreeFactory;
import org.kevoree.loader.ModelLoader;

import java.util.ArrayList;
import java.util.List;

public class CloudPopulationFactory implements KevoreePopulationFactory {

    private static Integer numberOfInfraNode_lowPower = 3;
    private static Integer numberOfInfraNode_fullPower = 2;

    private static Integer numberOfCustomerNode = 5;
    private static Integer numberOfSoftwareComp = 4;
    private final String propertyName = "CPU_FREQUENCY";

    @Override
    public List<ContainerRoot> createPopulation() {
        ArrayList<ContainerRoot> population = new ArrayList<ContainerRoot>();
        KevoreeFactory factory = new DefaultKevoreeFactory();
        ModelLoader loader = new ModelLoader();
        ModelCloner cloner = new ModelCloner();
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
        for (int i = 0; i < 10; i++) {
            population.add(cloner.cloneMutableOnly(rootModel, false));
        }
        return population;
    }
      /*


    @Override
    public List<ContainerRoot> createPopulation() {
        ArrayList<ContainerRoot> population = new ArrayList<ContainerRoot>();
        KevoreeFactory factory = new DefaultKevoreeFactory();
        ModelLoader loader = new ModelLoader();
        ModelCloner cloner = new ModelCloner();
        //CReate init Model
        //ContainerRoot rootModel = factory.createContainerRoot();


        Bootstraper bs = new NodeTypeBootstrapHelper();
        ContainerRoot initModel = null;
        try {
            initModel = initInfrastructureModel(bs);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            initModel = populateCustomerNode(initModel, bs);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //Add to population
        for (int i = 0; i < 10; i++) {
            population.add(cloner.clone(initModel));
        }
        return population;
    }


    private static ContainerRoot populateCustomerNode(ContainerRoot model,Bootstraper bs) throws Exception {
        KevScriptOfflineEngine kevScriptEngine = new KevScriptOfflineEngine(model,bs);

        Random randGenerator = new Random(System.currentTimeMillis());

        for (int i = 0; i < numberOfInfraNode; i++) {
            for (int j = 0; j < numberOfCustomerNode; j++) {
                String subNodeName;
                if (randGenerator.nextInt(2) == 0){
                    subNodeName = "XenCustNode"+i+j;
                    kevScriptEngine.append("addNode XenCustNode"+i+j+":XenCustomerNode");
                    kevScriptEngine.append("updateDictionary XenCustNode"+i+j+" { owner=\"Arash\" }");
                    kevScriptEngine.append("updateDictionary XenCustNode"+i+j+" { maxComponentCount=\"10\" }");

                    InfrastructureInfoHelper infraInfoHelper = InfrastructureInfoHelper.getInstance();
                    String[] compoNameList = infraInfoHelper.getComponentTypeArray();

                    numberOfSoftwareComp = randGenerator.nextInt(64);
                    for (int h = 0; h < numberOfSoftwareComp; h++) {
                        String compType = compoNameList[randGenerator.nextInt(compoNameList.length)];
                        kevScriptEngine.append("addComponent softComp_"+i+j+h+"@"+subNodeName+" : "+compType);
                        kevScriptEngine.append("updateDictionary softComp_"+i+j+h+" { type=\""+compType+"\" }");
                    }
                }
                else {
                    subNodeName = "VMWareCustNode"+i+j;
                    kevScriptEngine.append("addNode VMWareCustNode"+i+j+":VMWareCustomerNode");
                    kevScriptEngine.append("updateDictionary VMWareCustNode"+i+j+" { owner=\"Arash\" }");
                    kevScriptEngine.append("updateDictionary VMWareCustNode"+i+j+" { maxComponentCount=\"10\" }");

                    InfrastructureInfoHelper infraInfoHelper = InfrastructureInfoHelper.getInstance();
                    String[] compoNameList = infraInfoHelper.getComponentTypeArray();

                    numberOfSoftwareComp = randGenerator.nextInt(64);
                    for (int h = 0; h < numberOfSoftwareComp; h++) {
                        String compType = compoNameList[randGenerator.nextInt(compoNameList.length)];
                        kevScriptEngine.append("addComponent softComp"+compType+"_"+i+j+h+"@"+subNodeName+" : "+compType);
                    }
                }
                if (randGenerator.nextInt(2) == 0){
                    kevScriptEngine.append("addChild "+subNodeName+"@ARMINode"+i);
                } else {
                    kevScriptEngine.append("addChild "+subNodeName+"@XeonINode"+i);
                }
            }
        }

        return kevScriptEngine.interpret();


    } */
}
