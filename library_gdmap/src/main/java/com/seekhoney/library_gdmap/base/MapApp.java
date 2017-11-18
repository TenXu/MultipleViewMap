package com.seekhoney.library_gdmap.base;

import android.app.Application;
import com.bumptech.glide.Glide;

/**
 * Created by ryan on 17/6/10.
 */

public class MapApp extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();
        Glide.with(this);
    }




}
