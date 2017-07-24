package com.meng.hui.android.xiezuo.util;

import com.meng.hui.android.xiezuo.entity.BookEntity;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Format;
import java.text.SimpleDateFormat;

/**
 * Created by mzk10 on 2017/7/21.
 */

public class Utils {

    private static final String TAG = "Utils";

    public static final BookEntity getBookDirInfo(File dir) {
        if (dir.exists() && dir.isDirectory()) {
            FileFilter filter = new FileFilter() {
                @Override
                public boolean accept(File file) {
                    String name = file.getName();
                    String fileExtensionName = getFileExtensionName(name);
                    if (!file.isDirectory() && "txt".equals(fileExtensionName)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            File[] files = dir.listFiles(filter);
            int length = 0;
            for (File f : files) {
                String s = loadFileString(f, "UTF-8");
                int len = s.length();
                length += len;
            }
            BookEntity entity = new BookEntity();
            entity.setBookCount(files.length);
            entity.setBookLength(length);
            entity.setBookName(dir.getName());
            entity.setLastDate(dir.lastModified());
            return entity;
        }
        return null;
    }

    /**
     * 获取文件扩展名
     *
     * @param file
     * @return
     */
    public static String getFileExtensionName(String file) {
        int i = file.lastIndexOf('.');
        int leg = file.length();
        return (i > 0 ? (i + 1) == leg ? " " : file.substring(i + 1,
                file.length()) : " ");
    }

    /**
     * 从文件读取文本内容
     *
     * @param file
     * @param encode
     * @return
     */
    public static String loadFileString(File file, String encode) {
        FileInputStream fis = null;
        InputStreamReader isr = null;

        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis, encode);
            int len = 0;
            char[] buffer = new char[1024];
            StringBuffer sb = new StringBuffer();
            while ((len = isr.read(buffer)) != -1) {
                sb.append(buffer, 0, len);
            }
            String result = sb.toString();
            return result;
        } catch (Exception e) {
            XiezuoDebug.e(TAG, "", e);
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
