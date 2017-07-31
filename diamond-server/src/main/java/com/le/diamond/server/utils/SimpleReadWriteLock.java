package com.le.diamond.server.utils;


/**
 * ��򵥵Ķ�д��ʵ�֡�Ҫ������ͽ�������ɶԵ��á�
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
    // ���ʾû������������ʾ��д����������ʾ�Ӷ�������ֵ��ʾ�����ĸ�����
    private int status = 0;
}
