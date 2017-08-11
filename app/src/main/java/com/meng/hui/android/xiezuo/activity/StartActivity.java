package com.meng.hui.android.xiezuo.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.meng.hui.android.xiezuo.R;
import com.meng.hui.android.xiezuo.core.Constants;
import com.meng.hui.android.xiezuo.core.MyActivity;
import com.meng.hui.android.xiezuo.entity.ResponseData;
import com.meng.hui.android.xiezuo.entity.VersionCheckEntity;
import com.meng.hui.android.xiezuo.util.FileUtil;
import com.meng.hui.android.xiezuo.util.HttpHandler;
import com.meng.hui.android.xiezuo.util.MakeDialogUtil;
import com.meng.hui.android.xiezuo.util.Utils;
import com.meng.hui.android.xiezuo.util.XiezuoDebug;

import java.io.File;

/**
 * Created by mzk10 on 2017/7/12.
 */

public class StartActivity extends MyActivity {

    private ImageView iv_anim_welcome_scale;
    private ImageView iv_anim_welcome;

    private Animation anim_welcome_scale;
    private Animation anim_welcome;

    private Handler handler = new Handler();
    private String TAG = "StartActivity";

    @Override
    public void initView() {
        setContentView(R.layout.activity_start);
        iv_anim_welcome_scale = findViewById(R.id.iv_anim_welcome_icon);
        iv_anim_welcome = findViewById(R.id.iv_anim_welcome);
    }

    @Override
    public void initData() {
        anim_welcome_scale = AnimationUtils.loadAnimation(this, R.anim.anim_welcome_scale);
        anim_welcome_scale.setDuration(1000);
        anim_welcome_scale.setInterpolator(this, android.R.interpolator.bounce);
        anim_welcome_scale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iv_anim_welcome.setVisibility(View.VISIBLE);
                        iv_anim_welcome.startAnimation(anim_welcome);
                    }
                }, 200);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        anim_welcome = AnimationUtils.loadAnimation(StartActivity.this, R.anim.anim_welcome_rotate);
        anim_welcome.setInterpolator(StartActivity.this, android.R.interpolator.linear);
        anim_welcome.setDuration(3000);
        anim_welcome.setRepeatCount(Animation.INFINITE);
        anim_welcome.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                checkVersion();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void startAction() {
        iv_anim_welcome_scale.startAnimation(anim_welcome_scale);
    }

    @Override
    public void onBackClicked() {

    }

    @Override
    public void onClick(View view) {

    }

    private void checkVersion() {
        HttpHandler handler = new HttpHandler(Constants.url.URL_CHECKVERSION, StartActivity.this);
        handler.request(new HttpHandler.HttpCallBack() {
            @Override
            public void onCallBack(ResponseData data) {
                if (data.getCode() == 200) {
                    final VersionCheckEntity versionCheckEntity = new Gson().fromJson(data.getData(), VersionCheckEntity.class);
                    int appVersionCode = Utils.getAppVersionCode(StartActivity.this);
                    int lastVersion = versionCheckEntity.getLastVersion();
                    if (appVersionCode < lastVersion) {
                        String versionDetail = versionCheckEntity.getVersionDetail();
                        String[] buf= null;
                        if (versionDetail!=null && !"".equals(versionDetail))
                        {
                            buf = versionDetail.split(";");
                        }
                        XiezuoDebug.i(TAG, "发现新版本，下载地址：" + versionCheckEntity.getDownloadUrl());
                        //TODO 下载
                        MakeDialogUtil.showConfirmDialog(
                                StartActivity.this,
                                "发现新版本("+versionCheckEntity.getVersionName()+")，是否下载？" +
                                        "\n文件大小："+ FileUtil.getFileLength(versionCheckEntity.getLength()),
                                "聪明人更新",
                                "智障不更新",
                                new MakeDialogUtil.OnDialogConfirmLinsener() {
                                    @Override
                                    public void onConfirm(boolean isConfirm) {
                                        if (isConfirm)
                                        {
                                            Utils.showToast(StartActivity.this, "开始下载啦！");
                                            download(versionCheckEntity.getDownloadUrl());
                                        }else
                                        {
                                            Utils.showToast(StartActivity.this, "我就不下载！");
                                            jumpPage();
                                        }
                                    }
                                },
                                true,
                                buf
                        );
                    } else {
                        XiezuoDebug.i(TAG, "当前版本已经是最新");
                        jumpPage();
                    }
                } else {
                    XiezuoDebug.i(TAG, data.getInfo());
                    jumpPage();
                }
            }
        });
    }

    private void download(String url) {
        final MakeDialogUtil.DownPreContrl ctrl = MakeDialogUtil.showDownPre(StartActivity.this);
        AsyncTask<String, String, String> async = new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                String url = strings[0];
                File file = getExternalFilesDir(null);
                File dir = new File(file, "app");
                if (dir.exists() || dir.mkdirs())
                {
                    FileUtil.downloadFile(url, dir.getPath(), new FileUtil.OnDownloadListener() {
                        @Override
                        public void onDownloadComplate(final String path) {
                            XiezuoDebug.i(TAG, "下载完成，文件保存路径：" + path);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ctrl.close();
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }

                        @Override
                        public void onDownloadPro(final int pro) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ctrl.setPro(pro);
                                }
                            });
                        }
                    });
                }else{
                    Utils.showToast(StartActivity.this, "创建目录失败");
                }
                return null;
            }
        };
        async.execute(url);

    }

    private void jumpPage() {
        Intent intent = new Intent(StartActivity.this, BookListActivity.class);
        startActivity(intent);
        finish();
    }

}
