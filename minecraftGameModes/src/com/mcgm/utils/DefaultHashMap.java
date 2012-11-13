/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.utils;

import java.util.HashMap;

/**
 *
 * @author Thomas
 */
public class DefaultHashMap<K, V> extends HashMap<K, V> {

    public V get(Object k, V def) {
        V v = super.get(k);
        return ((v == null) && !this.containsKey(k)) ? def : v;
    }
}
