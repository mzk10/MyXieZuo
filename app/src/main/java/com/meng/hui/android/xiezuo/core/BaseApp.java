package com.meng.hui.android.xiezuo.core;

import android.app.Application;

import java.io.File;

/**
 * Created by mzk10 on 2017/8/11.
 */

public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        File f = getExternalFilesDir(null);
        File file = new File(f, "err");
        if (file.exists() || file.mkdirs())
        {
            CrashHandler crashHandler = CrashHandler.getInstance(file.getPath());
            crashHandler.init(this);
        }

    }
}
