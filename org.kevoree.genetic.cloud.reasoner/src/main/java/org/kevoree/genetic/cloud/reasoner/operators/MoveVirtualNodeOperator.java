package org.kevoree.genetic.cloud.reasoner.operators;

import org.kevoree.ContainerNode;
import org.kevoree.ContainerRoot;
import org.kevoree.genetic.cloud.reasoner.util.PropertyCachedResolver;
import org.kevoree.genetic.library.operator.MoveNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 16/03/13
 * Time: 17:52
 */
public class MoveVirtualNodeOperator extends MoveNode {

    private final String nbSubNodes = "vcpu";
    private PropertyCachedResolver resolver = new PropertyCachedResolver();

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
