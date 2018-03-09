package com.seekhoney.library_gdmap.ui;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import butterknife.OnClick;
import com.amap.api.maps.AMap;
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
import com.seekhoney.library_gdmap.listener.OnImgLoadFinish;
import com.seekhoney.library_gdmap.logic.LocateLogic;
import com.seekhoney.library_gdmap.module.CommonModule;
import com.seekhoney.library_gdmap.mvp.ILocateView;
import com.seekhoney.library_gdmap.utils.LogUtil;
import com.seekhoney.library_gdmap.utils.Utils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryan on 17/6/6.
 */

public class GdmapActivity extends GdBaseActivity implements AMap.OnMapLoadedListener, ILocateView {

    @BindView(R2.id.cancle_gdmap_sdk)
    TextView tv_cancle;
    @BindView(R2.id.title_gdmap_sdk)
    TextView tv_title;  //标题
    @BindView(R2.id.right_gdmap_sdk)
    TextView tv_sure;   //确定
    @BindView(R2.id.llt_back)
    LinearLayout llt_back; //返回图标
    @BindView(R2.id.map)
    MapView mMapView;   //地图view
    @BindView(R2.id.listview_poi)
    ListView listView;
    @Inject
    RxPermissions rxPermissions;
    @Inject
    LatLngBounds.Builder boundsBuilder; // 当前的坐标点集合，主要用于进行地图的可视区域的缩放



    private AMap mAmap;
    private Marker mPositionMark;          //定位的那个点
    private PoiItemAdapter poiItemAdapter;
    private List<PoiItem> poiItems = new ArrayList<>();
    private PoiItem poiItem;//选择的兴趣点
    private View logo;  //中心点Marker图标
    private ImageView imgHead;//中心点Marker头像
    private RoundedBitmapDrawable roundedBitmapDrawable;
    private Location location; //初始化,允许传入初始值
    private String primaryUrl; //初始,头像url
    private String title; //初始化,title
    private float radius = 500f;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerCommonComponent.builder().
                commonModule(new CommonModule(this)).
                build().inject(this);
        init(savedInstanceState);
        intData();
    }

    @OnClick({R2.id.cancle_gdmap_sdk,R2.id.right_gdmap_sdk,R2.id.llt_back,R2.id.llt_relocate})
    public void onViewClick(View v){
        int id = v.getId();
        if(id == R.id.cancle_gdmap_sdk){
            setCancle();
        }else if(id == R.id.right_gdmap_sdk){
            setSure();
        }else if(id == R.id.llt_back){
            setBack();
        }else if(id == R.id.llt_relocate){
            gdpresenter.singleLocate(radius);
        }
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
    }

    @Override
    public int getContentView() {
        return R.layout.activity_admin;
    }

    //取消

    @Override
    protected void setCancle() {
        finish();
    }

    //设置


    @Override
    protected void setSure() {
        if(poiItem != null){
            LocateLogic.getIns(this).selectedPoiCallback(poiItem);
        }

        finish();
    }

    @Override
    protected void setBack() {

    }


    /**
     * 地图加载完成
     */
    @Override
    public void onMapLoaded() {

        setCenterMaker(null,0,primaryUrl);  //为了加载图标,必须在这里加载,否则加载不出来
        gdpresenter.singleLocate(radius);
        LogUtil.getIns("定位").i("地图加载完成");


    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void notifyChanged(List<PoiItem> poiItems) {

        this.poiItems = poiItems;
        if (poiItems != null && poiItems.size() > 0) {
            poiItemAdapter.setItems(poiItems);
            poiItemAdapter.setSelectedPosition(0);
            poiItemAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            poiItem = poiItems.get(0);
        }

    }

    @Override
    public void setCenterMark(LatLng latLng, float radius) {
        setCenterMaker(latLng , radius , primaryUrl);
    }

    @Override
    public MapView getMapView() {
        return mMapView;
    }

    @Override
    public LatLngBounds.Builder getBounds() {
        return boundsBuilder;
    }


    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public RxPermissions getRxPermission() {
        return rxPermissions;
    }


    private void addCircle(LatLng lat, float radius){

        // 绘制一个圆形
        mAmap.addCircle(new CircleOptions().center(lat)
                .radius(radius).strokeColor(Const.STROKE_COLOR)
                .fillColor(Const.FILL_COLOR).strokeWidth(Const.STROKE_WIDTH));
        boundsBuilder.include(lat);
    }

    /**
     * 设置地图中心的标记,也是定位的当前位置
     */
    private void setCenterMaker(final LatLng latLng , final float radius, String url) {

        logo = getLayoutInflater().inflate(R.layout.view_setted_logo, null);
        imgHead = (ImageView) logo.findViewById(R.id.head_setmap);
        if(roundedBitmapDrawable != null){
            imgHead.setImageDrawable(roundedBitmapDrawable);
        }
        if(latLng != null){

            addCenterMark();
            addCircle(latLng,radius);
        }

        Utils.load(GdmapActivity.this, url, imgHead, new OnImgLoadFinish() {
            @Override
            public void loadFinish(RoundedBitmapDrawable drawable) {
                GdmapActivity.this.roundedBitmapDrawable = drawable;
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

        mMapView.onCreate(savedInstanceState);
        mAmap = mMapView.getMap();
        mAmap.getUiSettings().setZoomControlsEnabled(false); //缩放控件不显示
        mAmap.setOnMapLoadedListener(this);  //地图加载完成
        mAmap.moveCamera(CameraUpdateFactory.zoomBy(5));

        poiItemAdapter = new PoiItemAdapter(GdmapActivity.this, poiItems);
        listView.setAdapter(poiItemAdapter);
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
        }

    }
}
