package com.meng.hui.android.xiezuo.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class XiezuoDebug
{
    private static final String TAG = "XieZuo";
    private static final String FORMAT = "[class] : %s , [msg] : %s";
    private static final int MAX_VALUE = 1500;

    private static String buildLogMsg(String tag, String msg)
    {
        String log = String.format(FORMAT, tag, msg);
        return log;
    }

    public static void w(String tag, String msg)
    {
        DebugLevel level = getDebugLevel();
        if (level == DebugLevel.LEVEL_TECH)
        {
            try
            {
                Log.w(TAG, buildLogMsg(tag, msg.substring(0, MAX_VALUE)));
            } catch (Exception e)
            {
                Log.w(TAG, buildLogMsg(tag, msg));
            }
        }
    }

    public static void e(String tag, String msg)
    {
        DebugLevel level = getDebugLevel();
        if (level == DebugLevel.LEVEL_TECH)
        {
            try
            {
                Log.e(TAG, buildLogMsg(tag, msg.substring(0, MAX_VALUE)));
            } catch (Exception e)
            {
                Log.e(TAG, buildLogMsg(tag, msg));
            }
        }
    }

    public static void e(String tag, Throwable tr)
    {
        DebugLevel level = getDebugLevel();
        if (level == DebugLevel.LEVEL_TECH)
        {
            try
            {
                Log.e(TAG, buildLogMsg(tag, tr.getMessage().substring(0, MAX_VALUE)), tr);
            } catch (Exception e)
            {
                Log.e(TAG, buildLogMsg(tag, tr.getMessage()), tr);
            }
        }
    }

    public static void d(String tag, String msg)
    {
        DebugLevel level = getDebugLevel();
        if (level == DebugLevel.LEVEL_DEBUG || level == DebugLevel.LEVEL_TECH)
        {
            try
            {
                Log.d(TAG, buildLogMsg(tag, msg.substring(0, MAX_VALUE)));
            } catch (Exception e)
            {
                Log.d(TAG, buildLogMsg(tag, msg));
            }
        }
    }

    public static void v(String tag, String msg)
    {
        DebugLevel level = getDebugLevel();
        if (level == DebugLevel.LEVEL_TECH)
        {
            try
            {
                Log.v(TAG, buildLogMsg(tag, msg.substring(0, MAX_VALUE)));
            } catch (Exception e)
            {
                Log.v(TAG, buildLogMsg(tag, msg));
            }
        }
    }

    public static void i(String tag, String msg)
    {
        DebugLevel level = getDebugLevel();
        if (level == DebugLevel.LEVEL_TECH)
        {
            try
            {
                Log.i(TAG, buildLogMsg(tag, msg.substring(0, MAX_VALUE)));
            } catch (Exception e)
            {
                Log.i(TAG, buildLogMsg(tag, msg));
            }
        }
    }

    private static DebugLevel getDebugLevel()
    {
        return DebugLevel.LEVEL_TECH;
//        DebugLevel level = DebugLevel.LEVEL_PUBLISHER;
//        FileInputStream fis = null;
//        StringBuffer sb = new StringBuffer("");
//        try
//        {
//            File file = new File(LEVELLOCFILE);
//            fis = new FileInputStream(file);
//            int c;
//            while ((c = fis.read()) != -1)
//            {
//                sb.append((char) c);
//            }
//        } catch (FileNotFoundException e)
//        {
//            e.printStackTrace();
//        } catch (IOException e)
//        {
//            e.printStackTrace();
//        } finally
//        {
//            try
//            {
//                if (fis != null)
//                {
//                    fis.close();
//                }
//            } catch (Exception e2)
//            {
//            }
//        }
//        if (isNotNull(sb.toString()))
//        {
//            String trim = sb.toString().trim();
//            if (trim.equals("debug"))
//            {
//                return DebugLevel.LEVEL_DEBUG;
//            }
//            if (trim.equals("tech"))
//            {
//                return DebugLevel.LEVEL_TECH;
//            }
//        }
//        return level;
    }

    private enum DebugLevel
    {
        LEVEL_PUBLISHER, LEVEL_DEBUG, LEVEL_TECH
    }

    public static final boolean isNotNull(String str)
    {
        if (str != null && str.length() > 0)
        {
            return true;
        }
        return false;
    }


}