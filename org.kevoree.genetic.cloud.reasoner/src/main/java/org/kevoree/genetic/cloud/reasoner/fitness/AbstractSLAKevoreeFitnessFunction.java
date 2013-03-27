package org.kevoree.genetic.cloud.reasoner.fitness;

import org.kevoree.genetic.cloud.reasoner.SLAModel;
import org.kevoree.genetic.framework.KevoreeFitnessFunction;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 27/03/13
 * Time: 11:11
 */
public abstract class AbstractSLAKevoreeFitnessFunction implements KevoreeFitnessFunction {

    protected SLAModel slaModel = null;

    public SLAModel getSlaModel() {
        return slaModel;
    }

    public AbstractSLAKevoreeFitnessFunction setSlaModel(SLAModel _slaM) {
        slaModel = _slaM;
        return this;
    }


}
