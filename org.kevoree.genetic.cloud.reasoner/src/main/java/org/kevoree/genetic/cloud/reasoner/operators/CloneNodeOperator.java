package org.kevoree.genetic.cloud.reasoner.operators;

import org.kevoree.ContainerNode;
import org.kevoree.ContainerRoot;
import org.kevoree.KevoreeFactory;
import org.kevoree.genetic.library.operator.AbstractKevoreeOperator;
import org.kevoree.impl.DefaultKevoreeFactory;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 05/04/13
 * Time: 16:21
 */
public class CloneNodeOperator extends AbstractKevoreeOperator {
    private KevoreeFactory factory = new DefaultKevoreeFactory();


    @Override
    protected void applyMutation(Object o, ContainerRoot containerRoot) {
        if(o instanceof ContainerNode){
            ContainerNode targetNode = (ContainerNode) o;

           //create a new node instance of a type ... named newTargetNode
            //factory.createContainerNode()

            //create an instance of each components already defined as child of targetNode
            //loop




        } else {
            System.out.println("Bad configuration of mutator");
        }

    }
}
