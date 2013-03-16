package org.kevoree.genetic.cloud.reasoner.operators;

import org.kevoree.*;
import org.kevoree.genetic.cloud.reasoner.util.PropertyCachedResolver;
import org.kevoree.genetic.library.operator.AbstractKevoreeOperator;
import org.kevoree.impl.DefaultKevoreeFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 12/03/13
 * Time: 15:21
 */
public class AddVirtualNodeOperator extends AbstractKevoreeOperator {

    protected Random rand = new Random();
    private KevoreeFactory factory = new DefaultKevoreeFactory();

    private final String nbSubNodes = "vcpu";
    private PropertyCachedResolver resolver = new PropertyCachedResolver();

    @Override
    protected void applyMutation(Object o, ContainerRoot containerRoot) {
        ContainerNode node = factory.createContainerNode();
        node.setTypeDefinition(containerRoot.findTypeDefinitionsByID("XenCustomerNode"));
        node.setName("cust" + rand.nextInt());
        ContainerNode parent = (ContainerNode) o;
        parent.addHosts(node);
        containerRoot.addNodes(node);

       //((AbstractKevoreeOperator)getSuccessor()).setSelectorQuery("nodes[{ typeDefinition.name = *CustomerNode }]");
        ((AbstractKevoreeOperator)getSuccessor()).setSelectorQuery(node.path());


    }

    protected List<Object> selectTarget(ContainerRoot root, String query) {
        List<Object> nodeList = root.selectByQuery(query);
        List<Object> selectedNodes = new ArrayList<Object>();
        for (Object n : nodeList) {
            ContainerNode node = (ContainerNode) n;
            if (node.getHosts().size() < resolver.getDefault(node, nbSubNodes)) {
                selectedNodes.add(node);
            }
        }
        return selectedNodes;
    }

}
