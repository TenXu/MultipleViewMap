package com.seekhoney.library_gdmap.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import butterknife.ButterKnife;
import com.seekhoney.library_gdmap.R;
import com.seekhoney.library_gdmap.logic.LocateLogic;

/**
 * Created by ryan on 17/6/6.
 */

public abstract class GdBaseActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutResID = getContentView();
        if(layoutResID != 0){
            setContentView(layoutResID);
            ButterKnife.bind(this);
        }

    }





    /**
     * 获取子类的布局地址,加入FrameLayout;
     * */
    public abstract int getContentView();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        notifyNetWork();
    }

    protected void notifyNetWork(){
        if(!LocateLogic.isNetworkAvailable(this)){
            Toast.makeText(this,getString(R.string.locate_fail), Toast.LENGTH_SHORT).show();
        }
    }



}
