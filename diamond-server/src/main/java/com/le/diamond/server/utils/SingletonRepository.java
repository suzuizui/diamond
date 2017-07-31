package com.le.diamond.server.utils;

import java.util.concurrent.ConcurrentHashMap;


/**
 * ��������ͬ���ݵ�ʵ���Ĺ����ࡣ���磬������������ͻ���IP��
 */
public class SingletonRepository<T> {

    public SingletonRepository() {
        // ��ʼ����С2^16, �������������ռ��50k���ڴ棬���ⲻͣ����
        shared = new ConcurrentHashMap<T, T>(1 << 16);
    }
    
    public T getSingleton(T obj) {
        T previous = shared.putIfAbsent(obj, obj);
        return (null == previous) ? obj : previous;
    }

    public int size() {
        return shared.size();
    }
    
    // ����С��ʹ�á�
    public void remove(Object obj) {
        shared.remove(obj);
    }
    
    // =================
    private final ConcurrentHashMap<T, T> shared;
    
    // ===================
    /**
     * DataId��Group�Ļ��档
     */
    static public class DataIdGroupIdCache {
        static public String getSingleton(String str) {
            return cache.getSingleton(str);
        }
        
        static SingletonRepository<String> cache = new SingletonRepository<String>();
    }
}
