package com.le.diamond.server.utils;

import java.util.concurrent.ConcurrentHashMap;


/**
 * 避免多个相同内容的实例的工具类。比如，可以用来缓存客户端IP。
 */
public class SingletonRepository<T> {

    public SingletonRepository() {
        // 初始化大小2^16, 这个容器本身大概占用50k的内存，避免不停扩容
        shared = new ConcurrentHashMap<T, T>(1 << 16);
    }
    
    public T getSingleton(T obj) {
        T previous = shared.putIfAbsent(obj, obj);
        return (null == previous) ? obj : previous;
    }

    public int size() {
        return shared.size();
    }
    
    // 必须小心使用。
    public void remove(Object obj) {
        shared.remove(obj);
    }
    
    // =================
    private final ConcurrentHashMap<T, T> shared;
    
    // ===================
    /**
     * DataId和Group的缓存。
     */
    static public class DataIdGroupIdCache {
        static public String getSingleton(String str) {
            return cache.getSingleton(str);
        }
        
        static SingletonRepository<String> cache = new SingletonRepository<String>();
    }
}
