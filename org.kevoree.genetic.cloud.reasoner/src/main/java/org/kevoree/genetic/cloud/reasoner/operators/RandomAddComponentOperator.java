package org.kevoree.genetic.cloud.reasoner.operators;

import org.kevoree.ComponentInstance;
import org.kevoree.ContainerNode;
import org.kevoree.ContainerRoot;
import org.kevoree.TypeDefinition;
import org.kevoree.genetic.library.operator.AbstractKevoreeOperator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 13/03/13
 * Time: 11:20
 */
public class RandomAddComponentOperator extends AbstractKevoreeOperator {

    private List<String> types = new ArrayList<String>();

    public RandomAddComponentOperator addType(String t) {
        types.add(t);
        return this;
    }

    public RandomAddComponentOperator setAllTypes(List<String> _types) {
        types = _types;
        return this;
    }

    public List<String> getAllTypes(){
        return types;
    }

    @Override
    protected void applyMutation(Object target, ContainerRoot model) {

        //TD selection
        String targetType = types.get(rand.nextInt(types.size()));

        if (targetType != null && target instanceof ContainerNode) {
            TypeDefinition td = model.findTypeDefinitionsByID(targetType);
            if (td != null) {
                ComponentInstance inst = factory.createComponentInstance();
                inst.setName(generateName(targetType));
                inst.setTypeDefinition(td);

                ContainerNode targetNode = (ContainerNode) target;
                targetNode.addComponents(inst);
            }
        }
    }

    protected String generateName(String componentTypeName) {
        Random r = new Random();
        return componentTypeName + "_" + Math.abs(r.nextInt());
    }




}
