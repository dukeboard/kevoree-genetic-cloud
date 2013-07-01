package org.kevoree.genetic.cloud.library.infraNodes;

import org.kevoree.annotation.DictionaryAttribute;
import org.kevoree.annotation.DictionaryType;
import org.kevoree.annotation.Library;
import org.kevoree.annotation.NodeType;
import org.kevoree.library.sky.api.nodeType.IaaSNode;

@NodeType
@Library(name = "Snt-Cloud")
@DictionaryType({
        @DictionaryAttribute(name = "cost", defaultValue = "30", optional = true),
        @DictionaryAttribute(name = "consumption", defaultValue = "300", optional = true),
        @DictionaryAttribute(name = "vcpu", defaultValue = "8", optional = true),
})
public class XeonInfraNode implements IaaSNode {

}
