package com.le.diamond.utils;

import junit.framework.Assert;

import org.junit.Test;


public class ParamUtilsUnitTest {

    @Test
    public void testInvalidParam() {
        // �Ϸ��ַ�ֻ�ܰ�����ĸ�����֡��»��ߡ����ߡ���š�ð��
        Assert.assertTrue(ParamUtils.isValid("123"));
        Assert.assertTrue(ParamUtils.isValid("abc"));
        Assert.assertTrue(ParamUtils.isValid("abc_123"));
        Assert.assertTrue(ParamUtils.isValid("abc-123"));
        Assert.assertTrue(ParamUtils.isValid("a.b.c"));
        Assert.assertTrue(ParamUtils.isValid("abc:def"));
        Assert.assertTrue(ParamUtils.isValid("com.taobao.diamond.test:1.0.0-test_123"));
        // null�Ƿ�
        Assert.assertFalse(ParamUtils.isValid(null));
        // �ո�Ƿ�
        Assert.assertFalse(ParamUtils.isValid("abc 123"));
        // ����һЩ�Ƿ��ַ�, ���ﲻ��һһ�о�
        Assert.assertFalse(ParamUtils.isValid("abc?123"));
        Assert.assertFalse(ParamUtils.isValid("abc@123"));
        Assert.assertFalse(ParamUtils.isValid("abc!123"));
        Assert.assertFalse(ParamUtils.isValid("abc#123"));
        Assert.assertFalse(ParamUtils.isValid("abc%123"));
        Assert.assertFalse(ParamUtils.isValid("abc*123"));
        Assert.assertFalse(ParamUtils.isValid("abc^123"));
    }
}
