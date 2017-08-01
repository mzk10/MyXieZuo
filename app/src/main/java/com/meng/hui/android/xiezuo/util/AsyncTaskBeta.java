package com.meng.hui.android.xiezuo.util;

import android.os.Handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AsyncTaskBeta<Params,Progress,Result>
{
    
    private Status mStatus;
    private Handler mHandler;
    private Thread mThread;
    private boolean mCanceld;
    private Params[] mParams;
    
    public static ExecutorService pool = Executors.newFixedThreadPool(10);
    
    //private static final int onPostExecute = 0x80001;
    
    public AsyncTaskBeta(){
        mStatus = Status.PENDING;
        mHandler = new Handler();
    }
    
    public final void execute(Params... params){
        this.mParams = params;
        this.onPreExecute();
        mThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final Result result = doInBackground(mParams);
                if (!mCanceld)
                {
                    /*Message msg = new Message();
                    msg.what = onPostExecute;
                    msg.obj = result;
                    mHandler.sendMessage(msg);*/
                    mHandler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            onPostExecute(result);
                        }
                    });
                }
            }
        });
        pool.execute(mThread);
//        mThread.start();
    }
    
    protected abstract Result doInBackground(Params... param);
    
    /**
     * 线程开始
     */
    protected void onPreExecute(){
        mStatus = Status.RUNNING;
    }
    
    /**
     * 线程结束
     * @param result
     */
    protected void onPostExecute(Result result){
        mStatus = Status.FINISHED;
    }
    
    public final Status getStatus(){
        return this.mStatus;
    }
    
    public enum Status{
        /**
         * Indicates that the task has not been executed yet.
         */
        PENDING,
        /**
         * Indicates that the task is running.
         */
        RUNNING,
        /**
         * Indicates that {@link AsyncTaskBeta#onPostExecute} has finished.
         */
        FINISHED,
    }
    
    /*@SuppressLint("HandlerLeak")
    private class MHandler extends Handler{
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if (msg.what == onPostExecute)
            {
                onPostExecute((Result)msg.obj);
            }
        }
    }*/
    
    public final void cancel(boolean flag){
        mCanceld = flag;
    }
}
