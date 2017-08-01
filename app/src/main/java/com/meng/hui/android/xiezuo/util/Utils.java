package com.meng.hui.android.xiezuo.util;

import com.meng.hui.android.xiezuo.entity.BookEntity;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
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
            BookEntity entity = new BookEntity();
            entity.setBookCount(files.length);
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
     * @return
     */
    public static String loadFileString(File file) {
        String result = "";
        FileInputStream fis = null;
        InputStreamReader isr = null;
        String encode = "UTF-8";
        if (file.length() > 0) {
            try {
                encode = new FileCharsetDetector().guessFileEncoding(file);
                if (encode.contains(","))
                encode = encode.substring(0, encode.indexOf(","));
            } catch (IOException e) {
            }
        }
        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis, encode);
            int len = 0;
            char[] buffer = new char[1024];
            StringBuffer sb = new StringBuffer();
            while ((len = isr.read(buffer)) != -1) {
                sb.append(buffer, 0, len);
            }
            result = sb.toString();
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
        return result;
    }

    /**
     * 将文字写入文件
     * @param string
     * @param file
     */
    public static void saveStringToFile(String string, File file)
    {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        try {
            fos = new FileOutputStream(file);
            osw = new OutputStreamWriter(fos, "UTF-8");
            osw.write(string);
            osw.flush();
        } catch (Exception e) {
            XiezuoDebug.e(TAG, "", e);
        } finally {
            if (osw!=null)
            {
                try {
                    osw.close();
                } catch (IOException e) {
                    XiezuoDebug.e(TAG, "", e);
                }
            }
            if (fos!=null)
            {
                try {
                    fos.close();
                } catch (IOException e) {
                    XiezuoDebug.e(TAG, "", e);
                }
            }
        }

    }

    /**
     * 获取文字实际长度
     * @param content
     * @return
     */
    public static int getStringAbsLength(String content) {
        if (content!=null)
        {
            char[] chars = null;
            chars = content.toCharArray();
            int count = 0;
            for (char c : chars) {
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                    count++;
                } else if (c >= '0' && c <= '9') {
                    count++;
                } else if (c > 128) {
                    count++;
                }
            }
            return count;
        }else
        {
            return 0;
        }

    }

    /**
     * 保存可序列化对象
     * @param obj
     * @param path
     */
    public static void saveSerializable(Serializable obj, String path)
    {
        if (obj!=null)
        {
            FileOutputStream fos = null;
            ObjectOutputStream oos = null;
            try {
                fos = new FileOutputStream(path);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(obj);
                oos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }finally
            {
                if (oos!=null)
                {
                    try {
                        oos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (fos!=null)
                {
                    try {
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static Object loadSerializable(String path)
    {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(path);
            ois = new ObjectInputStream(fis);
            Object o = ois.readObject();
            return o;
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }




}