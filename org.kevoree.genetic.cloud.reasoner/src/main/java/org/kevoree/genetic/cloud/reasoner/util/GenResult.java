package org.kevoree.genetic.cloud.reasoner.util;

import org.kevoree.ContainerRoot;

public class GenResult {
        public GenResult(long time, ContainerRoot val){timestamp=time;model=val;}
        public long timestamp;
        public ContainerRoot model;
    }