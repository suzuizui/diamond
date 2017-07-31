package com.le.diamond.utils;

import junit.framework.Assert;

import org.junit.Test;

import com.le.diamond.common.Constants;


public class ContentUtilsUnitTest {

    @Test
    public void testVerifyIncrementPubContent() {

        String str = null;
        try {
            ContentUtils.verifyIncrementPubContent(str);
            Assert.fail("δ�׳��쳣");
        }
        catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
        str = "";
        try {
            ContentUtils.verifyIncrementPubContent(str);
            Assert.fail("δ�׳��쳣");
        }
        catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }

        str = "hello world";
        try {
            ContentUtils.verifyIncrementPubContent(str);
            Assert.assertTrue(true);
        }
        catch (IllegalArgumentException e) {
            Assert.fail("��Ӧ���׳��쳣");
        }
        
        str = "hello world \r hello world";
        try {
            ContentUtils.verifyIncrementPubContent(str);
            Assert.fail("δ�׳��쳣");
        }
        catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }

        str = "hello world \n hello world";
        try {
            ContentUtils.verifyIncrementPubContent(str);
            Assert.fail("δ�׳��쳣");
        }
        catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }

        str = "hello world \r\n hello world";
        try {
            ContentUtils.verifyIncrementPubContent(str);
            Assert.fail("δ�׳��쳣");
        }
        catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
        
        str = "hello world" + Constants.WORD_SEPARATOR + " hello world";
        try {
            ContentUtils.verifyIncrementPubContent(str);
            Assert.fail("δ�׳��쳣");
        }
        catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }

    }


    @Test
    public void testGetIdentify() {
        String content = "identity" + Constants.WORD_SEPARATOR + "content";
        String id = ContentUtils.getContentIdentity(content);
        Assert.assertEquals("identity", id);
    }


    @Test
    public void testGetContent() {
        String content = "identity" + Constants.WORD_SEPARATOR + "content";
        String id = ContentUtils.getContent(content);
        Assert.assertEquals("content", id);
    }

}
