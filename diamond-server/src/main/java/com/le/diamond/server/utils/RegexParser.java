package com.le.diamond.server.utils;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.NullArgumentException;



/**
 * ����ConfigCenter��֧�ֵ�ͨ���ַ�ͨ���ж��Լ���׼����ת����ͨ����
 *
 * @author tianhu E-mail:
 * @version ����ʱ�䣺2008-12-30 ����07:09:52 ��˵��
 */
public class RegexParser {

    /**
     * �滻�����ַ����з����������ַ�Ϊ��׼������ʽ�ַ���; <br>
     * '*'�滻Ϊ ��.*�� '?'�滻Ϊ'{n}'��nΪ����?�ĸ���; <br>
     * ��������ĸ�����ֵ������ַ�ǰ�����'\'.
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
