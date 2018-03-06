package com.seekhoney.library_gdmap.ui;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import com.amap.api.fence.GeoFence;
import com.amap.api.fence.GeoFenceClient;
import com.amap.api.fence.GeoFenceListener;
import com.amap.api.location.DPoint;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.*;
import com.amap.api.services.core.PoiItem;
import com.seekhoney.library_gdmap.R;
import com.seekhoney.library_gdmap.R2;
import com.seekhoney.library_gdmap.adapter.PoiItemAdapter;
import com.seekhoney.library_gdmap.base.GdBaseActivity;
import com.seekhoney.library_gdmap.component.DaggerCommonComponent;
import com.seekhoney.library_gdmap.constant.Const;
import com.seekhoney.library_gdmap.constant.TestConst;
import com.seekhoney.library_gdmap.listener.OnImgLoadFinish;
import com.seekhoney.library_gdmap.listener.OnLocationGetListener;
import com.seekhoney.library_gdmap.logic.LocateLogic;
import com.seekhoney.library_gdmap.model.PositionEntity;
import com.seekhoney.library_gdmap.module.CommonModule;
import com.seekhoney.library_gdmap.task.LocationTask;
import com.seekhoney.library_gdmap.task.RegeocodeTask;
import com.seekhoney.library_gdmap.utils.LogUtil;
import com.seekhoney.library_gdmap.utils.Utils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ryan on 17/6/6.
 */

public class GdmapActivity extends GdBaseActivity implements AMap.OnCameraChangeListener,
        AMap.OnMapLoadedListener, OnLocationGetListener, View.OnClickListener, GeoFenceListener {

    @BindView(R2.id.map)
    MapView mMapView;   //地图view
    @BindView(R2.id.listview_poi)
    ListView listView;
    @Inject
    RxPermissions rxPermissions;

    private AMap mAmap;

    private Marker mPositionMark;          //定位的那个点

    private LatLng mStartPosition;         //存储经纬度坐标值的类

    private RegeocodeTask mRegeocodeTask;  //封装的逆地理编码类

    private LocationTask mLocationTask;   //封装的定位类

    private ImageView mLocationImage;

    private LinearLayout llt_relocate;


    //第一次进入时在onLocationGet回调中添加围栏,其他都在onGeoFenceCreateFinished中添加
    private boolean mIsFirst = true;

    // 地理围栏客户端
    private GeoFenceClient fenceClient = null;

    // 记录已经添加成功的围栏,有这个必要吗?
    private HashMap<String, GeoFence> fenceMap = new HashMap<String, GeoFence>();

    // 当前的坐标点集合，主要用于进行地图的可视区域的缩放
    private LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

    private PoiItemAdapter poiItemAdapter;

    private List<PoiItem> poiItems = new ArrayList<>();

    private PoiItem poiItem;//选择的兴趣点

    List<GeoFence> fenceList = new ArrayList<GeoFence>();

    private String TAG = "GDMAP";

    private LogUtil logUtil = LogUtil.getIns(TAG);

    /**
     * logo ,imgHead,roundedBitmapDrawable:
     * 由于glide对同一个url加载成功一次后就不再回调,而Marker是不断的清除与添加,所以用这个临时解决方案
     *
     * */

    private View logo;  //中心点Marker图标

    private ImageView imgHead;//中心点Marker头像

    private RoundedBitmapDrawable roundedBitmapDrawable;

    private Location location; //初始化,允许传入初始值

    private String primaryUrl; //初始,头像url

    private String title; //初始化,title

    private GdmapActivity ins;

    private float radius = 500f;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    drawFence2Map();
                    break;
                case 1:
                    int errorCode = msg.arg1;
                    Toast.makeText(getApplicationContext(),
                            "添加围栏失败 " + errorCode, Toast.LENGTH_SHORT).show();
                    break;
                case Const.LOAD_IMG:
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerCommonComponent.builder().
                commonModule(new CommonModule(this)).
                build().inject(this);
        logUtil.setShow(true);
        ins = this;
        init(savedInstanceState);
        intData();
        hideBack();
        showSureCancel();
        mLocationTask = new LocationTask(getApplicationContext());
        mLocationTask.setOnLocationGetListener(this);
        mRegeocodeTask = new RegeocodeTask(getApplicationContext());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.removeMarkers();
        mMapView.onDestroy();
        mLocationTask.onDestroy();
        if (null != fenceClient) {
            fenceClient.removeGeoFence();
        }
    }

    @Override
    public int getContentView() {
        return R.layout.activity_admin;
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.llt_relocate) {
            mLocationTask.startSingleLocate();
            logUtil.e("onClick-startSingleLocate");
        }
    }

    //取消

    @Override
    protected void setCancle() {
        finish();
    }

    //设置


    @Override
    protected void setSure() {
        TestConst.latLng = mStartPosition;
        if(poiItem != null){
            LocateLogic.getIns(ins).selectedPoiCallback(poiItem);
        }

        finish();
    }

    @Override
    protected void setBack() {

    }

    /**
     * 围栏添加完毕
     */
    @Override
    public void onGeoFenceCreateFinished(List<GeoFence> geoFenceList, int errorCode, String customId) {

        Message msg = Message.obtain();
        if (errorCode == GeoFence.ADDGEOFENCE_SUCCESS) {
            fenceList = geoFenceList;
            msg.obj = customId;
            msg.what = 0;
        } else {
            msg.arg1 = errorCode;
            msg.what = 1;
        }
        mHandler.sendMessage(msg);
        logUtil.e("onGeoFenceCreateFinished-" + "围栏添加完毕");

    }

    /**
     * 高德原生
     *
     * */
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

        // TODO

    }


    /**
     * 地图状态改变完成时,第一次加载完和状态改变时都会被调用
     * 高德原生
     */
    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {

//        LatLng exactPostion = cameraPosition.target;
        mStartPosition = cameraPosition.target;
        mRegeocodeTask.setOnLocationGetListener(this);
        mRegeocodeTask
                .search(mStartPosition.latitude, mStartPosition.longitude,50); //逆向地理编码
        //增加围栏
        addRoundFence();
        logUtil.e("onCameraChangeFinish-" + "地图状态改变完成");

    }

    /**
     * 地图加载完成
     */
    @Override
    public void onMapLoaded() {

        setCenterMaker(null,0,primaryUrl);  //为了加载图标,必须在这里加载,否则加载不出来
        mLocationTask.startSingleLocate();      //开启单次定位
        logUtil.e("onMapLoaded-" + "地图加载完成");

    }

    /**
     * LocationTask中定位完成后调用了此方法
     */
    @Override
    public void onLocationGet(PositionEntity entity) {

        if (mIsFirst && location != null) {
                mStartPosition = new LatLng(location.getLatitude(), location.getLongitude());
//            addRoundFence();
            mIsFirst = false;

        }else {
            mStartPosition = new LatLng(entity.latitue, entity.longitude);
        }


        CameraUpdate cameraUpate = CameraUpdateFactory.newLatLngZoom(
                mStartPosition, mAmap.getCameraPosition().zoom);
        mAmap.animateCamera(cameraUpate); //动画的方式更新地图状态


        logUtil.e("onLocationGet-" + "定位完成"+"--mStartPosition="+mStartPosition.toString());
    }


    /**
     * OnLocationGetListener 接口的回调,返回值不能满足需求,废弃
     */
    @Override
    public void onRegecodeGet(PositionEntity entity) {

    }


    @Override
    public void onRegecodeCallback(List<PoiItem> poiItems) {


//        for(int i = 0; i< poiItems.size(); i++){
//            LatLonPoint point = poiItems.get(i).getLatLonPoint();
//            String address = poiItems.get(i).getSnippet();
//            String title = poiItems.get(i).getTitle();
//
//        }
        this.poiItems = poiItems;
        if (poiItems != null && poiItems.size() > 0) {
            poiItemAdapter.setItems(poiItems);
            poiItemAdapter.setSelectedPosition(0);
            poiItemAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            poiItem = poiItems.get(0);
        }


    }


    Object lock = new Object();

    void drawFence2Map() {
        new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (lock) {
                        if (null == fenceList || fenceList.isEmpty()) {
                            return;
                        }
                        for (GeoFence fence : fenceList) {
                            if (fenceMap.containsKey(fence.getFenceId())) {
                                continue;
                            }
                            drawFence(fence);
                            fenceMap.put(fence.getFenceId(), fence);
                        }
                    }
                } catch (Throwable e) {

                }
            }
        }.start();
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
        mAmap.clear(true);
        //重新绘制图中心的标记,也就是当前定位的位置
        setCenterMaker(center,radius,primaryUrl);
    }

    private void addCircle(LatLng lat, float radius){

        // 绘制一个圆形
        mAmap.addCircle(new CircleOptions().center(lat)
                .radius(radius).strokeColor(Const.STROKE_COLOR)
                .fillColor(Const.FILL_COLOR).strokeWidth(Const.STROKE_WIDTH));
        boundsBuilder.include(lat);
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
                boundsBuilder.include(
                        new LatLng(point.getLatitude(), point.getLongitude()));
            }
            polygonOption.addAll(lst);

            polygonOption.strokeColor(Const.STROKE_COLOR)
                    .fillColor(Const.FILL_COLOR).strokeWidth(Const.STROKE_WIDTH);
            mAmap.addPolygon(polygonOption);
        }
    }


    /**
     * 设置地图中心的标记,也是定位的当前位置
     */
    private void setCenterMaker(final LatLng latLng , final float radius, String url) {

        logo = getLayoutInflater().inflate(R.layout.view_setted_logo, null);
        imgHead = (ImageView) logo.findViewById(R.id.head_setmap);
        if(roundedBitmapDrawable != null){
            imgHead.setImageDrawable(roundedBitmapDrawable);
            Log.e("load","center-2:"+"imgHead="+imgHead+"--roundedBitmapDrawable="+roundedBitmapDrawable);
        }
        if(latLng != null){

            addCenterMark();
            addCircle(latLng,radius);
        }

        Utils.load(GdmapActivity.this, url, imgHead, new OnImgLoadFinish() {
            @Override
            public void loadFinish(RoundedBitmapDrawable drawable) {
                GdmapActivity.this.roundedBitmapDrawable = drawable;
                Log.e("load","center-1:"+"roundedBitmapDrawable="+drawable);
//                addCenterMark();
                mHandler.sendEmptyMessage(Const.LOAD_IMG);
            }

        });





    }

    /**
     * 为了符合glide的逻辑而添加的方法
     * glide 加载一个url成功后不再重复加载,所以中心marker在清除之后要重新添加
     * */
    private void addCenterMark(){
        LatLng primaryLat = new LatLng(0, 0);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.setFlat(true);
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.position(primaryLat);
        markerOptions.icon(BitmapDescriptorFactory.fromView(logo));
        mPositionMark = mAmap.addMarker(markerOptions);
        mPositionMark.setPositionByPixels(mMapView.getWidth() / 2,
                mMapView.getHeight() / 2);
    }

    private void init(Bundle savedInstanceState) {

//        mMapView = (MapView) findViewById(R.id.map);
        llt_relocate = (LinearLayout) findViewById(R.id.llt_relocate);
        llt_relocate.setOnClickListener(this);
        mMapView.onCreate(savedInstanceState);
        mAmap = mMapView.getMap();
        mAmap.getUiSettings().setZoomControlsEnabled(false); //缩放控件不显示
//        mAmap.getUiSettings().setRotateGesturesEnabled(false); //禁用旋转手势,与iOS一致
        mAmap.setOnMapLoadedListener(this);  //地图加载完成
        mAmap.setOnCameraChangeListener(this); //地图状态发送变化
//        mAmap.setMinZoomLevel(15f); //设置地图最小缩放
        mAmap.moveCamera(CameraUpdateFactory.zoomBy(5));

        mLocationImage = (ImageView) findViewById(R.id.location_image);
        mLocationImage.setOnClickListener(this);
        // 初始化地理围栏
        fenceClient = new GeoFenceClient(getApplicationContext());

//        listView = (ListView) findViewById(R.id.listview_poi);

        poiItemAdapter = new PoiItemAdapter(GdmapActivity.this, poiItems);

        listView.setAdapter(poiItemAdapter);
        /**
         * 创建pendingIntent
         */
        fenceClient.setGeoFenceListener(this);
        /**
         * 设置地理围栏的触发行为,默认为进入
         */
        fenceClient.setActivateAction(GeoFenceClient.GEOFENCE_IN);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                poiItemAdapter.setSelectedPosition(position);
                poiItemAdapter.notifyDataSetChanged();
                GdmapActivity.this.poiItem = poiItems.get(position);

            }
        });


    }

    private void intData(){


        Intent intent = getIntent();
        location = (Location) intent.getParcelableExtra("location");
        primaryUrl = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        String r = intent.getStringExtra("radius");

        try{
            radius = Float.parseFloat(r);
        }catch (Exception e){
            logUtil.e(e.toString());
        }

        if(title != null){
            setBarTitle(title);
        }else{
            setBarTitle(getString(R.string.set_position));
        }


    }

    /**
     * 添加圆形围栏,默认直径500m
     * customId不能重复 ,
     */
    private void addRoundFence() {
        String customId = "default" + Math.random(); //不能每次相同
        if (null == mStartPosition) {
            Toast.makeText(getApplicationContext(), "显示出错", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        DPoint centerPoint = new DPoint(mStartPosition.latitude,
                mStartPosition.longitude);
        fenceClient.addGeoFence(centerPoint, radius, customId);
        logUtil.i("addRoundFence-" + "添加圆radius="+radius);
    }

    /**
     * 更新地图状态
     */

    private void refreshMapStatue(LatLng latLng) {
        CameraUpdate cameraUpate = CameraUpdateFactory.newLatLngZoom(
                latLng, mAmap.getCameraPosition().zoom);
        mAmap.animateCamera(cameraUpate); //动画的方式更新地图状态
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public RxPermissions getRxPermission() {
        return rxPermissions;
    }
}
