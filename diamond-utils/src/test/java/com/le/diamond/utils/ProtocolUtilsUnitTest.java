package com.le.diamond.utils;

import static org.junit.Assert.*;

import org.junit.Test;


public class ProtocolUtilsUnitTest {

    @Test
    public void test() {
        assertEquals(205, Protocol.getVersionNumber("2.0.5"));
        assertEquals(300, Protocol.getVersionNumber("3.0.0"));
        assertEquals(-1, Protocol.getVersionNumber(null));
    }
}
