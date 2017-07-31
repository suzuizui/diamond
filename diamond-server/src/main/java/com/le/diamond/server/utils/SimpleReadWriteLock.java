package com.le.diamond.server.utils;


/**
 * 最简单的读写锁实现。要求加锁和解锁必须成对调用。
 *
 */
public class SimpleReadWriteLock {
    
    public synchronized boolean tryReadLock() {
        if (isWriteLocked()) {
            return false;
        } else {
            status++;
            return true;
        }
    }
    
    public synchronized void releaseReadLock() {
        status--;
    }
    
    public synchronized boolean tryWriteLock() {
        if (!isFree()) {
            return false;
        } else {
            status = -1;
            return true;
        }
    }
    
    public synchronized void releaseWriteLock() {
        status = 0;
    }
    
    private boolean isWriteLocked() {
        return status < 0;
    }
    private boolean isFree() {
        return status == 0;
    }
    
    // ================
    // 零表示没有锁；负数表示加写锁；正数表示加读锁，数值表示读锁的个数。
    private int status = 0;
}
