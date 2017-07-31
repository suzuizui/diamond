package com.le.diamond.utils;

/**
 * 参数合法性检查工具类
 * 
 * @author zh
 * 
 */
public class ParamUtils {

    private static char[] validChars = new char[] { '_', '-', '.', ':' };


    /**
     * 白名单的方式检查, 合法的参数只能包含字母、数字、以及validChars中的字符, 并且不能为空
     * 
     * @param param
     * @return
     */
    public static boolean isValid(String param) {
        if (param == null) {
            return false;
        }
        int length = param.length();
        for (int i = 0; i < length; i++) {
            char ch = param.charAt(i);
            if (Character.isLetterOrDigit(ch)) {
                continue;
            }
            else if (isValidChar(ch)) {
                continue;
            }
            else {
                return false;
            }
        }
        return true;
    }


    private static boolean isValidChar(char ch) {
        for (char c : validChars) {
            if (c == ch) {
                return true;
            }
        }
        return false;
    }

}
