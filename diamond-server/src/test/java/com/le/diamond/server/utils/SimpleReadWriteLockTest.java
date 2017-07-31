package com.le.diamond.server.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;



public class SimpleReadWriteLockTest {

//    @Test
//    public void test_双重读锁_全部释放_加写锁() {
//        SimpleReadWriteLock lock = new SimpleReadWriteLock();
//        assertEquals(true, lock.tryReadLock());
//        assertEquals(true, lock.tryReadLock());
//
//        lock.releaseReadLock();
//        lock.releaseReadLock();
//
//        assertEquals(true, lock.tryWriteLock());
//    }
//
//    @Test
//    public void test_加写锁() {
//        SimpleReadWriteLock lock = new SimpleReadWriteLock();
//        assertEquals(true, lock.tryWriteLock());
//        lock.releaseWriteLock();
//    }
//
//    @Test
//    public void test_双重写锁() {
//        SimpleReadWriteLock lock = new SimpleReadWriteLock();
//
//        assertEquals(true, lock.tryWriteLock());
//        assertEquals(false, lock.tryWriteLock());
//    }
//
//    @Test
//    public void test_先读锁后写锁() {
//        SimpleReadWriteLock lock = new SimpleReadWriteLock();
//
//        assertEquals(true, lock.tryReadLock());
//        assertEquals(false, lock.tryWriteLock());
//    }
//
//    @Test
//    public void test_双重读锁_释放一个_加写锁失败() {
//        SimpleReadWriteLock lock = new SimpleReadWriteLock();
//        assertEquals(true, lock.tryReadLock());
//        assertEquals(true, lock.tryReadLock());
//
//        lock.releaseReadLock();
//
//        assertEquals(false, lock.tryWriteLock());
//    }
}
