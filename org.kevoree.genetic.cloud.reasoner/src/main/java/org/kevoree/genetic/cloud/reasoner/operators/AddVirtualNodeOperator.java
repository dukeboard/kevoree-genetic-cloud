package org.kevoree.genetic.cloud.reasoner.operators;

import org.kevoree.*;
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

    private final String nbSubNodes = "vcpu";
    private HashMap<TypeDefinition, Integer> vCPUCache = new HashMap<TypeDefinition, Integer>();
    protected Random rand = new Random();
    private KevoreeFactory factory = new DefaultKevoreeFactory();

    @Override
    protected void applyMutation(Object o, ContainerRoot containerRoot) {
        ContainerNode node = factory.createContainerNode();
        node.setTypeDefinition(containerRoot.findTypeDefinitionsByID("XenCustomerNode"));
        node.setName("cust" + rand.nextInt());
        ContainerNode parent = (ContainerNode) o;
        parent.addHosts(node);
        containerRoot.addNodes(node);
    }

    protected List<Object> selectTarget(ContainerRoot root, String query) {
        List<Object> nodeList = root.selectByQuery(query);
        List<Object> selectedNodes = new ArrayList<Object>();
        for (Object n : nodeList) {
            ContainerNode node = (ContainerNode) n;
            if (node.getHosts().size() < resolveDictionaryValue(node.getTypeDefinition())) {
                selectedNodes.add(node);
            }
        }
        return selectedNodes;
    }

    protected Integer resolveDictionaryValue(TypeDefinition td) {
        Integer cacheValue = vCPUCache.get(td);
        if (cacheValue == null) {
            for (DictionaryValue v : td.getDictionaryType().getDefaultValues()) {
                if (v.getAttribute().getName().equals(nbSubNodes)) {
                    cacheValue = Integer.parseInt(v.getValue());
                }
            }
        }
        if (cacheValue == null) {
            cacheValue = 0;
        }
        vCPUCache.put(td, cacheValue);
        return cacheValue;
    }

}
