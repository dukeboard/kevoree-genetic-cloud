package org.kevoree.genetic.cloud.reasoner.operators;

import org.kevoree.*;
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

            // Get the type of the node and set the same type for the new  node
            newTargetNode.setName("Cloned Node");
            TypeDefinition td =targetNode.getTypeDefinition();
            newTargetNode.setTypeDefinition(td);


            //create an instance of each components already defined as child of targetNode


            for(ComponentInstance ci : targetNode.getComponents())
            {
                String namecomposent=ci.getName();
                ComponentInstance ci1= factory.createComponentInstance();
                ci1.setName(namecomposent);

                TypeDefinition tdc =ci.getTypeDefinition();
                ci1.setTypeDefinition(tdc);

                newTargetNode.addComponents(ci1);
            }


        } else {
            System.out.println("Bad configuration of mutator");
        }

    }
}