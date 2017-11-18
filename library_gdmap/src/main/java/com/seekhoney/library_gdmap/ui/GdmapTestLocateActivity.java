package com.seekhoney.library_gdmap.ui;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.amap.api.fence.GeoFence;
import com.amap.api.fence.GeoFenceClient;
import com.amap.api.fence.GeoFenceListener;
import com.amap.api.location.DPoint;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.*;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.seekhoney.library_gdmap.R;
import com.seekhoney.library_gdmap.base.BaseActivity;
import com.seekhoney.library_gdmap.constant.Const;
import com.seekhoney.library_gdmap.listener.OnImgLoadFinish;
import com.seekhoney.library_gdmap.listener.OnLocationGetListener;
import com.seekhoney.library_gdmap.logic.LocateLogic;
import com.seekhoney.library_gdmap.model.PositionEntity;
import com.seekhoney.library_gdmap.task.LocationTask;
import com.seekhoney.library_gdmap.task.RegeocodeTask;
import com.seekhoney.library_gdmap.utils.LogUtil;
import com.seekhoney.library_gdmap.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ryan on 17/6/6.
 */

public class GdmapTestLocateActivity extends BaseActivity implements AMap.OnCameraChangeListener,
        AMap.OnMapLoadedListener, OnLocationGetListener, View.OnClickListener, GeoFenceListener,
        AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter, AMap.OnMarkerClickListener, AMap.OnMapClickListener {

    private MapView nMapView;   //地图view

    private AMap mAmap;

    private Marker mSettedMark;            //当前定位的marker

    private Marker mPositionMark;          //被设置的基准marker

    private LatLng mStartPosition;         //存储经纬度坐标值的类

    private LatLng mSettedPosition;        //存储设定的经纬度的值,测试用

    private RegeocodeTask mRegeocodeTask;  //封装的逆地理编码类

    private LocationTask mLocationTask;   //封装的定位类

    private ImageView nLocationImage;

    private LinearLayout nllt_relocate;

    private Circle circle;

    private TextView address_locate;

    private TextView within_area; //底部栏是否在范围内

    private float distance = 500f; //两点间距离,默认500m

    //第一次进入时在onLocationGet回调中添加围栏,其他都在onGeoFenceCreateFinished中添加
    private boolean mIsFirst = true;

    // 地理围栏客户端
    private GeoFenceClient fenceClient = null;

    // 记录已经添加成功的围栏,有这个必要吗?
    private HashMap<String, GeoFence> fenceMap = new HashMap<String, GeoFence>();

    // 当前的坐标点集合，主要用于进行地图的可视区域的缩放
    private LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();


    private List<PoiItem> poiItems = new ArrayList<>();

    List<GeoFence> fenceList = new ArrayList<GeoFence>();

    private String TAG = "GDMAP";

    private LogUtil logUtil = LogUtil.getIns(TAG);

    private int judgeCode = -5; //记录当前位置是否在范围内

    private View window; //点击Marker弹窗

    private TextView tv_isWithin; // 点击Marker提示语所在

    /**
     * logo ,imgHead,roundedBitmapDrawable:
     * 由于glide对同一个url加载成功一次后就不再回调,而Marker是不断的清除与添加,所以用这个临时解决方案
     */

    private View logo;//中心marker的图标

    private ImageView imgHeadCenter;//中心的头像

    private RoundedBitmapDrawable roundedBitmapDrawable;

    private View myLogo;//我的头像view

    private ImageView myHead;

    private RoundedBitmapDrawable myRoudDrawable;

    private Location location; //定位初始值

    private String logoUrl;   //定位marker头像 的url

    private String title;      //title

    private String headUrl;    //我的头像url

    private String alertCenter; //定位Marker弹出提示语

    private String alertYou;      //我的Marker弹窗提示语

    private String judgement;     //判断语,在范围内

    private String nonjudgement; //判断语,不在范围内

    private TextView my_location;//我的位置,主要为显示断网时加载失败

    private GdmapTestLocateActivity ins;


    private Handler mHandler = new Handler() {
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
                case 3:
                    GeoFence fence = (GeoFence) msg.obj;
                    drawCircle(fence);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logUtil.setShow(true);
        init(savedInstanceState);
        initData();
        hideSureCancel();
        showBack();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        nMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        nMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.removeMarkers();
        nMapView.onDestroy();
        mLocationTask.onDestroy();
        if (null != fenceClient) {
            fenceClient.removeGeoFence();
        }
    }

    @Override
    public int getContentView() {
        return R.layout.activity_locate;
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.llt_relocated) {
            mLocationTask.startSingleLocate();
//            CameraUpdate cameraUpate = CameraUpdateFactory.newLatLngZoom(
//                    mStartPosition, mAmap.getCameraPosition().zoom);
//            mAmap.animateCamera(cameraUpate); //动画的方式更新地图状态

//            refreshMapStatue(mStartPosition);
            logUtil.e("onClick-startSingleLocate");
        }
    }

    //取消
    @Override
    protected void setCancle() {
        finish();
    }

    //确定
    @Override
    protected void setSure() {

    }

    @Override
    protected void setBack() {
        LocateLogic.getIns(GdmapTestLocateActivity.this).notifyIsWhin(judgeCode);
        finish();
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

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

        // TODO

    }


    /**
     * 地图状态改变完成时,第一次加载完和状态改变时都会被调用
     */
    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {

//        mStartPosition = cameraPosition.target; //取出经纬度(当前定位的?)
        mRegeocodeTask.setOnLocationGetListener(this);
        mRegeocodeTask
                .search(mStartPosition.latitude, mStartPosition.longitude,distance); //逆向地理编码
        //增加围栏
//        addRoundFence();
//        addMarker(mStartPosition);
        logUtil.e("onCameraChangeFinish-" + "地图状态改变完成" + "latitude=" + mStartPosition.latitude + "-longitude=" + mStartPosition.longitude);
    }

    /**
     * 地图加载完成
     */
    @Override
    public void onMapLoaded() {

        setCenterMaker(null, 0, logoUrl);  //为了加载图标,必须在这里加载,否则加载不出来
        addMarker(null, headUrl);
        mLocationTask.startSingleLocate();      //开启单次定位
        if(!LocateLogic.isNetworkAvailable(ins)){
            showTextViews(false);

        }
        logUtil.e("onMapLoaded-" + "地图加载完成");

    }

    /**
     * LocationTask中定位完成后调用了此方法
     */
    @Override
    public void onLocationGet(PositionEntity entity) {

        if (mSettedPosition == null) {
            Toast.makeText(GdmapTestLocateActivity.this, getString(R.string.no_locate_data), Toast.LENGTH_SHORT).show();
            return;
        }
//        mSettedPosition = new LatLng(entity.latitue+0.004, entity.longitude+0.004);
        mStartPosition = new LatLng(entity.latitue, entity.longitude);
//        CameraUpdate cameraUpate = CameraUpdateFactory.newLatLngZoom(
//                mSettedPosition, mAmap.getCameraPosition().zoom);
//        mAmap.animateCamera(cameraUpate); //动画的方式更新地图状态

        if (mIsFirst) { //初次定位增加围栏,获取100米范围内商圈;然后在onCameraChangeFinish处理
            addRoundFence();
            refreshMapStatue(mSettedPosition);
            logUtil.e("onLocationGet-" + "首次定位完成");
            mIsFirst = false;
        }else {
            refreshMapStatue(mStartPosition);
            logUtil.e("onLocationGet-" + "非首次定位完成");
        }

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

        //刷新底部栏ui
        if (poiItems != null && poiItems.size() > 0) {
            PoiItem item = poiItems.get(0);
            String address = item.getSnippet();
            LatLonPoint point = item.getLatLonPoint();
            LatLng xlatLng = null;
            if (!TextUtils.isEmpty(address)) {
                address_locate.setText(address);
            }
            if (point != null) {
                xlatLng = new LatLng(point.getLatitude(), point.getLongitude());
            }

            //显示没网时匿掉的view
            showTextViews(true);

            //判断是否在范围内
            List<LatLng> list = new ArrayList<>();
            list.add(mStartPosition);
            judgeCode = LocateLogic.getIns(GdmapTestLocateActivity.this).isWithinGroup(mSettedPosition, list, distance);

            if (judgeCode == Const.WITHIN_AREA) {
                if(!TextUtils.isEmpty(judgement)){
                    within_area.setText("("+judgement+")");
                }else {
                    within_area.setText(getString(R.string.within_area));
                }

            } else {
                if(!TextUtils.isEmpty(nonjudgement)){
                    within_area.setText("("+nonjudgement+")");
                }else {
                    within_area.setText(getString(R.string.without_area));
                }
                within_area.setTextColor(getResources().getColor(R.color.map_cc0000));
            }


//            if(LocateLogic.getIns(GdmapTestLocateActivity.this).isWithin(xlatLng,mSettedPosition , distance)){
//                within_area.setText(getString(R.string.within_area));
//            }else {
//                within_area.setText(getString(R.string.without_area));
//                within_area.setTextColor(getResources().getColor(R.color.map_cc0000));
//            }
        }

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
    }

    @Override
    public View getInfoWindow(Marker marker) {

        if (window == null) {
            window = getLayoutInflater().inflate(R.layout.view_alert, null);
        }
        if (marker.equals(mPositionMark)) {
            if(!TextUtils.isEmpty(alertCenter)){
                setWindowData(window,alertCenter);
            }else {
                setWindowData(window, getString(R.string.target_postion));
            }

        } else {
            if(!TextUtils.isEmpty(alertYou)){
                setWindowData(window, alertYou);
            }else {
                setWindowData(window, getString(R.string.your_position));
            }

        }
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if (marker.equals(mPositionMark)) {

            if (marker.isInfoWindowShown()) {
                mPositionMark.hideInfoWindow();
            } else {
                mPositionMark.showInfoWindow();
            }
        } else if (marker.equals(mSettedMark)) {
            if (marker.isInfoWindowShown()) {
                mSettedMark.hideInfoWindow();
            } else {
                mSettedMark.showInfoWindow();
            }
        }
        return true;
    }


    @Override
    public void onMapClick(LatLng latLng) {
        if (mSettedMark != null && mSettedMark.isInfoWindowShown()) {
            mSettedMark.hideInfoWindow();
        }

        if (mPositionMark != null && mPositionMark.isInfoWindowShown()) {
            mPositionMark.hideInfoWindow();
        }
    }

    private void setWindowData(View view, String alert) {

        if (tv_isWithin == null) {
            tv_isWithin = (TextView) view.findViewById(R.id.tv_isWithin);
        }
        if (alert != null) {
            tv_isWithin.setText(alert);
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
                    logUtil.e(e.toString());
                }
            }
        }.start();
    }

    private void drawFence(GeoFence fence) {
        switch (fence.getType()) {
            case GeoFence.TYPE_ROUND:
            case GeoFence.TYPE_AMAPPOI:
                Message msg = new Message();
                msg.what = 3;
                msg.obj = fence;
                mHandler.sendMessage(msg);
//                drawCircle(fence);
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
        setCenterMaker(center, radius, logoUrl);
        addMarker(mStartPosition, headUrl);

        // 绘制一个圆形
//        circle = mAmap.addCircle(new CircleOptions().center(center)
//                .radius(fence.getRadius()).strokeColor(Const.STROKE_COLOR)
//                .fillColor(Const.FILL_COLOR).strokeWidth(Const.STROKE_WIDTH));
//        boundsBuilder.include(center);
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
     * 暂注释掉,测试后解除
     */
    private void setCenterMaker(final LatLng latLng, final float radius, String url) {
        logo = getLayoutInflater().inflate(R.layout.view_sign_logo, null);
        imgHeadCenter = (ImageView) logo.findViewById(R.id.head_signmap);

        if (roundedBitmapDrawable != null) {
            imgHeadCenter.setImageDrawable(roundedBitmapDrawable);
            Log.e("load","imgHeadCenter2="+roundedBitmapDrawable);
        }
        if (latLng != null) {

            addCenterMark(latLng);
            addCircle(latLng, radius);
        }


        Utils.load(GdmapTestLocateActivity.this, url, imgHeadCenter, new OnImgLoadFinish() {
            @Override
            public void loadFinish(RoundedBitmapDrawable drawable) {
                GdmapTestLocateActivity.this.roundedBitmapDrawable = drawable;
                Log.e("load","imgHeadCenter1="+drawable);
            }

        });

    }

    /**
     * 此方法废弃待删
     *
     */
    private void addPrimaryCenterMark() {
        LatLng primaryLat = new LatLng(0, 0);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.setFlat(true);
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.position(primaryLat);
        markerOptions.icon(BitmapDescriptorFactory.fromView(logo));
        mPositionMark = mAmap.addMarker(markerOptions);
        mPositionMark.setPositionByPixels(nMapView.getWidth() / 2,
                nMapView.getHeight() / 2);
    }

    private void addCenterMark(LatLng lng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.setFlat(true);
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.position(lng);
        markerOptions.title("");
        markerOptions.icon(BitmapDescriptorFactory.fromView(logo));
        mPositionMark = mAmap.addMarker(markerOptions);
        mPositionMark.setPosition(lng);
    }

    private void addCircle(LatLng lat, float radius) {

        // 绘制一个圆形
        mAmap.addCircle(new CircleOptions().center(lat)
                .radius(radius).strokeColor(Const.STROKE_COLOR)
                .fillColor(Const.FILL_COLOR).strokeWidth(Const.STROKE_WIDTH));
        boundsBuilder.include(lat);
    }

    private void init(Bundle savedInstanceState) {

        ins = this;

        nMapView = (MapView) findViewById(R.id.map_locate);
        nllt_relocate = (LinearLayout) findViewById(R.id.llt_relocated);
        nllt_relocate.setOnClickListener(this);
        nMapView.onCreate(savedInstanceState);
        mAmap = nMapView.getMap();
        mAmap.getUiSettings().setZoomControlsEnabled(false); //缩放控件不显示
//        mAmap.getUiSettings().setRotateGesturesEnabled(false); //禁用旋转手势,与iOS一致
        mAmap.setOnMapLoadedListener(this);  //地图加载完成
        mAmap.setOnCameraChangeListener(this); //地图状态发送变化
        mAmap.setOnMarkerClickListener(this);
        mAmap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
//        mAmap.setMinZoomLevel(15f); //设置地图最小缩放
        mAmap.moveCamera(CameraUpdateFactory.zoomBy(5));
        mAmap.setOnMapClickListener(this);

        nLocationImage = (ImageView) findViewById(R.id.location_image_locate);
        nLocationImage.setOnClickListener(this);
        // 初始化地理围栏
        fenceClient = new GeoFenceClient(getApplicationContext());

        address_locate = (TextView) findViewById(R.id.address_locate);
        within_area = (TextView) findViewById(R.id.within_area);
        my_location = (TextView) findViewById(R.id.my_location);


        /**
         * 创建pendingIntent
         */
        fenceClient.setGeoFenceListener(this);
        /**
         * 设置地理围栏的触发行为,默认为进入
         */
        fenceClient.setActivateAction(GeoFenceClient.GEOFENCE_IN);

        setBarTitle(getString(R.string.set_position));

//        mLocationTask = LocationTask.getInstance(getApplicationContext()); //修改为非单例
        mLocationTask = new LocationTask(getApplicationContext());
        mLocationTask.setOnLocationGetListener(this);
        mRegeocodeTask = new RegeocodeTask(getApplicationContext());

    }

    //为了没网的时候显示加载失败
    private void showTextViews(boolean isShow){
        if(isShow){
            within_area.setVisibility(View.VISIBLE);
            address_locate.setVisibility(View.VISIBLE);
            my_location.setText(getString(R.string.my_location));
        }else {
            within_area.setVisibility(View.GONE);
            address_locate.setVisibility(View.GONE);
            my_location.setText(getString(R.string.locate_fail));
        }

    }

    private void initData() {

        Intent intent = getIntent();
        if (intent != null) {
            location = intent.getParcelableExtra("location");
            logoUrl = intent.getStringExtra("url");
            title = intent.getStringExtra("title");
            headUrl = intent.getStringExtra("headUrl");
            alertCenter = intent.getStringExtra("alertc");
            alertYou = intent.getStringExtra("alertu");
            judgement = intent.getStringExtra("judgement");
            nonjudgement = intent.getStringExtra("nonjudgement");
            String r = intent.getStringExtra("radius");
            try{
                distance = Float.parseFloat(r);
            }catch (Exception e){
                logUtil.e(e.toString());
            }
        }

        if (location != null) {
            mSettedPosition = new LatLng(location.getLatitude(), location.getLongitude());
        } else {
            finish();
        }

    }

    /**
     * 添加圆形围栏,默认直径500m
     * customId不能重复 ,
     */
    private void addRoundFence() {
        if (mSettedPosition == null) {
            Toast.makeText(GdmapTestLocateActivity.this, getString(R.string.no_locate_data), Toast.LENGTH_SHORT).show();
            return;
        }
        String customId = "default" + Math.random(); //不能每次相同
        if (null == mStartPosition) {
            Toast.makeText(getApplicationContext(), "显示出错", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        DPoint centerPoint = new DPoint(mSettedPosition.latitude,
                mSettedPosition.longitude);
        fenceClient.addGeoFence(centerPoint, distance, customId);
        logUtil.i("addRoundFence-" + "添加圆");
    }


    /**
     * 设置标记,这里还是用本地的图标,之后改为网络图标
     */
    private void addMarker(LatLng latlng, String url) {

        myLogo = getLayoutInflater().inflate(R.layout.view_location_logo, null);
        myHead = (ImageView) myLogo.findViewById(R.id.head_map);
        if (myRoudDrawable != null) {
            myHead.setImageDrawable(myRoudDrawable);
            Log.e("load","myRoudDrawable2="+myRoudDrawable);
        }

        if (latlng != null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.setFlat(true);
            markerOptions.anchor(0.5f, 0.5f);
            markerOptions.position(latlng);
            markerOptions.title("");
            markerOptions.icon(BitmapDescriptorFactory.fromView(myLogo));
            mSettedMark = mAmap.addMarker(markerOptions);
            mSettedMark.setPosition(latlng);
        }

        Utils.load(GdmapTestLocateActivity.this, url, myHead, new OnImgLoadFinish() {
            @Override
            public void loadFinish(RoundedBitmapDrawable drawable) {
                GdmapTestLocateActivity.this.myRoudDrawable = drawable;
                Log.e("load","myRoudDrawable1="+drawable);
            }

        });
    }


    /**
     * 更新地图状态
     */

    private void refreshMapStatue(LatLng latLng) {
        CameraUpdate cameraUpate = CameraUpdateFactory.newLatLngZoom(
                latLng, mAmap.getCameraPosition().zoom);
        mAmap.animateCamera(cameraUpate); //动画的方式更新地图状态
    }

    /**
     * 设置marker弹窗
     */
    private void setWindowInfo() {

    }


}
