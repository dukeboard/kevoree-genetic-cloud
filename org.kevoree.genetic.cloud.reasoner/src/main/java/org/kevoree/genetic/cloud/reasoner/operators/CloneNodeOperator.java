package org.kevoree.genetic.cloud.reasoner.operators;

import org.kevoree.ComponentInstance;
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
            ContainerNode newTargetNode  = factory.createContainerNode();
           // newTargetNode.setName();

            //create an instance of each components already defined as child of targetNode


            for(ComponentInstance ci : targetNode.getComponents())
            {
                ComponentInstance ci1= factory.createComponentInstance();
                ci1= ci;
                newTargetNode.addComponents(ci1);
            }


        } else {
            System.out.println("Bad configuration of mutator");
        }

    }
}