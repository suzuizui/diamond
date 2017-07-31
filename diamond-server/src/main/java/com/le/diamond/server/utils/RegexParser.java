package com.le.diamond.server.utils;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.NullArgumentException;



/**
 * 用于ConfigCenter可支持的通配字符通配判定以及标准正则转换的通用类
 *
 * @author tianhu E-mail:
 * @version 创建时间：2008-12-30 下午07:09:52 类说明
 */
public class RegexParser {

    /**
     * 替换输入字符串中非正则特殊字符为标准正则表达式字符串; <br>
     * '*'替换为 ‘.*’ '?'替换为'{n}'，n为连续?的个数; <br>
     * 其他非字母或数字的特殊字符前均添加'\'.
     *
     * @param regex
     * @return
     */
    static public String regexFormat(String regex) {
        if (regex == null) {
            throw new NullArgumentException("regex string can't be null");
        }
        StringBuffer result = new StringBuffer();
        result.append("^");
        for (int i = 0; i < regex.length(); i++) {
            char ch = regex.charAt(i);
            if (CharUtils.isAsciiAlphanumeric(ch) || CharUtils.isAsciiNumeric(ch)) {
                result.append(ch);
            } else if (ch == '*') {
                result.append(".*");
            } else if (ch == '?') {
                int j = 0;
                for (; j < regex.length() - i && ch == '?'; j++) {
                    ch = regex.charAt(i + j);
                }
                if (j == regex.length() - i) {
                    result.append(".{" + j + "}");
                    break;
                } else {
                    j -= 1;
                    result.append(".{" + (j) + "}");
                    i += j - 1;
                }
            } else {
                result.append("\\" + ch);
            }
        }
        result.append("$");
        return result.toString();
    }

    static public boolean containsWildcard(String regex) {
        return (regex.contains("?") || regex.contains("*"));
    }

    public static void main(String[] args) {
        String str = "com.le.uic.*";
        System.out.println(str + " -> " + regexFormat(str));
    }
}
