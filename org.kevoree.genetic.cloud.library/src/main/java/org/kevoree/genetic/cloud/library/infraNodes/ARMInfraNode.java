package org.kevoree.genetic.cloud.library.infraNodes;

import org.kevoree.annotation.DictionaryAttribute;
import org.kevoree.annotation.DictionaryType;
import org.kevoree.annotation.Library;
import org.kevoree.annotation.NodeType;
import org.kevoree.library.sky.api.KevoreeNodeRunner;
import org.kevoree.library.sky.api.nodeType.AbstractIaaSNode;

@NodeType
@Library(name = "Snt-Cloud")
@DictionaryType({
        @DictionaryAttribute(name = "cost", defaultValue = "18", optional = true),
        @DictionaryAttribute(name = "consumption", defaultValue = "30", optional = true),
        @DictionaryAttribute(name = "vcpu", defaultValue = "1", optional = true),
})
public class ARMInfraNode extends AbstractIaaSNode {

    @Override
    public KevoreeNodeRunner createKevoreeNodeRunner(String s) {
        return null;
    }
}
