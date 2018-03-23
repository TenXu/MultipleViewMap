package com.seekhoney.library_gdmap.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import com.seekhoney.library_gdmap.constant.Const;
import com.seekhoney.library_gdmap.mvp.ISelect;

import javax.inject.Inject;

/**
 * Created by ryan on 18/3/23.
 */

public class SelectPresenter
{
    ISelect view;

    @Inject
    public SelectPresenter(ISelect view ){
        this.view = view;
    }

    public void addSearchHistory(String history){

        SharedPreferences sp = view.getActivity().getSharedPreferences(Const.SP_SELECT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();


    }

}
