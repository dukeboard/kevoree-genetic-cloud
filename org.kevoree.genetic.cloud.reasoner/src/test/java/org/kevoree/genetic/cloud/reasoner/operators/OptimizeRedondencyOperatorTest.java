package org.kevoree.genetic.cloud.reasoner.operators;

import org.junit.Test;
import org.kevoree.ContainerNode;
import org.kevoree.ContainerRoot;
import org.kevoree.KevoreeFactory;
import org.kevoree.genetic.cloud.reasoner.fitness.RedondencyFitness;
import org.kevoree.genetic.cloud.reasoner.population.CloudPopulationFactory;
import org.kevoree.impl.DefaultKevoreeFactory;
import org.kevoree.serializer.ModelSerializer;

import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 13/03/13
 * Time: 12:52
 */
public class OptimizeRedondencyOperatorTest {


    @Test
    public void testOptimizeRedondencyOperator() {

        CloudPopulationFactory factory = new CloudPopulationFactory();
        List<ContainerRoot> models = factory.createPopulation();
        ContainerRoot model = models.get(0);
        KevoreeFactory kfactory = new DefaultKevoreeFactory();
        Random rand = new Random();

        for (ContainerNode n : model.getNodes()) {
            ContainerNode node = kfactory.createContainerNode();
            node.setName("XenCustomerNode_"+rand.nextInt());
            node.setTypeDefinition(model.findTypeDefinitionsByID("XenCustomerNode"));
            model.addNodes(node);
            n.addHosts(node);
        }

        OptimizeRedondencyOperator operator = new OptimizeRedondencyOperator();
        operator.addType("ItemDB").addType("LoadBalancer").addType("PaymentDB").addType("UserDB").addType("WebFrontend");
        operator.setSelectorQuery("nodes[{ typeDefinition.name = *CustomerNode }]");

        model = test(operator,model,1);
        model = test(operator, model, 2);
        model = test(operator, model, 3);
        model = test(operator, model, 4);
        model = test(operator, model, 5);

        RedondencyFitness fit = new RedondencyFitness();
        double d = fit.evaluate(model);
        assert(d==0.0d);

    }

    public ContainerRoot test(OptimizeRedondencyOperator operator,ContainerRoot model,Integer nb){
        ContainerRoot model2 = model;
        for (int i = 0; i < (operator.getAllTypes().size()); i++) {
            model2 = operator.mutate(model2);
        }
        for(String type : operator.getAllTypes()){
            List<Object> components = model2.selectByQuery("nodes[{name=*}]/hosts[{name=*}]/components[{typeDefinition.name = "+type+" }]");
            System.out.println(type+"-"+components.size());
            assert (components.size() == nb);
        }
        return model2;
    }

}
