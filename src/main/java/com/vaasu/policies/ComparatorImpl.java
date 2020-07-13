package com.vaasu.policies;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;

public class ComparatorImpl<K> implements Comparator<K>, Serializable {

    private final Map<K, Long> comparatorMap;

    public ComparatorImpl(Map<K, Long> comparatorMap) {
        this.comparatorMap = comparatorMap;
    }

    @Override
    public int compare(K o1, K o2) {
        Long key1Long = comparatorMap.get(o1);
        Long key2Long = comparatorMap.get(o2);

        return key1Long.compareTo(key2Long);
    }
}
