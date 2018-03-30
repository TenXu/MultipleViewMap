package com.seekhoney.library_gdmap.presenter;

import android.app.Application;
import android.text.TextUtils;
import android.widget.Toast;
import com.amap.api.fence.GeoFence;
import com.amap.api.fence.GeoFenceClient;
import com.amap.api.fence.GeoFenceListener;
import com.amap.api.location.*;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.seekhoney.library_gdmap.constant.Const;
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
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ryan on 18/2/20.
 */

@ActivityScope
public class Gdpresenter implements AMapLocationListener , GeocodeSearch.OnGeocodeSearchListener,
                                    GeoFenceListener , AMap.OnCameraChangeListener {
    private ILocateView view;
    private Application application;
    private ResponseErrorListener listener;
    private RxErrorHandler rxErrorHandler;

    /**定位类对象*/
    private AMapLocationClient mLocationClient;

    /**逆地理编码类对象*/
    private GeocodeSearch mGeocodeSearch;

    /**定位类配置*/
    private AMapLocationClientOption option;

    /**围栏半径*/
    private float radius;

    /**地理围栏类对象*/
    private GeoFenceClient fenceClient;

    /**定位后获取的当前经纬度*/
    private LatLng currentLocation;

    /**记录已经添加成功的围栏*/
    private HashMap<String, GeoFence> fenceMap = new HashMap<String, GeoFence>();



    @Inject
    public Gdpresenter(Application application,
                       ILocateView view,
                       RxErrorHandler rxErrorHandler,
                       AMapLocationClient mLocationClient,
                       GeocodeSearch mGeocodeSearch,
                         GeoFenceClient fenceClient) {

        this.view = view;
        this.application = application;
        this.rxErrorHandler = rxErrorHandler;
        this.mLocationClient = mLocationClient;
        this.mGeocodeSearch = mGeocodeSearch;
        this.fenceClient = fenceClient;

        this.view.getMapView().getMap().setOnCameraChangeListener(this); //地图状态发送变化
        this.mLocationClient.setLocationListener(this);
        this.mGeocodeSearch.setOnGeocodeSearchListener(this);
        this.fenceClient.setGeoFenceListener(this);//创建pendingIntent
        fenceClient.setActivateAction(GeoFenceClient.GEOFENCE_IN); //设置地理围栏的触发行为,默认为进入
    }

    public void requestLocationPermission(PermisionUtil.RequestPermisionResult result) {
        PermisionUtil.getLocation(view.getRxPermission(), rxErrorHandler, result);
    }

    /**
     * 单次定位,回调 see{ onLocationChanged } 返回定位值
     * @param radius 围栏半径, 同时也是逆地理编码的范围
     * */
    public void singleLocate(float radius) {

        this.radius = radius;
        if (option == null) {
            option = new AMapLocationClientOption();
            option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            option.setOnceLocation(true);
        }

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
    public void onLocationChanged(AMapLocation aMapLocation) {  //单次定位后的回调

        if(aMapLocation.getErrorCode() == 0){

            LogUtil.getIns("定位").i("定位成功 aMapLocation = "+ aMapLocation.toString());
            PositionEntity entity = new PositionEntity();
            entity.latitue = aMapLocation.getLatitude();
            entity.longitude = aMapLocation.getLongitude();

            if (!TextUtils.isEmpty(aMapLocation.getAddress())) {
                entity.address = aMapLocation.getAddress();
            }

            //获取当前经纬度后进行逆地理编码
            Observable.just(entity)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<PositionEntity>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(PositionEntity positionEntity) {
                            if(positionEntity != null && view.getMapView() != null){
                                currentLocation = new LatLng(positionEntity.latitue, positionEntity.longitude);
                                moveCamera(currentLocation);
                                Regeocode (currentLocation);
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
    public void onCameraChangeFinish(CameraPosition cameraPosition) { //地图移动完成回调
        currentLocation = cameraPosition.target;
        addRoundFence();
        Regeocode (currentLocation);
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

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onGeoFenceCreateFinished(List<GeoFence> list, int errorCode, String customId)  //围栏添加完毕
    {
        if(errorCode == 0){
            GeoFence [] geoFences = new GeoFence[list.size()];
            list.toArray(geoFences);
            Observable.fromArray(geoFences).
                    map(new Function<GeoFence, GeoFence>() {
                        @Override
                        public GeoFence apply(GeoFence geoFence) throws Exception {
                            return geoFence;
                        }
                    }).subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Observer<GeoFence>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(GeoFence geoFence) {
                    drawFence(geoFence);
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });

        }

    }

    /**逆地理编码*/
    public void  Regeocode (LatLng latLng){
        RegeocodeQuery regecodeQuery = new RegeocodeQuery(new LatLonPoint(
                latLng.latitude, latLng.longitude), radius, GeocodeSearch.AMAP);
        mGeocodeSearch.getFromLocationAsyn(regecodeQuery);
    }

    /**移动地图指针*/
    public void moveCamera(LatLng latLng){
        AMap aMap = view.getMapView().getMap();
        CameraUpdate cameraUpate = CameraUpdateFactory.newLatLngZoom(
                latLng, aMap.getCameraPosition().zoom);
        aMap.animateCamera(cameraUpate); //动画的方式更新地图状态

    }



    /**
     * 添加圆形围栏,默认直径500m
     * customId不能重复 ,
     * 在回调 see{onGeoFenceCreateFinished()}中绘制图形
     */
    private void addRoundFence() {
        String customId = "default" + Math.random(); //不能每次相同
        if (null == currentLocation) {
            Toast.makeText(view.getActivity(), "显示出错", Toast.LENGTH_SHORT).show();
            return;
        }

        DPoint centerPoint = new DPoint(currentLocation.latitude,currentLocation.longitude);
        fenceClient.addGeoFence(centerPoint, radius, customId);
    }


    private void drawFence(GeoFence fence) {
        switch (fence.getType()) {
            case GeoFence.TYPE_ROUND:
            case GeoFence.TYPE_AMAPPOI:
                drawCircle(fence);
                break;
            case GeoFence.TYPE_POLYGON:
            case GeoFence.TYPE_DISTRICT:
                drawPolygon(fence);
                break;
            default:
                break;
        }

    }

    private void drawCircle(GeoFence fence) {
        LatLng center = new LatLng(fence.getCenter().getLatitude(),
                fence.getCenter().getLongitude());
        float radius = fence.getRadius();

        //清除上一个circle对象,还有别的办法吗?
        view.getMapView().getMap().clear(true);
        //重新绘制图中心的标记,也就是当前定位的位置
//        setCenterMaker(center,radius,primaryUrl);
        view.setCenterMark(center,radius);
    }

    private void drawPolygon(GeoFence fence) {
        final List<List<DPoint>> pointList = fence.getPointList();
        if (null == pointList || pointList.isEmpty()) {
            return;
        }
        for (List<DPoint> subList : pointList) {
            List<LatLng> lst = new ArrayList<LatLng>();

            PolygonOptions polygonOption = new PolygonOptions();
            for (DPoint point : subList) {
                lst.add(new LatLng(point.getLatitude(), point.getLongitude()));
                view.getBounds().include(
                        new LatLng(point.getLatitude(), point.getLongitude()));
            }
            polygonOption.addAll(lst);

            polygonOption.strokeColor(Const.STROKE_COLOR)
                    .fillColor(Const.FILL_COLOR).strokeWidth(Const.STROKE_WIDTH);
            view.getMapView().getMap().addPolygon(polygonOption);
        }
    }



}
