package org.kevoree.genetic.cloud.reasoner.util;

import org.kevoree.DictionaryValue;
import org.kevoree.Instance;
import org.kevoree.TypeDefinition;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 15/03/13
 * Time: 17:20
 */
public class PropertyCachedResolver {

    private HashMap<TypeDefinition, Double> default_cache = new HashMap<TypeDefinition, Double>();

    public Double getDefault(Instance i, String attName) {
        Double cacheValue = default_cache.get(i.getTypeDefinition());
        if (cacheValue == null) {
            for (DictionaryValue v : i.getTypeDefinition().getDictionaryType().getDefaultValues()) {
                if (v.getAttribute().getName().equals(attName)) {
                    cacheValue = Double.parseDouble(v.getValue());
                }
            }
        }
        if (cacheValue == null) {
            cacheValue = 0d;
        }
        default_cache.put(i.getTypeDefinition(), cacheValue);
        return cacheValue;
    }


}
