package com.meng.hui.android.xiezuo.util;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meng.hui.android.xiezuo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mzk10 on 2017/7/20.
 */

public class MakeDialogUtil {

    public static Dialog buildFullDialog(Activity activity) {
        boolean isfullScreen = isFullScreen(activity);
        Dialog dialog = null;
        if (isfullScreen) {
            dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        } else {
            dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        return dialog;
    }

    private static boolean isFullScreen(Activity activity) {
        return ((activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0);
    }

    public static void showInputDialog(Activity activity, String title, String[] defs, String[] Hints, final OnInputCallBack callBack) {
        boolean isfullScreen = isFullScreen(activity);
        Dialog tmpdialog = null;
        if (isfullScreen) {
            tmpdialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        } else {
            tmpdialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        }
        tmpdialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        tmpdialog.setCanceledOnTouchOutside(true);
        final Dialog dialog = tmpdialog;

        LinearLayout ll_dialog = new LinearLayout(activity);
        ll_dialog.setBackgroundColor(0x99000000);
        ll_dialog.setGravity(Gravity.CENTER);
        ll_dialog.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params_confirm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params_confirm.setMarginStart(20);
        params_confirm.setMarginEnd(20);
        LinearLayout ll_confirm = new LinearLayout(activity);
        ll_confirm.setBackgroundResource(R.drawable.inputdialog_bg);
//        view_confirm.setPadding(10, 10, 10, 10);
        ll_confirm.setGravity(Gravity.CENTER);
        ll_confirm.setOrientation(LinearLayout.VERTICAL);

        //Title
        LinearLayout.LayoutParams params_title = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params_title.setMargins(0, 20, 0, 0);
        TextView tv_title = new TextView(activity);
        tv_title.setText(title);
        tv_title.setTextColor(activity.getResources().getColor(R.color.colorBlack));
        tv_title.setTextSize(20);
        tv_title.setPadding(10, 10, 10, 10);
        tv_title.setGravity(Gravity.CENTER);
        ll_confirm.addView(tv_title, params_title);

        final List<EditText> tvlist = new ArrayList<EditText>();
        if (defs != null && Hints != null && (defs.length == Hints.length)) {
            for (int i = 0; i < Hints.length; i++) {
                EditText et_height = new EditText(activity);
                et_height.setBackgroundResource(R.drawable.edit_bg);
                et_height.setTextColor(0xff000000);
                et_height.setHint(Hints[i]);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(20, 10, 20, 0);
                et_height.setLayoutParams(params);
                et_height.setText(defs[i]);
                ll_confirm.addView(et_height);
                tvlist.add(et_height);
            }
        }

        LinearLayout ll_btnbox = new LinearLayout(activity);
        ll_btnbox.setOrientation(LinearLayout.HORIZONTAL);
        ll_btnbox.setGravity(Gravity.CENTER);

        Button btn_confirm = new Button(activity);
        btn_confirm.setBackgroundResource(R.drawable.confirmbtn_bg);
        btn_confirm.setTextSize(18);
        btn_confirm.setTextColor(activity.getResources().getColor(R.color.colorWhite));
        btn_confirm.setText("确定");
        btn_confirm.setPadding(20, 10, 20, 10);
        LinearLayout.LayoutParams params_btn_confirm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params_btn_confirm.setMargins(20, 20, 20, 30);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null) {
                    String params[] = new String[tvlist.size()];
                    for (int i = 0; i < tvlist.size(); i++) {
                        EditText et = tvlist.get(i);
                        String param = et.getText().toString();
                        params[i] = param;
                    }
                    callBack.onCallBack(params);
                    dialog.cancel();
                }
            }
        });
        ll_btnbox.addView(btn_confirm, params_btn_confirm);

        Button btn_cancel = new Button(activity);
        btn_cancel.setBackgroundResource(R.drawable.confirmbtn_bg);
        btn_cancel.setTextSize(18);
        btn_cancel.setTextColor(activity.getResources().getColor(R.color.colorWhite));
        btn_cancel.setText("取消");
        btn_cancel.setPadding(20, 10, 20, 10);
        LinearLayout.LayoutParams params_btn_cancel = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params_btn_cancel.setMargins(20, 20, 20, 30);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        ll_btnbox.addView(btn_cancel, params_btn_cancel);

        ll_confirm.addView(ll_btnbox);
        ll_dialog.addView(ll_confirm, params_confirm);
        dialog.setContentView(ll_dialog);
        dialog.show();
    }

    public interface OnInputCallBack {
        public void onCallBack(String[] params);
    }

}
