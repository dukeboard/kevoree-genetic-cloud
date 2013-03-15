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
public class OptimizeRedondencyOperator extends AbstractKevoreeOperator {

    private List<String> types = new ArrayList<String>();

    public OptimizeRedondencyOperator addType(String t) {
        types.add(t);
        return this;
    }

    public OptimizeRedondencyOperator setAllTypes(List<String> _types) {
        types = _types;
        return this;
    }

    public List<String> getAllTypes(){
        return types;
    }

    //private Integer globalRedondency = 5; //MAGIC NUMBER !!!

    @Override
    protected void applyMutation(Object target, ContainerRoot model) {
        //Look for best typeDefinition
        HashMap<String, Integer> counter = new HashMap<String, Integer>();
        //INITIATE with all TD of components
        for (String tdName : types) {
            counter.put(tdName, 0);
        }
        for (ContainerNode node : model.getNodes()) {
            for (ComponentInstance instance : node.getComponents()) {
                Integer val = counter.get(instance.getTypeDefinition().getName());
                if (val != null) {
                    val = val + 1;
                    counter.put(instance.getTypeDefinition().getName(), val);
                }
            }
        }
        String targetType = null;
        Integer lowestScore = Integer.MAX_VALUE;
        for (String key : counter.keySet()) {
            Integer score = counter.get(key);
            if (score < lowestScore) {
                targetType = key;
                lowestScore = score;
            }
        }
        if (targetType != null && target instanceof ContainerNode) {
            TypeDefinition td = model.findTypeDefinitionsByID(targetType);
            if (td != null) {
                ComponentInstance inst = factory.createComponentInstance();
                inst.setName(generateName(targetType));
                inst.setTypeDefinition(td);

                ContainerNode targetNode = (ContainerNode) target;
                //if(targetNode.getComponents().size() < globalRedondency){
                    targetNode.addComponents(inst);
                //}
            }
        }
    }

    protected String generateName(String componentTypeName) {
        Random r = new Random();
        return componentTypeName + "_" + Math.abs(r.nextInt());
    }




}
