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
import com.meng.hui.android.xiezuo.core.database.FontDao;
import com.meng.hui.android.xiezuo.entity.FontEntity;
import com.meng.hui.android.xiezuo.entity.ResponseData;
import com.meng.hui.android.xiezuo.entity.VersionCheckEntity;
import com.meng.hui.android.xiezuo.util.FileUtil;
import com.meng.hui.android.xiezuo.util.HttpHandler;
import com.meng.hui.android.xiezuo.util.MakeDialogUtil;
import com.meng.hui.android.xiezuo.util.Utils;
import com.meng.hui.android.xiezuo.util.XiezuoDebug;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
                                            updateFontList();
                                        }
                                    }
                                },
                                true,
                                buf
                        );
                    } else {
                        XiezuoDebug.i(TAG, "当前版本已经是最新");
                        updateFontList();
                    }
                } else {
                    XiezuoDebug.i(TAG, data.getInfo());
                    updateFontList();
                }
            }
        });
    }

    private void download(String url) {
        final MakeDialogUtil.DownPreContrl ctrl = MakeDialogUtil.showDownPre(StartActivity.this);
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

    }


    private void updateFontList()
    {
        HttpHandler handler = new HttpHandler(Constants.url.URL_FONTLIST, this);
        handler.request(new HttpHandler.HttpCallBack() {
            @Override
            public void onCallBack(ResponseData data) {
                if (data.getCode() == 200){
                    try {
                        JSONArray array = new JSONArray(data.getData());
                        FontDao dao = new FontDao(StartActivity.this);
                        for (int i = 0; i<array.length(); i++)
                        {
                            FontEntity entity = new FontEntity();
                            JSONObject obj = array.getJSONObject(i);
                            entity.setId(obj.getInt("id"));
                            entity.setName(obj.getString("name"));
                            entity.setPath(obj.getString("path"));
                            FontEntity result = dao.selectData(entity.getId());
                            if (result == null)
                            {
                                XiezuoDebug.i(TAG, "无此记录，添加");
                                dao.addData(entity);
                            }else{
                                XiezuoDebug.i(TAG, "已经有此记录，不添加");
                            }
                        }
                        dao.closeDB();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                jump();
            }
        });
    }

    private void jump()
    {
        Intent intent = new Intent(StartActivity.this, BookListActivity.class);
        startActivity(intent);
        finish();
    }

}
