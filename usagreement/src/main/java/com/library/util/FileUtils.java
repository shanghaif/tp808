package com.library.util;

import java.io.File;
import java.nio.channels.FileChannel;

public class FileUtils {

    public static boolean isPath(String path){
        String matches = "[A-Za-z]:\\\\[^:?\"><*]*";
        return path.matches(matches);
    }
    /**
     * 添加文件夹
     * @param dirpath 文件路径
     */
    public static void createDirFile(String dirpath) {
        File dirfile = new File(dirpath);
        if (!dirfile.exists()) {
            dirfile.mkdirs();
        }
    }

    /***
     * 删除文件
     * @param filepath 文件路径
     * @return 删除状态
     */
    public static boolean deleteFile(String filepath) {
        File file = new File(filepath);
        //  路径为文件且不为空
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false ;
    }

    /**
     * 删除文件夹（强制删除）
     * 1 // 验证字符串是否为正确路径名的正则表达式
     * 2 private static String matches = "[A-Za-z]:\\\\[^:?\"><*]*";
     * 3 String sPath = "" ;    //  sPath 为路径字符串
     * 4 // 通过 sPath.matches(matches) 方法的返回值判断是否正确
     * @param path 删除目录的文件路径
     */
    public static void deleteAllFilesOfDir(File path) {
        if (null != path) {
            //  如果不存在，则退出
            if (!path.exists())
                return;
            if (path.isFile()) {
                boolean result = path.delete();
                int tryCount = 0;
                while (!result && tryCount++ < 10) {
                    System.gc(); // 回收资源
                    result = path.delete();
                }
            }
            File[] files = path.listFiles();
            if (null != files) {
                for (int i = 0; i < files.length; i++) {
                    deleteAllFilesOfDir(files[i]);
                }
            }
            path.delete();
        }
    }

    /**
     * 通过文件路径直接修改文件名
     *
     * @param filePath    需要修改的文件的完整路径
     * @param newFileName 需要修改的文件的名称
     * @return
     */
    public static String FixFileName(String filePath, String newFileName) {
        File f = new File(filePath);
        if (!f.exists()) { // 判断原文件是否存在（防止文件名冲突）
            return null;
        }
        newFileName = newFileName.trim();
        if ("".equals(newFileName) || newFileName == null) // 文件名不能为空
            return null;
        String newFilePath = null;
        if (f.isDirectory()) { // 判断是否为文件夹
            newFilePath = filePath.substring(0, filePath.lastIndexOf("/")) + "/" + newFileName;
        } else {
            newFilePath = filePath.substring(0, filePath.lastIndexOf("/")) + "/" + newFileName
                    + filePath.substring(filePath.lastIndexOf("."));
        }
        File nf = new File(newFilePath);
        try {
            f.renameTo(nf); // 修改文件名
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
        return newFilePath;
    }

    /**
     * 获取文件大小
     * @param path  文件路径
     * @return  返回byte
     */
    public static long getFileSize(String path){
        File file = new File(path) ;
        if (file.exists() && file.isFile()){
            return file.length() ;
        }
        return 0 ;
    }

}
