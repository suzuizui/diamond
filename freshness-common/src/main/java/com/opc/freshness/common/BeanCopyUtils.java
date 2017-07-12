package com.opc.freshness.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

/**
 * @author ming.wei
 * @date 2017年5月29日
 */
public final class BeanCopyUtils {
    private final static Logger logger = Logger.getLogger(BeanCopyUtils.class);

    private BeanCopyUtils() {

    }

    /**
     * 通用对象转换方法
     * 
     * @param from 转换源对象
     * @param to 转换目标对象
     * @param ignoreProperties 忽略的屬性
     * @return
     */
    public static <T> T convertClass(Object from, Class<T> to, String[] ignoreProperties) {
        T t = null;
        try {
            if (from != null) {
                t = to.newInstance();
                BeanUtils.copyProperties(from, t, ignoreProperties);
            }
        } catch (Exception e) {
            logger.error("转换出错:目标类" + to.getName(), e);
        }
        return t;
    }

    /**
     * 通用对象转换方法
     * 
     * @param from 转换源对象
     * @param to 转换目标对象
     * @return
     */
    public static <T> T convertClass(Object from, Class<T> to) {
        return convertClass(from, to, null);
    }

    /**
     * 通用列表转换方法
     * 
     * @param from 转换源列表
     * @param to 转换目标对象类
     * @param ignoreProperties
     * @return
     */
    public static <T, V> List<T> convertList(List<V> from, Class<T> to, String[] ignoreProperties) {
        if (CollectionUtils.isEmpty(from)) {
            return null;
        }
        List<T> targets = new ArrayList<T>();
        for (V v : from) {
            T target = convertClass(v, to, ignoreProperties);
            if (target == null) {
                continue;
            }
            targets.add(target);
        }
        return targets;
    }

    /**
     * 通用列表转换方法
     * 
     * @param from 转换源列表
     * @param to 转换目标对象类
     * @return
     */
    public static <T, V> List<T> convertList(List<V> from, Class<T> to) {
        return convertList(from, to, null);
    }

}
