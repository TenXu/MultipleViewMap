package com.seekhoney.library_gdmap.presenter;

import android.app.Application;
import android.text.TextUtils;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.seekhoney.library_gdmap.model.PositionEntity;
import com.seekhoney.library_gdmap.mvp.ILocateView;
import com.seekhoney.library_gdmap.rxError.ResponseErrorListener;
import com.seekhoney.library_gdmap.rxError.RxErrorHandler;
import com.seekhoney.library_gdmap.scope.ActivityScope;
import com.seekhoney.library_gdmap.utils.LogUtil;
import com.seekhoney.library_gdmap.utils.PermisionUtil;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by ryan on 18/2/20.
 */

@ActivityScope
public class Gdpresenter implements AMapLocationListener , GeocodeSearch.OnGeocodeSearchListener {
    private ILocateView view;
    private Application application;
    private ResponseErrorListener listener;
    private RxErrorHandler rxErrorHandler;
    private AMapLocationClient mLocationClient;
    private GeocodeSearch mGeocodeSearch;
    private AMapLocationClientOption option;
    private float radius;


    @Inject
    public Gdpresenter(Application application,
                       ILocateView view,
                       RxErrorHandler rxErrorHandler,
                       AMapLocationClient mLocationClient,
                       GeocodeSearch mGeocodeSearch) {

        this.view = view;
        this.application = application;
        this.rxErrorHandler = rxErrorHandler;
        this.mLocationClient = mLocationClient;
        this.mGeocodeSearch = mGeocodeSearch;
    }

    public void requestLocationPermission(PermisionUtil.RequestPermisionResult result) {
        PermisionUtil.getLocation(view.getRxPermission(), rxErrorHandler, result);
    }

    public void singleLocate(float radius) {

        this.radius = radius;
        if (option == null) {
            option = new AMapLocationClientOption();
            option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            option.setOnceLocation(true);
        }


        mLocationClient.setLocationListener(Gdpresenter.this);
        mGeocodeSearch.setOnGeocodeSearchListener(this);
        Observable.just(option)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AMapLocationClientOption>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AMapLocationClientOption aMapLocationClientOption) {
                        mLocationClient.setLocationOption(aMapLocationClientOption);
                        mLocationClient.startLocation();

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if(aMapLocation.getErrorCode() == 0){

            LogUtil.getIns("定位").i("定位成功 aMapLocation = "+ aMapLocation.toString());
            PositionEntity entity = new PositionEntity();
            entity.latitue = aMapLocation.getLatitude();
            entity.longitude = aMapLocation.getLongitude();

            if (!TextUtils.isEmpty(aMapLocation.getAddress())) {
                entity.address = aMapLocation.getAddress();
            }

            Observable.just(entity)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<PositionEntity>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(PositionEntity positionEntity) {
                            if(positionEntity != null && view.getMapView() != null){
                                LatLng current = new LatLng(positionEntity.latitue, positionEntity.longitude);
                                AMap aMap = view.getMapView().getMap();

                                CameraUpdate cameraUpate = CameraUpdateFactory.newLatLngZoom(
                                        current, aMap.getCameraPosition().zoom);
                                aMap.animateCamera(cameraUpate); //动画的方式更新地图状态
                                RegeocodeQuery regecodeQuery = new RegeocodeQuery(new LatLonPoint(
                                        positionEntity.latitue, positionEntity.longitude), radius, GeocodeSearch.AMAP);
                                mGeocodeSearch.getFromLocationAsyn(regecodeQuery);
                            }

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });

        }else {
            LogUtil.getIns("定位").i("定位失败");
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int resultCode) {

        if (resultCode == AMapException.CODE_AMAP_SUCCESS) {
            if (regeocodeResult != null
                    && regeocodeResult.getRegeocodeAddress() != null
                    && regeocodeResult != null) {

                if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null) {
                    List<PoiItem> poiItemList = regeocodeResult.getRegeocodeAddress().getPois();
                    view.notifyChanged(poiItemList);
                } else {
                    view.notifyChanged(null);
                }

            }
            LogUtil.getIns("定位").i("逆地理编码成功");
        }else {
            LogUtil.getIns("定位").i("逆地理编码失败");
        }

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
}
