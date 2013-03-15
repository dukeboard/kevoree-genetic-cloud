package org.kevoree.genetic.cloud.reasoner.fitness;

import org.kevoree.ComponentInstance;
import org.kevoree.ContainerNode;
import org.kevoree.ContainerRoot;
import org.kevoree.genetic.cloud.reasoner.util.PropertyCachedResolver;
import org.kevoree.genetic.framework.KevoreeFitnessFunction;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 15/03/13
 * Time: 17:06
 */
public class SecurityFitness implements KevoreeFitnessFunction {

    private Integer percentLostBySecurityLevelBreak = 10;
    private String property = "securityLevel";
    private PropertyCachedResolver resolver = new PropertyCachedResolver();

    @Override
    public double evaluate(ContainerRoot model) {
        Double violationBreak = 0d;
        for (ContainerNode node : model.getNodes()) {
            double maxSecurityLevel = 0;
            if (node.getComponents().size() > 0) {
                //Look for higher security level
                for (ComponentInstance c : node.getComponents()) {
                    maxSecurityLevel = Math.max(maxSecurityLevel, resolver.getDefault(c, property));
                }
                //check dif for each component with max security level on the node
                for (ComponentInstance c : node.getComponents()) {
                    violationBreak += (maxSecurityLevel - resolver.getDefault(c, property));
                }
            }
        }
        if (violationBreak * percentLostBySecurityLevelBreak > 100) {
            return 100d;
        } else {
            return violationBreak * percentLostBySecurityLevelBreak;
        }
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
