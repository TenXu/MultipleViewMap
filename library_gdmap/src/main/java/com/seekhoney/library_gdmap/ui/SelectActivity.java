package com.seekhoney.library_gdmap.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.OnClick;
import com.seekhoney.library_gdmap.R;
import com.seekhoney.library_gdmap.R2;
import com.seekhoney.library_gdmap.adapter.SelectRecAdapter;
import com.seekhoney.library_gdmap.base.GdBaseActivity;
import com.seekhoney.library_gdmap.mvp.ISelect;

import javax.inject.Inject;

/**
 * Created by ryan on 18/3/22.
 */

public class SelectActivity extends GdBaseActivity implements ISelect
{
    @BindView(R2.id.edit_search)
    AutoCompleteTextView edit_search;
    @BindView(R2.id.delete_search)
    ImageView delete_search;
    @BindView(R2.id.tv_quit_search)
    ImageView quit_search;
    @BindView(R2.id.rec_select)
    RecyclerView rec_select;
    @Inject
    SelectRecAdapter recAdapter;


    @Override
    public int getContentView() {
        return R.layout.activity_select;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public Activity getActivity() {
        return this;
    }




    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }


    @OnClick({R2.id.edit_search,R2.id.delete_search,R2.id.tv_quit_search})
    public void onViewClick(View v){

    }
}
