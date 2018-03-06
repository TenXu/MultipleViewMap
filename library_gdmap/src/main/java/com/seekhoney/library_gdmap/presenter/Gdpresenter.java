package com.seekhoney.library_gdmap.presenter;

import android.app.Application;
import com.seekhoney.library_gdmap.mvp.IView;
import com.seekhoney.library_gdmap.rxError.ResponseErrorListener;
import com.seekhoney.library_gdmap.rxError.RxErrorHandler;
import com.seekhoney.library_gdmap.scope.ActivityScope;
import com.seekhoney.library_gdmap.utils.PermisionUtil;

import javax.inject.Inject;

/**
 * Created by ryan on 18/2/20.
 */

@ActivityScope
public class Gdpresenter
{
    private IView view;
    private Application application;
    private ResponseErrorListener listener;
    private RxErrorHandler rxErrorHandler;

    @Inject
    public Gdpresenter(Application application ,
                       IView view ,
                       RxErrorHandler rxErrorHandler)
    {

        this.view = view;
        this.application = application;
        this.rxErrorHandler = rxErrorHandler;
    }

    public void requestLocationPermission(PermisionUtil.RequestPermisionResult result){
        PermisionUtil.getLocation(view.getRxPermission(), rxErrorHandler, result);
    }







}
