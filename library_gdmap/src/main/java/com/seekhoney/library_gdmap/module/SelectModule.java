package com.seekhoney.library_gdmap.module;

import com.seekhoney.library_gdmap.mvp.ISelect;
import com.seekhoney.library_gdmap.scope.ActivityScope;
import dagger.Module;
import dagger.Provides;

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


}
