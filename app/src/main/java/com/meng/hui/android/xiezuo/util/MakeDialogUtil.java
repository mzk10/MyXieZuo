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

    public static void showInputDialog(Activity activity, String title, String defs, String Hints, final OnInputCallBack callBack) {
        final Dialog tmpdialog = buildFullDialog(activity);
        tmpdialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        tmpdialog.setCancelable(true);
        LinearLayout ll_inputdialog = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.layout_inputdialog, null);
        TextView tv_inputdialog_title = ll_inputdialog.findViewById(R.id.tv_inputdialog_title);
        final EditText et_inputdialog_content = ll_inputdialog.findViewById(R.id.et_inputdialog_content);
        View btn_inputdialog_confirm = ll_inputdialog.findViewById(R.id.btn_inputdialog_confirm);
        View btn_inputdialog_cancel = ll_inputdialog.findViewById(R.id.btn_inputdialog_cancel);

        tv_inputdialog_title.setText(title);
        btn_inputdialog_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = et_inputdialog_content.getText().toString();
                callBack.onCallBack(str);
                tmpdialog.cancel();
            }
        });
        btn_inputdialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tmpdialog.cancel();
            }
        });
        tmpdialog.setContentView(ll_inputdialog);
        tmpdialog.show();
    }

    public interface OnInputCallBack {
        public void onCallBack(String param);
    }

}
