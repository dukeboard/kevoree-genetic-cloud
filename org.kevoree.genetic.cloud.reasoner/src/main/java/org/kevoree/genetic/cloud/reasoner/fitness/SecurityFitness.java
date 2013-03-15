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
/**
 * This fitness function mesure security violation.
 * It's based on the following assumption : a component has a security level, if he is hosted on the same node than a component with a lower security level, it introduce a risk
 * If a component with a level 4 is mixed with a component with a security level 2 it introduced a security violation of 2, of it is mixed with a level of 1, it introduced 3 security break, etc ...
 * */
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
