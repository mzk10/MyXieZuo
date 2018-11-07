package com.meng.hui.android.xiezuo;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.gson.reflect.TypeToken;
import com.meng.hui.android.xiezuo.core.Constants;
import com.meng.hui.android.xiezuo.core.MyActivity;
import com.meng.hui.android.xiezuo.entity.FontEntity;
import com.meng.hui.android.xiezuo.entity.ResponseData;
import com.meng.hui.android.xiezuo.entity.VersionCheckEntity;
import com.meng.hui.android.xiezuo.module.book.BookListActivity;
import com.meng.hui.android.xiezuo.util.FileUtil;
import com.meng.hui.android.xiezuo.util.GsonUtils;
import com.meng.hui.android.xiezuo.util.MakeDialogUtil;
import com.meng.hui.android.xiezuo.util.Utils;
import com.meng.hui.android.xiezuo.util.XiezuoDebug;
import com.meng.hui.android.xiezuo.util.database.FontDao;
import com.meng.hui.android.xiezuo.util.http.HttpRequester;

import java.io.File;
import java.util.List;

import butterknife.BindView;

/**
 * Created by mzk10 on 2017/7/12.
 */

public class StartActivity extends MyActivity {

    @BindView(R.id.iv_anim_welcome_icon)
    ImageView iv_anim_welcome_icon;
    @BindView(R.id.iv_anim_welcome)
    ImageView iv_anim_welcome;

    private Animation anim_welcome_scale;
    private Animation anim_welcome;

    private Handler handler = new Handler();
    private String TAG = "StartActivity";

    @Override
    public int bindLayout() {
        return R.layout.activity_start;
    }

    @Override
    public void initView() {
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
    public void setListener() {

    }

    @Override
    public void startAction() {
        iv_anim_welcome_icon.startAnimation(anim_welcome_scale);
    }

    @Override
    public void onBackClicked() {

    }

    private void checkVersion() {

        HttpRequester.request(Constants.url.URL_CHECKVERSION, null, HttpRequester.METHOD_GET, new HttpRequester.OnRequestListener<ResponseData<VersionCheckEntity>>() {

            @Override
            public ResponseData<VersionCheckEntity> onRequestInBackground(String json) {
                ResponseData<VersionCheckEntity> result = GsonUtils.convertEntity(json, new TypeToken<ResponseData<VersionCheckEntity>>() {
                });
                return result;
            }

            @Override
            public void onRequest(ResponseData<VersionCheckEntity> result) {
                if (result != null && result.getCode() == 200) {
                    final VersionCheckEntity versionCheckEntity = result.getData();
                    int appVersionCode = Utils.getAppVersionCode(StartActivity.this);
                    int lastVersion = versionCheckEntity.getLastVersion();
                    if (appVersionCode < lastVersion) {
                        String versionDetail = versionCheckEntity.getVersionDetail();
                        String[] buf = null;
                        if (versionDetail != null && !"".equals(versionDetail)) {
                            buf = versionDetail.split(";");
                        }
                        XiezuoDebug.i(TAG, "发现新版本，下载地址：" + versionCheckEntity.getDownloadUrl());
                        //TODO 下载
                        MakeDialogUtil.showConfirmDialog(
                                StartActivity.this,
                                "发现新版本(" + versionCheckEntity.getVersionName() + ")，是否下载？" +
                                        "\n文件大小：" + FileUtil.getFileLength(versionCheckEntity.getLength()),
                                "聪明人更新",
                                "智障不更新",
                                new MakeDialogUtil.OnDialogConfirmLinsener() {
                                    @Override
                                    public void onConfirm(boolean isConfirm) {
                                        if (isConfirm) {
                                            Utils.showToast(StartActivity.this, "开始下载啦！");
                                            download(versionCheckEntity.getDownloadUrl());
                                        } else {
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
                    XiezuoDebug.i(TAG, result == null ? "服务器错误" : result.getInfo());
                    updateFontList();
                }
            }
        });
    }

    private void download(String url) {
        final MakeDialogUtil.DownPreContrl ctrl = MakeDialogUtil.showDownPre(StartActivity.this);
        File file = getExternalCacheDir();
        File dir = new File(file, "app");
        if (dir.exists() || dir.mkdirs()) {
            FileUtil.downloadFile(url, dir.getPath(), new FileUtil.OnDownloadListener() {
                @Override
                public void onDownloadComplate(final String path) {
                    XiezuoDebug.i(TAG, "下载完成，文件保存路径：" + path);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ctrl.close();
                            installApk(path);
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
        } else {
            Utils.showToast(StartActivity.this, "创建目录失败");
        }

    }

    private void installApk(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data;
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // "net.csdn.blog.ruancoder.fileprovider"即是在清单文件中配置的authorities
            data = FileProvider.getUriForFile(StartActivity.this, "com.meng.hui.android.xiezuo.fileProvider", new File(path));
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(new File(path));
        }
        //Uri data = Uri.fromFile(new File(path));
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        startActivity(intent);
        finish();
    }

    private void updateFontList() {
        HttpRequester.request(Constants.url.URL_FONTLIST, null, HttpRequester.METHOD_GET, new HttpRequester.OnRequestListener<ResponseData<List<FontEntity>>>() {
            @Override
            public ResponseData<List<FontEntity>> onRequestInBackground(String json) {
                return GsonUtils.convertEntity(json, new TypeToken<ResponseData<List<FontEntity>>>() {
                });
            }

            @Override
            public void onRequest(ResponseData<List<FontEntity>> data) {
                if (data != null) {
                    if (data.getCode() == 200) {
                        FontDao dao = new FontDao(StartActivity.this);
                        List<FontEntity> fontEntities = data.getData();
                        for (FontEntity entity : fontEntities) {
                            FontEntity result = dao.selectData(entity.getId());
                            if (result == null) {
                                XiezuoDebug.i(TAG, "无此记录，添加");
                                dao.addData(entity);
                            } else {
                                XiezuoDebug.i(TAG, "已经有此记录，不添加");
                            }
                        }
                        dao.closeDB();
                    }
                    jump();
                }
            }
        });
    }

    private void jump() {
        Intent intent = new Intent(StartActivity.this, BookListActivity.class);
        startActivity(intent);
        finish();
    }

}
