package com.seekhoney.library_gdmap.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.seekhoney.library_gdmap.R;
import com.seekhoney.library_gdmap.logic.LocateLogic;

/**
 * Created by ryan on 17/6/6.
 */

public abstract class BaseActivity extends AppCompatActivity
{
    private TextView tv_cancle; //取消
    private TextView tv_title;  //标题
    private TextView tv_sure;   //确定
    private FrameLayout root_container;
    private LinearLayout llt_back; //返回图标


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        initViews();

    }

    private void initViews(){
        tv_cancle = (TextView) findViewById(R.id.cancle_gdmap_sdk);
        tv_cancle.setOnClickListener(listener);
        tv_title = (TextView) findViewById(R.id.title_gdmap_sdk);
        tv_sure = (TextView) findViewById(R.id.right_gdmap_sdk);
        tv_sure.setOnClickListener(listener);
        root_container = (FrameLayout) findViewById(R.id.flt_root_container);
        llt_back = (LinearLayout) findViewById(R.id.llt_back);
        llt_back.setOnClickListener(listener);

        //添加进根布局的FrameLayout,参数是wrap,wrap
        LayoutInflater.from(BaseActivity.this).inflate(getContentView(),root_container);
//        View childView =  LayoutInflater.from(BaseActivity.this).inflate(getContentView(),null);
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        root_container.addView(childView,params);

    }

    /**
     * 获取子类的布局地址,加入FrameLayout;
     * */
    public abstract int getContentView();

    protected void setBarTitle(String title){
        if(title != null && tv_title != null){
            tv_title.setText(title);
        }
    }

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

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(id == R.id.cancle_gdmap_sdk){
                setCancle();
            }else if(id == R.id.right_gdmap_sdk){
                setSure();
            }else if(id == R.id.llt_back){
                setBack();
            }
        }
    }; //取消与确定的监听

    protected void showSureCancel(){
        if(tv_sure != null){
            tv_sure.setVisibility(View.VISIBLE);
        }

        if(tv_cancle != null){
            tv_cancle.setVisibility(View.VISIBLE);
        }
    }

    protected void hideSureCancel(){
        if(tv_sure != null){
            tv_sure.setVisibility(View.GONE);
        }

        if(tv_cancle != null){
            tv_cancle.setVisibility(View.GONE);
        }
    }

    protected void showBack(){
        if(llt_back != null){
            llt_back.setVisibility(View.VISIBLE);
        }
    }

    protected void hideBack(){
        if(llt_back != null){
            llt_back.setVisibility(View.GONE);
        }
    }

    protected void notifyNetWork(){
        if(!LocateLogic.isNetworkAvailable(this)){
            Toast.makeText(this,getString(R.string.locate_fail), Toast.LENGTH_SHORT).show();
        }
    }



    protected abstract void setCancle();

    protected abstract void setSure();

    protected abstract void setBack();
}
