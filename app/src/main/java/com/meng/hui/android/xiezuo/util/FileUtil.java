package com.meng.hui.android.xiezuo.util;

import android.os.AsyncTask;

import com.meng.hui.android.xiezuo.entity.BookEntity;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by mzk10 on 2017/8/11.
 */

public class FileUtil {

    private static final String TAG = "FileUtil";

    /**
     * 保存可序列化对象
     *
     * @param obj
     * @param path
     */
    public static void saveSerializable(Serializable obj, String path) {
        if (obj != null) {
            FileOutputStream fos = null;
            ObjectOutputStream oos = null;
            try {
                fos = new FileOutputStream(path);
                oos = new ObjectOutputStream(fos);
                oos.writeObject(obj);
                oos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (oos != null) {
                    try {
                        oos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static Object loadSerializable(String path) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(path);
            ois = new ObjectInputStream(fis);
            Object o = ois.readObject();
            return o;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
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
        return (i > 0 ? (i + 1) == leg ? " " : file.substring(i + 1,  file.length()) : " ");
    }

    /**
     * 通过url获取文件名
     * @param url
     * @return
     */
    public static String getFilenameFromUrl(String url) {
        String substring = url.substring(url.lastIndexOf("/")+1, url.length());
        return substring;
    }

    /**
     * 去掉文件扩展名
     * @param name
     * @return
     */
    public static String cutExtensionName(String name)
    {
        try {
            int lastIndexOf = name.lastIndexOf(".");
            String substring = name.substring(0, lastIndexOf);
            return substring;
        } catch (Exception e) {
            return name;
        }
    }

    /**
     * 修改文件名（保留原扩展名）
     * @param file
     * @param newname
     */
    public static void changeFileName(File file, String newname)
    {
        if (file.exists())
        {
            String path = file.getPath();
            String name = FileUtil.getFilenameFromUrl(path);
            name = FileUtil.cutExtensionName(name);
            String newpath = path.replace(name, newname);
            file.renameTo(new File(newpath));
        }
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
            XiezuoDebug.e(TAG, e);
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
     *
     * @param string
     * @param file
     */
    public static void saveStringToFile(String string, File file) {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        try {
            fos = new FileOutputStream(file);
            osw = new OutputStreamWriter(fos, "UTF-8");
            osw.write(string);
            osw.flush();
        } catch (Exception e) {
            XiezuoDebug.e(TAG, e);
        } finally {
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    XiezuoDebug.e(TAG, e);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    XiezuoDebug.e(TAG, e);
                }
            }
        }
    }

    public static String getFileLength(int length) {
        final float k = 1024f;
        final float m = k * 1024f;
        final float g = m * 1024f;
        String result = "";
        if (length<(k*0.8f))
        {
            result = length + "b";
        }else if (length<(m*0.8f))
        {
            result = String.format("%.2f", (float)length / k) + "K";
        }else if (length<(g*0.8f))
        {
            result = String.format("%.2f", (float)length / m) + "M";
        }else{
            result = String.format("%.2f", (float)length / g) + "G";
        }
        return result;
    }


    public static void downloadFile(String url, String dir, final OnDownloadListener listener)
    {
        AsyncTask<String, Integer, String> async = new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... strings) {
                String url = strings[0];
                String dir = strings[1];
                XiezuoDebug.i(TAG, "开始下载 url=" + url + " dir=" + dir);
                InputStream is = null;
                FileOutputStream fos = null;
                String name = url.substring(url.lastIndexOf("/", url.length()));
                try {
                    File file_dir = new File(dir);
                    if (file_dir.exists() || file_dir.mkdirs())
                    {
                        URL u = new URL(url);
                        URLConnection conn = u.openConnection();
                        is = conn.getInputStream();
                        File file = new File(file_dir, name);
                        fos = new FileOutputStream(file);
                        int contentLength = conn.getContentLength();
                        byte[] buffer = new byte[1024];
                        int len;
                        int pro = 0;
                        int leijilen = 0;
                        publishProgress(pro);
                        while((len = is.read(buffer))!=-1)
                        {
                            leijilen += len;
                            fos.write(buffer, 0, len);
                            float res = (float)leijilen / (float)contentLength * 100f;
                            if ((int)res > pro)
                            {
                                pro = (int)res;
                                publishProgress(pro);
                            }
                        }
                        fos.flush();
                        return file.getPath();
                    }
                } catch (Exception e) {
                    XiezuoDebug.e(TAG, e);
                }finally
                {
                    if (fos!=null)
                    {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (is!=null)
                    {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                listener.onDownloadComplate(s);
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                listener.onDownloadPro(values[0]);
            }
        };

        async.execute(url, dir);

    }

    public interface OnDownloadListener
    {
        public void onDownloadComplate(String path);
        public void onDownloadPro(int pro);
    }

}
