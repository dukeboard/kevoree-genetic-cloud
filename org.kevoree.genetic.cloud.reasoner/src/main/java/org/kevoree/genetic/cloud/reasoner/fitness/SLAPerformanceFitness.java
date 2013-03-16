package org.kevoree.genetic.cloud.reasoner.fitness;

import org.kevoree.ComponentInstance;
import org.kevoree.ContainerRoot;
import org.kevoree.TypeDefinition;
import org.kevoree.genetic.cloud.reasoner.SLAModel;
import org.kevoree.genetic.cloud.reasoner.util.PropertyCachedResolver;
import org.kevoree.genetic.framework.KevoreeFitnessFunction;

import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 16/03/13
 * Time: 15:51
 */
public class SLAPerformanceFitness implements KevoreeFitnessFunction {

    private String property = "vcpu_load";
    private PropertyCachedResolver resolver = new PropertyCachedResolver();

    private SLAModel slaModel = new SLAModel();

    public SLAModel getSlaModel() {
        return slaModel;
    }

    public SLAPerformanceFitness setSlaModel(SLAModel slaModel) {
        this.slaModel = slaModel;
        return this;
    }

    @Override
    public double evaluate(ContainerRoot model) {
        HashMap<TypeDefinition, Double> map = new HashMap<TypeDefinition, Double>();
        List<Object> components = model.selectByQuery("nodes[*]/hosts[*]/components[*]");
        for (Object o : components) {
            ComponentInstance ci = (ComponentInstance) o;
            if (slaModel.getVCPULoad(ci.getTypeDefinition().getName()) != null) {  //only consider SLA of declared types
                Double currentValue = map.get(ci.getTypeDefinition());
                if (currentValue == null) {
                    currentValue = 0d;
                }
                Double vcpu_load = resolver.getDefault(ci, property);
                if (vcpu_load != null) {
                    map.put(ci.getTypeDefinition(), currentValue + vcpu_load);
                }
            }
        }
        Double globalPercent = 0d;
        for (TypeDefinition td : map.keySet()) {
            Double maxVCPUperType = slaModel.getVCPULoad(td.getName());
            Double currentVCPU = map.get(td);
            if (currentVCPU >= maxVCPUperType) {
                globalPercent = globalPercent + 100d;
            } else {
                globalPercent = globalPercent + ((currentVCPU / maxVCPUperType) * 100d);
            }
        }
        if (globalPercent == 0) {
            return 100;
        }
        return (100 - (globalPercent / slaModel.getTypes().size()));
    }

    @Override
    public String getName() {
        return SLAPerformanceFitness.class.getSimpleName();
    }

}
