package com.meng.hui.android.xiezuo.util.pop;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import com.meng.hui.android.xiezuo.R;
import com.meng.hui.android.xiezuo.util.pop.holder.BaseHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PopWindowUtil implements DialogInterface.OnDismissListener {

    private static PopWindowUtil instance;

    private Map<String, PopWindow> windowMap;
    private Queue<PopWindow> windowQueue;
    private boolean isNext;

    private PopWindowUtil() {
    }

    public static PopWindowUtil getInstance() {
        if (instance == null) {
            instance = new PopWindowUtil();
            instance.isNext = true;
        }
        if (instance.windowQueue == null) {
            instance.windowQueue = new ConcurrentLinkedQueue<>();
        }
        if (instance.windowMap == null){
            instance.windowMap = new HashMap<>();
        }
        return instance;
    }

    public void insertPop(PopWindow window) {
        windowQueue.offer(window);
        if (windowQueue.size() == 1) {
            start();
        }
    }

    public void insertPop(String flag, PopWindow window) {
        windowMap.put(flag, window);
        insertPop(window);
    }

    public PopWindow getPop(String flag){
        return windowMap.get(flag);
    }

    public void start() {
        PopWindow popWindow = windowQueue.peek();
        if (popWindow!=null){
            if (popWindow.dead){
                windowQueue.poll();
                start();
            }else {
                popWindow.show();
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        windowQueue.poll();
        if (isNext) {
            start();
        } else {
            //windowQueue.clear();
            isNext = true;
        }
    }

    public void setIsNext(boolean isNext) {
        this.isNext = isNext;
    }

    /**
     * Builder
     */
    public static class Builder {

        private final Activity activity;

        private boolean isCover = true;
        private boolean cancelable = true;
        private boolean hasOpenAnim = true;
        private boolean hasCloseAnim = true;
        private PopWindow popWidow;

        public Builder(@NonNull Activity activity) {
            this.activity = activity;
        }

        public Builder setCover(boolean cover) {
            isCover = cover;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setHasOpenAnim(boolean hasOpenAnim) {
            this.hasOpenAnim = hasOpenAnim;
            return this;
        }

        public Builder setHasCloseAnim(boolean hasCloseAnim) {
            this.hasCloseAnim = hasCloseAnim;
            return this;
        }

        public void closeWindow() {
            if (popWidow != null) {
                popWidow.close();
            }
        }

        public PopWindow create(final BaseHolder holder) {
            final Dialog dialog = buildFullDialog();
            final FrameLayout fl_container = new FrameLayout(activity);
            if (isCover) {
                fl_container.setBackgroundColor(0x7F000000);
            }
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            fl_container.addView(holder.view);

            ViewGroup.LayoutParams container_params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.addContentView(fl_container, container_params);

            final PopWindow popWindow = new PopWindow() {
                @Override
                public void show() {
                    if (hasOpenAnim) {
                        TranslateAnimation up_in = new TranslateAnimation(0, 0, 300, 0);
                        up_in.setDuration(100);
                        AlphaAnimation fade_in = new AlphaAnimation(0, 1);
                        fade_in.setDuration(100);
                        fl_container.startAnimation(fade_in);
                        holder.view.startAnimation(up_in);
                        dialog.show();
                    } else {
                        dialog.show();
                    }
                }

                @Override
                public void close() {
                    if (hasCloseAnim) {
                        TranslateAnimation down_out = new TranslateAnimation(0, 0, 0, 300);
                        down_out.setDuration(100);
                        down_out.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                dialog.dismiss();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                        AlphaAnimation fade_out = new AlphaAnimation(1, 0);
                        fade_out.setDuration(100);
                        fl_container.startAnimation(fade_out);
                        holder.view.startAnimation(down_out);
                    } else {
                        dialog.dismiss();
                    }
                }
            };
            popWindow.setHolder(holder);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    PopWindowUtil.instance.onDismiss(dialog);
                    fl_container.removeAllViews();
                }
            });

            dialog.setCancelable(cancelable);
            if (cancelable) {
                fl_container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popWindow.close();
                    }
                });
            }
            this.popWidow = popWindow;
            return popWindow;
        }

        private Dialog buildFullDialog() {
            Dialog dialog;
            dialog = new Dialog(activity, R.style.MenuTheme);
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }

    }

    public abstract static class PopWindow {
        private boolean dead;
        private BaseHolder holder;

        public void setDead(boolean dead) {
            this.dead = dead;
        }

        public BaseHolder getHolder() {
            return holder;
        }

        public void setHolder(BaseHolder holder) {
            this.holder = holder;
        }

        abstract void show();
        abstract void close();
    }

}
