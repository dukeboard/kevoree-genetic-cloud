package org.kevoree.genetic.cloud.reasoner.operators;

import org.kevoree.ComponentInstance;
import org.kevoree.ContainerNode;
import org.kevoree.ContainerRoot;
import org.kevoree.TypeDefinition;
import org.kevoree.genetic.cloud.reasoner.SLAModel;
import org.kevoree.genetic.library.operator.AbstractKevoreeOperator;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 13/03/13
 * Time: 11:20
 */
public class AddRandomComponentOperator extends AbstractKevoreeOperator {

    private SLAModel slaModel = new SLAModel();

    public SLAModel getSlaModel() {
        return slaModel;
    }

    List<String> typesList = null;

    public AddRandomComponentOperator setSlaModel(SLAModel slaModel) {
        this.slaModel = slaModel;
        Set<String> types = slaModel.getTypes();
        typesList = new ArrayList<String>(types);
        return this;
    }

    private Random rand = new Random();

    @Override
    protected void applyMutation(Object target, ContainerRoot model) {
        String type = typesList.get(rand.nextInt(typesList.size()));
        if (type != null && target instanceof ContainerNode) {
            TypeDefinition td = model.findTypeDefinitionsByID(type);
            if (td != null) {
                ComponentInstance inst = factory.createComponentInstance();
                inst.setName(generateName(type));
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

    protected List<Object> selectTarget(ContainerRoot root, String query) {
        return root.selectByQuery(query);
    }

}
