package com.meng.hui.android.xiezuo.core;

/**
 * Created by mzk10 on 2017/8/9.
 */

public class Constants {

    public static final class url
    {
        public static final String BASE_URL = "http://api.kanfamily.net:8080/myservice/";
//        public static final String BASE_URL = "http://10.10.10.46:8080/myservice/";
        public static final String URL_CHECKVERSION = BASE_URL + "version?app=check";
        public static final String URL_FONTLIST = BASE_URL + "setting?app=fontlist";
    }

    public static final class path
    {
        public static final String BOOKLIST_DIR = "/booklist/";
        public static final String FONT_DIR = "/fonts/";
    }

    public static final String VOL_EXT_NAME = ".txt";
    public static final String VOLACTION_EXT_NAME = ".bak";

}
