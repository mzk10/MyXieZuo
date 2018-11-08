package com.meng.hui.android.xiezuo.util.pop.holder;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.SeekBar;

import com.meng.hui.android.xiezuo.R;

import butterknife.BindView;
import butterknife.OnClick;

public class EditerSettingHolder extends BaseHolder<Integer, Integer, String> {
    @BindView(R.id.seek_setting_testsize)
    SeekBar seek_setting_testsize;
    @BindView(R.id.seek_setting_bg)
    SeekBar seek_setting_bg;

    public EditerSettingHolder(Context context, OnCloseListener<Integer, Integer, String> listener) {
        super(context, listener);
    }

    @Override
    public int initView() {
        return R.layout.pop_editer_setting;
    }

    @Override
    public void startAction() {
        SharedPreferences config = getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        initSeedTextSize(config);
        initSeedBG(config);
    }

    private void initSeedBG(final SharedPreferences config) {
        int editbg = config.getInt("editbg", 1);
        switch (editbg) {
            case 1:
                seek_setting_bg.setProgress(0);
                break;
            case 2:
                seek_setting_bg.setProgress(1);
                break;
        }
        seek_setting_bg.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (getListener() != null) {
                        SharedPreferences.Editor edit = config.edit();
                        switch (progress) {
                            case 0:
                                edit.putInt("editbg", 1).apply();
                                break;
                            case 1:
                                edit.putInt("editbg", 2).apply();
                                break;
                        }
                    }
                    getListener().onClick(true, 1, null, null);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initSeedTextSize(final SharedPreferences config) {
        int textsize = config.getInt("fontSize", 18);
        switch (textsize) {
            case 14:
                seek_setting_testsize.setProgress(0);
                break;
            case 18:
                seek_setting_testsize.setProgress(1);
                break;
            case 22:
                seek_setting_testsize.setProgress(2);
                break;
        }
        seek_setting_testsize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (getListener() != null) {
                        SharedPreferences.Editor edit = config.edit();
                        switch (progress) {
                            case 0:
                                edit.putInt("fontSize", 14).apply();
                                break;
                            case 1:
                                edit.putInt("fontSize", 18).apply();
                                break;
                            case 2:
                                edit.putInt("fontSize", 22).apply();
                                break;
                        }
                        getListener().onClick(true, 0, null, null);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void changeValue(String... value) {

    }

    @OnClick(R.id.btn_main_menu_changefont)
    public void onBtnMainMenuChangefontClicked() {
    }

    @OnClick(R.id.btn_main_menu_cancel)
    public void onBtnMainMenuCancelClicked() {
        if (getListener() != null) {
            getListener().onClick(false, null, null, null);
        }
    }
}
