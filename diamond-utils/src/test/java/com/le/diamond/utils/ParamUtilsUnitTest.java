package com.le.diamond.utils;

import junit.framework.Assert;

import org.junit.Test;


public class ParamUtilsUnitTest {

    @Test
    public void testInvalidParam() {
        // 合法字符只能包含字母、数字、下划线、横线、点号、冒号
        Assert.assertTrue(ParamUtils.isValid("123"));
        Assert.assertTrue(ParamUtils.isValid("abc"));
        Assert.assertTrue(ParamUtils.isValid("abc_123"));
        Assert.assertTrue(ParamUtils.isValid("abc-123"));
        Assert.assertTrue(ParamUtils.isValid("a.b.c"));
        Assert.assertTrue(ParamUtils.isValid("abc:def"));
        Assert.assertTrue(ParamUtils.isValid("com.taobao.diamond.test:1.0.0-test_123"));
        // null非法
        Assert.assertFalse(ParamUtils.isValid(null));
        // 空格非法
        Assert.assertFalse(ParamUtils.isValid("abc 123"));
        // 其他一些非法字符, 这里不能一一列举
        Assert.assertFalse(ParamUtils.isValid("abc?123"));
        Assert.assertFalse(ParamUtils.isValid("abc@123"));
        Assert.assertFalse(ParamUtils.isValid("abc!123"));
        Assert.assertFalse(ParamUtils.isValid("abc#123"));
        Assert.assertFalse(ParamUtils.isValid("abc%123"));
        Assert.assertFalse(ParamUtils.isValid("abc*123"));
        Assert.assertFalse(ParamUtils.isValid("abc^123"));
    }
}
