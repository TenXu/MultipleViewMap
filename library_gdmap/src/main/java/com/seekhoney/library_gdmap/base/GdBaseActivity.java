package com.seekhoney.library_gdmap.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import butterknife.ButterKnife;
import com.seekhoney.library_gdmap.R;
import com.seekhoney.library_gdmap.logic.LocateLogic;
import com.seekhoney.library_gdmap.presenter.Gdpresenter;

import javax.inject.Inject;

/**
 * Created by ryan on 17/6/6.
 */

public abstract class GdBaseActivity extends AppCompatActivity
{

    @Inject
    protected Gdpresenter gdpresenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_base);
//        initViews();
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

//    protected void setBarTitle(String title){
//        if(title != null && tv_title != null){
//            tv_title.setText(title);
//        }
//    }

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



//    protected void showSureCancel(){
//        if(tv_sure != null){
//            tv_sure.setVisibility(View.VISIBLE);
//        }
//
//        if(tv_cancle != null){
//            tv_cancle.setVisibility(View.VISIBLE);
//        }
//    }
//
//    protected void hideSureCancel(){
//        if(tv_sure != null){
//            tv_sure.setVisibility(View.GONE);
//        }
//
//        if(tv_cancle != null){
//            tv_cancle.setVisibility(View.GONE);
//        }
//    }
//
//    protected void showBack(){
//        if(llt_back != null){
//            llt_back.setVisibility(View.VISIBLE);
//        }
//    }
//
//    protected void hideBack(){
//        if(llt_back != null){
//            llt_back.setVisibility(View.GONE);
//        }
//    }

    protected void notifyNetWork(){
        if(!LocateLogic.isNetworkAvailable(this)){
            Toast.makeText(this,getString(R.string.locate_fail), Toast.LENGTH_SHORT).show();
        }
    }



    protected abstract void setCancle();

    protected abstract void setSure();

    protected abstract void setBack();
}
