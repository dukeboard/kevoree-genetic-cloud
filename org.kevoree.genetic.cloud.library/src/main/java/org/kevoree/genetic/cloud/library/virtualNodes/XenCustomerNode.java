package org.kevoree.genetic.cloud.library.virtualNodes;

import org.kevoree.annotation.DictionaryAttribute;
import org.kevoree.annotation.DictionaryType;
import org.kevoree.annotation.Library;
import org.kevoree.annotation.NodeType;
import org.kevoree.library.sky.api.nodeType.PJavaSENode;


@NodeType
@Library(name = "Snt-Cloud")
@DictionaryType({
        @DictionaryAttribute(name = "owner", defaultValue = "Arash",optional = true),
        @DictionaryAttribute(name = "maxComponentCount", defaultValue = "10",optional = true)
})
public class XenCustomerNode extends PJavaSENode {
}
