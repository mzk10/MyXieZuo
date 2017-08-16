package com.meng.hui.android.xiezuo.util;

import android.app.Activity;
import android.app.Dialog;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.meng.hui.android.xiezuo.R;
import com.meng.hui.android.xiezuo.entity.FontEntity;

import java.util.List;

/**
 * Created by mzk10 on 2017/7/20.
 */

public class MakeDialogUtil {

    public static final String TAG = "MakeDialogUtil";

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

    public static void showInputDialog(Activity activity, String title, String defs, String Hints, int inputType, final OnInputCallBack callBack) {
        final Dialog tmpdialog = buildFullDialog(activity);
        tmpdialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        tmpdialog.setCancelable(true);
        LinearLayout ll_inputdialog = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.layout_dialog_input, null);
        TextView tv_inputdialog_title = ll_inputdialog.findViewById(R.id.tv_inputdialog_title);
        final EditText et_inputdialog_content = ll_inputdialog.findViewById(R.id.et_inputdialog_content);
        if (inputType != -1)
        {
            et_inputdialog_content.setInputType(inputType);
        }
        View btn_inputdialog_confirm = ll_inputdialog.findViewById(R.id.btn_inputdialog_confirm);
        View btn_inputdialog_cancel = ll_inputdialog.findViewById(R.id.btn_inputdialog_cancel);
        if (defs!=null)
        {
            et_inputdialog_content.setText(defs);
        }
        if (Hints!=null)
        {
            et_inputdialog_content.setHint(Hints);
        }
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

    public static void showInputDialog(Activity activity, String title, String defs, String Hints, final OnInputCallBack callBack) {
        showInputDialog(activity, title, defs, Hints, -1, callBack);
    }

    public interface OnInputCallBack
    {
        public void onCallBack(String param);
    }

    public static void showMenuDialog(Activity activity, String param[], final OnMenuCallBack callBack)
    {
        if (param == null || param.length == 0)
            return;
        final Dialog dialog = buildFullDialog(activity);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View menu = inflater.inflate(R.layout.layout_dialog_menu, null);
        LinearLayout ll_menu_box = menu.findViewById(R.id.ll_menu_box);
        for (int i = 0; i < param.length; i++)
        {
            View item = inflater.inflate(R.layout.layout_menu_item, null);
            Button btn = item.findViewById(R.id.btn_menu_item);
            btn.setText(param[i]);
            final int j = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callBack!=null){
                        callBack.onCallBack(j);
                    }
                    dialog.cancel();
                }
            });
            ll_menu_box.addView(item);
        }
        dialog.setContentView(menu);
        dialog.show();
    }

    public interface OnMenuCallBack
    {
        public void onCallBack(int i);
    }

    /**
     * 确认取消Dialog
     * @param activity
     * @param title
     *            Dialog标题文字，如果为空，则默认是”确定要这么做吗？“
     * @param confirmText
     *            Dialog左侧按钮文字 ，如果为空，则默认是”确定“
     * @param cancelText
     *            Dialog右侧按钮文字，如果为空则，默认是”取消“
     * @param linsener
     *            如果为空，则只显示一个左侧按钮并且不执行任何动作
     * @param showContent
     *            是否显示中间的列表
     * @param content
     *            如果显示列表，则需要传入中间列表的数据
     */
    public static void showConfirmDialog(Activity activity, String title, String confirmText, String cancelText, final OnDialogConfirmLinsener linsener, boolean showContent,
                                         String[] content) {
        final Dialog dialog = buildFullDialog(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_dialog_confirm, null);

        TextView tv_title = (TextView) view.findViewById(R.id.tv_dialog_title);
        Button btn_confirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_dialog_cancel);

        LinearLayout ll_content = (LinearLayout) view.findViewById(R.id.ll_dialog_content);

        if (showContent) {
            ll_content.setVisibility(View.VISIBLE);
            if (content != null) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                for (int i = 0; i < content.length; i++) {
                    TextView tv = new TextView(activity);
                    tv.setLayoutParams(params);
                    tv.setGravity(Gravity.CENTER);
                    tv.setTextColor(0xff000000);
                    tv.setTextSize(14);
                    tv.setText(content[i]);
                    ll_content.addView(tv);
                }
            }
        }

        if (title != null) {
            tv_title.setText(title);
        }
        if (confirmText != null) {
            btn_confirm.setText(confirmText);
        }

        if (cancelText != null) {
            btn_cancel.setText(cancelText);
        }

        if (linsener == null) {
            btn_cancel.setVisibility(View.GONE);
        }
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linsener != null) {
                    linsener.onConfirm(true);
                }
                dialog.cancel();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linsener != null) {
                    linsener.onConfirm(false);
                }
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    public static interface OnDialogConfirmLinsener {
        public void onConfirm(boolean isConfirm);
    }


    public static DownPreContrl showDownPre(Activity activity) {
        final Dialog downDialog = buildFullDialog(activity);

        View dialogView = LayoutInflater.from(activity).inflate(R.layout.layout_dialog_download, null);
        final ImageView progress_dialog_content = dialogView.findViewById(R.id.progress_content);
        final ImageView progress_dialog_bg = dialogView.findViewById(R.id.progress_bg);
        final TextView progress_dialog_int = dialogView.findViewById(R.id.progress_int);
        downDialog.setContentView(dialogView);

        DownPreContrl ctrl = new DownPreContrl() {
            @Override
            public void setPro(int pro) {
                int width = progress_dialog_bg.getWidth();
                float v = (float) width / 100f;
                float width_pro = v * pro;
                ViewGroup.LayoutParams layoutParams = progress_dialog_content.getLayoutParams();
                layoutParams.width = (int) width_pro;
                progress_dialog_content.setLayoutParams(layoutParams);
                progress_dialog_int.setText(pro+"%");
            }

            @Override
            public void close() {
                downDialog.cancel();
            }
        };

        downDialog.show();
        return ctrl;
    }

    public interface DownPreContrl
    {
        public void setPro(int pro);
        public void close();
    }

}
