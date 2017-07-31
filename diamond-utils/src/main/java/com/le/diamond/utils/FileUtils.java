package com.le.diamond.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.le.diamond.common.Constants;


public class FileUtils {

    public static boolean isFile(String path) {
        File file = new File(path);
        return file.isFile();
    }


    public static boolean isDirectory(String path) {
        File dir = new File(path);
        return dir.isDirectory();
    }


    public static String getFileName(String path) {
        File file = new File(path);
        if (!file.isFile()) {
            throw new RuntimeException("��·�����Ĳ����ļ�");
        }
        return file.getName();
    }


    public static String getParentDir(String path) {
        File file = new File(path);
        if (!file.isFile()) {
            throw new RuntimeException("��·�����Ĳ����ļ�");
        }
        File parent = file.getParentFile();
        if (parent.isDirectory()) {
            return parent.getName();
        }
        else {
            throw new RuntimeException("��Ŀ¼����Ŀ¼");
        }
    }


    public static String getGrandpaDir(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            throw new RuntimeException("��·�����Ĳ����ļ�");
        }
        File parent = file.getParentFile();
        if (parent.isDirectory()) {
            File grandpa = parent.getParentFile();
            if (grandpa.isDirectory()) {
                return grandpa.getName();
            }
            else {
                throw new RuntimeException("��Ŀ¼����Ŀ¼");
            }
        }
        else {
            throw new RuntimeException("��Ŀ¼����Ŀ¼");
        }
    }


    public static String getFileContent(String path) throws IOException {
        File tFile = new File(path);
        if (!tFile.isFile()) {
            throw new RuntimeException("�����ļ�");
        }
        RandomAccessFile file = new RandomAccessFile(tFile, "r");
        long fileSize = file.length();
        byte[] bytes = new byte[(int) fileSize];
        long readLength = 0L;
        while (readLength < fileSize) {
            int onceLength = file.read(bytes, (int) readLength, (int) (fileSize - readLength));
            if (onceLength > 0) {
                readLength += onceLength;
            }
            else {
                break;
            }
        }
        try {
            file.close();
        }
        catch (Exception e) {

        }
        return new String(bytes, Constants.ENCODE);
    }
}
