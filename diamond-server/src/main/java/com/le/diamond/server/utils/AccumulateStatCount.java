package com.le.diamond.server.utils;

import java.util.concurrent.atomic.AtomicLong;



public class AccumulateStatCount {
    
    final AtomicLong total = new AtomicLong(0);
    long lastStatValue = 0;
    
    
    public long increase() {
        return total.incrementAndGet();
    }
    
    public long stat() {
        long tmp = total.get() - lastStatValue;
        lastStatValue += tmp;
        return tmp;
    }
}
