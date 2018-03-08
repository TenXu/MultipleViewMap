package com.seekhoney.library_gdmap.utils;

import android.util.Log;

/**
 * Created by ryan on 17/6/6.
 */

public class LogUtil
{
    private boolean isShow = true;
    private static LogUtil ins;
    private String tag;

    private LogUtil(String tag){
        this.tag = tag;
    }

    public synchronized static LogUtil getIns(String tag){

        if(ins == null){
            ins = new LogUtil(tag);
        }
        return ins;
    }

    public void setShow(boolean isShow){
        this.isShow = isShow;
    }

    public void i(String msg){
        if(isShow){
            Log.i(tag,msg);
        }
    }

    public void e(String msg){
        if(isShow){
            Log.e(tag,msg);
        }
    }
}
