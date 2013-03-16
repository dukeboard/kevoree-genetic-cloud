package org.kevoree.genetic.cloud.library.onlineStore;

import org.kevoree.annotation.*;
import org.kevoree.framework.AbstractComponentType;

@Library(name = "Snt-Cloud")
@ComponentType
@DictionaryType({
        @DictionaryAttribute(name = "securityLevel", defaultValue = "4", optional = true),
        @DictionaryAttribute(name = "vcpu_load", defaultValue = "0.6", optional = true)
})
@Requires({
        @RequiredPort(name="outgoingConnection", type = PortType.MESSAGE, optional = true)
})
@Provides({
        @ProvidedPort(name = "message", type = PortType.MESSAGE)
})
public class PaymentDB extends AbstractComponentType {

    @Start
    public void start(){

    }

    @Stop
    public void stop(){

    }

    @Port(name = "message")
    public void messageReceived(Object o) {

    }
}
