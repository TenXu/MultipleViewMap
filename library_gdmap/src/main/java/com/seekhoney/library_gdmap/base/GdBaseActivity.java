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
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.seekhoney.library_gdmap.R;
import com.seekhoney.library_gdmap.R2;
import com.seekhoney.library_gdmap.logic.LocateLogic;
import com.seekhoney.library_gdmap.mvp.IView;
import com.seekhoney.library_gdmap.presenter.Gdpresenter;

import javax.inject.Inject;

/**
 * Created by ryan on 17/6/6.
 */

public abstract class GdBaseActivity extends AppCompatActivity implements IView
{
    @BindView(R2.id.cancle_gdmap_sdk)
    TextView tv_cancle;
    @BindView(R2.id.title_gdmap_sdk)
    TextView tv_title;  //标题
    @BindView(R2.id.right_gdmap_sdk)
    TextView tv_sure;   //确定
    @BindView(R2.id.flt_root_container)
    FrameLayout root_container;
    @BindView(R2.id.llt_back)
    LinearLayout llt_back; //返回图标
    @Inject
    Gdpresenter gdpresenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ButterKnife.bind(this);
        initViews();


    }

    @OnClick({R2.id.cancle_gdmap_sdk,R2.id.right_gdmap_sdk,R2.id.llt_back})
    public void onViewClick(View v){
        int id = v.getId();
        if(id == R.id.cancle_gdmap_sdk){
            setCancle();
        }else if(id == R.id.right_gdmap_sdk){
            setSure();
        }else if(id == R.id.llt_back){
            setBack();
        }
    }


    private void initViews(){
        //添加进根布局的FrameLayout,参数是wrap,wrap
        LayoutInflater.from(GdBaseActivity.this).inflate(getContentView(),root_container);

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
