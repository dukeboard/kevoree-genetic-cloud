package org.kevoree.genetic.cloud.reasoner;

import org.kevoree.TypeDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 16/03/13
 * Time: 15:46
 */
/* This SLA Model is a stub , which has to be replaced with our KMFv2 implantation
 *  */
public class SLAModel {

    private HashMap<String, Double> slaVCPULoad = new HashMap<String, Double>();

    public SLAModel putVCPULoad(String td, Double p) {
        slaVCPULoad.put(td, p);
        return this;
    }

    public Double getVCPULoad(String td) {
        return slaVCPULoad.get(td);
    }

    public SLAModel scale(Double factor) {
        SLAModel newSLAMODEL = new SLAModel();
        for (String type : slaVCPULoad.keySet()) {
            newSLAMODEL.putVCPULoad(type, slaVCPULoad.get(type) * factor);
        }
        return newSLAMODEL;
    }

    public Set<String> getTypes(){
        return slaVCPULoad.keySet();
    }

}
