package com.opc.freshness.common.util;

import org.apache.http.util.TextUtils;

import java.util.Collection;

/**
 * Created by qishang on 2017/7/13.
 */
public class CollectionUtils {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.size() == 0;
    }

    public static void notEmpty(Collection<?> collection, String name) {
        if (isEmpty(collection)) {
            throw new IllegalStateException(name + " is empty");
        }
    }
}
