package com.seekhoney.library_gdmap.module;

import android.app.Application;
import com.amap.api.fence.GeoFenceClient;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.seekhoney.library_gdmap.mvp.ILocateView;
import com.seekhoney.library_gdmap.rxError.ResponseErrorListener;
import com.seekhoney.library_gdmap.rxError.RxErrorHandler;
import com.seekhoney.library_gdmap.scope.ActivityScope;
import com.tbruyelle.rxpermissions2.RxPermissions;
import dagger.Module;
import dagger.Provides;

/**
 * Created by ryan on 18/2/21.
 * 这里不能使用@Singleton,否则报错, 是个大问题,待查
 *
 */

@Module
public class CommonModule
{
    private ILocateView view;

    public CommonModule(ILocateView view){
        this.view = view;
    }

    @ActivityScope
    @Provides
    public ILocateView provideIView(){
        return this.view;
    }

    @ActivityScope
    @Provides
    public Application provideApplication(){
        return view.getActivity().getApplication();
    }

    @ActivityScope
    @Provides
    public RxPermissions provideRxPermission(){
        return new RxPermissions(view.getActivity());
    }

    @ActivityScope
    @Provides
    public AMapLocationClient provideAMapLocationClient(){
        return new AMapLocationClient(view.getActivity());
    }

    @ActivityScope
    @Provides
    public GeocodeSearch provideGeocodeSearch(){
        return new GeocodeSearch(view.getActivity());
    }

    @ActivityScope
    @Provides
    public GeoFenceClient getGeoFenceClient(){
        return new GeoFenceClient(view.getActivity().getApplicationContext());
    }
    @ActivityScope
    @Provides
    public LatLngBounds.Builder getLatlngBounds() {
        return new  LatLngBounds.Builder();
    }

    //此处编译不能通过, 待理解了继续写
//    @Singleton
//    @Provides
//    public ResponseErrorListener provideResponseErrorListener(ResponseErrorListener listener){
//
//        return listener == null? ResponseErrorListener.EMPTY : listener;
//    }

    @ActivityScope
    @Provides
    public RxErrorHandler provideRxErrorHandler(Application application ){
        return RxErrorHandler.builder().
                with(application).
                responseErrorListener(ResponseErrorListener.EMPTY).
                build();
    }
}
