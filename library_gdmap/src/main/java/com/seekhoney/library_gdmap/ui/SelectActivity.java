package com.seekhoney.library_gdmap.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.seekhoney.library_gdmap.R;
import com.seekhoney.library_gdmap.R2;
import com.seekhoney.library_gdmap.adapter.SelectRecAdapter;
import com.seekhoney.library_gdmap.base.GdBaseActivity;
import com.seekhoney.library_gdmap.component.DaggerSelectComponent;
import com.seekhoney.library_gdmap.listener.OnRecItemclickListener;
import com.seekhoney.library_gdmap.model.PoiIEntity;
import com.seekhoney.library_gdmap.module.SelectModule;
import com.seekhoney.library_gdmap.mvp.ISelect;
import com.seekhoney.library_gdmap.presenter.SelectPresenter;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;
import java.util.LinkedList;

/**
 * Created by ryan on 18/3/22.
 */

public class SelectActivity extends GdBaseActivity implements ISelect,OnRecItemclickListener,TextWatcher
{
    @BindView(R2.id.edit_search)
    AutoCompleteTextView edit_search;
    @BindView(R2.id.delete_search)
    ImageView delete_search;
    @BindView(R2.id.tv_quit_search)
    TextView quit_search;
    @BindView(R2.id.rec_select)
    RecyclerView rec_select;
    @Inject
    SelectRecAdapter recAdapter;
    @Inject
    SelectPresenter presenter;


    @Override
    public int getContentView() {
        return R.layout.activity_select;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerSelectComponent.builder()
                .selectModule(new SelectModule(this))
                .build()
                .inject(this);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter = null;
    }

    @Override
    public Activity getActivity() {
        return this;
    }


    @OnClick({R2.id.delete_search,R2.id.tv_quit_search})
    public void onViewClick(View v){
        int id = v.getId();
        if(id == R.id.delete_search){
            edit_search.setText("");
        }else if(id == R.id.tv_quit_search){
            finish();
        }
    }

    @Override
    public void showHistoryDate(LinkedList<PoiIEntity> list) {
        if(list != null && list.size() > 0){
            recAdapter.setDates(list);
            recAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void recItemCallback(PoiIEntity entity) {

        if(entity != null){
            presenter.addSearchHistory(entity);
            EventBus.getDefault().post(entity);
            finish();
        }
    }

    @Override
    public OnRecItemclickListener getRecItemListener() {
        return this;
    }


    @Override
    public void afterTextChanged(Editable s) {

        String keyWord = edit_search.getText().toString().trim();
        if(!TextUtils.isEmpty(keyWord)){
            presenter.doSearchQuery(keyWord);
        }

    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        String target = s.toString().trim();
        presenter.SearchQuery(target);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }



    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    private void init(){
        edit_search.addTextChangedListener(this);
        rec_select.setAdapter(recAdapter);
        rec_select.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        presenter.showSearchHistory();
    }


}
