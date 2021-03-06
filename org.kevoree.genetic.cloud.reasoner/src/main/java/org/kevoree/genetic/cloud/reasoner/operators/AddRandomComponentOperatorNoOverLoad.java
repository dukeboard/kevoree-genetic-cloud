package org.kevoree.genetic.cloud.reasoner.operators;

import org.kevoree.ComponentInstance;
import org.kevoree.ContainerNode;
import org.kevoree.ContainerRoot;
import org.kevoree.TypeDefinition;
import org.kevoree.genetic.cloud.reasoner.SLAModel;
import org.kevoree.genetic.cloud.reasoner.util.PropertyCachedResolver;
import org.kevoree.genetic.library.operator.AbstractKevoreeOperator;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 13/03/13
 * Time: 11:20
 */
public class AddRandomComponentOperatorNoOverLoad extends AbstractKevoreeOperator {

    private SLAModel slaModel = new SLAModel();

    public SLAModel getSlaModel() {
        return slaModel;
    }

    List<String> typesList = null;
    private String property = "vcpu_load";
    private PropertyCachedResolver resolver = new PropertyCachedResolver();

    public AddRandomComponentOperatorNoOverLoad setSlaModel(SLAModel slaModel) {
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

                Double totalLoadOfNode = 0d;
                for(ComponentInstance ci : targetNode.getComponents()){
                    totalLoadOfNode = totalLoadOfNode + resolver.getDefault(ci,property);
                }
                if(totalLoadOfNode < 1){
                    targetNode.addComponents(inst);

                }
            }
        }
    }

    protected String generateName(String componentTypeName) {
        Random r = new Random();
        return componentTypeName + "_" + Math.abs(r.nextInt());
    }

    protected List<Object> selectTarget(ContainerRoot root, String query) {
        if(query.contains("{")){
            return root.selectByQuery(query);
        } else {
            return Collections.singletonList(root.findByPath(query));
        }
        //System.out.println("query="+query+"/"+root.selectByQuery(query).size()+"-"+root.findByPath(query));
        //return root.selectByQuery(query);
    }


}
