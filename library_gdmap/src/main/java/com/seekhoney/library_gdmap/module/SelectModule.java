package com.seekhoney.library_gdmap.module;

import android.content.Context;
import com.seekhoney.library_gdmap.listener.OnRecItemclickListener;
import com.seekhoney.library_gdmap.model.PoiIEntity;
import com.seekhoney.library_gdmap.mvp.ISelect;
import com.seekhoney.library_gdmap.scope.ActivityScope;
import dagger.Module;
import dagger.Provides;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ryan on 18/3/23.
 */

@Module
public class SelectModule
{
    ISelect view;

    public SelectModule(ISelect view){
        this.view = view;
    }


    @ActivityScope
    @Provides
    ISelect provideISelect(){
        return view;
    }

    @ActivityScope
    @Provides
    Context getContext(){
        return view.getActivity();
    }

    @ActivityScope
    @Provides
    List<PoiIEntity> provideDates(){
        List<PoiIEntity> list = new LinkedList<>();
        return list;
    }

    @ActivityScope
    @Provides
    OnRecItemclickListener provideRecItemListener(){
        return view.getRecItemListener();
    }


}
