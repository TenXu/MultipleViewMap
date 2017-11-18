package com.seekhoney.library_gdmap.logic;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import com.amap.api.maps.model.LatLng;
import com.seekhoney.library_gdmap.constant.Const;
import com.seekhoney.library_gdmap.listener.OnCheckWhinAreaListener;
import com.seekhoney.library_gdmap.listener.OnLocationCommonListener;
import com.seekhoney.library_gdmap.listener.OnSelectedPoiListener;
import com.seekhoney.library_gdmap.model.PositionEntity;
import com.seekhoney.library_gdmap.task.LocationTask;
import com.seekhoney.library_gdmap.ui.GdmapAdminActivity;
import com.seekhoney.library_gdmap.ui.GdmapTestLocateActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryan on 17/6/8.
 */

public class MapHelper
{
    private static MapHelper ins;
    private Context mContext;
    final List<LatLng> listLatlng = new ArrayList<>();
//    public YXTPermissionsBuilder permissionsBuilder;
//    private boolean isAllow = false;
    private MapHelper(Context mContext){
        this.mContext = mContext;
    }

    public static synchronized MapHelper getIns(Context mContext){

        if(ins == null){
            ins = new MapHelper(mContext);
        }
        return ins;
    }

    /**
     * 对外开放的接口,在onCreate中调用以申请权限
     * */
//    public void onCreate(){
//        requestReadStoragePermission();
//    }

    /**
     * 对外开放的接口,获取授权结果
     *
     * */
//    public void requestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
//        if (permissionsBuilder != null && (
//                requestCode == Const.READ_STORAGE ||
//                        requestCode == Const.GET_LOCATIONS )){
//            permissionsBuilder.onRequestPermissionsResult(requestCode, permissions, grantResults, mContext);
//        }
//    }

    /**
     * 对外开放的接口,进入定位页面
     * @param location 初始定位,如果非空则按此经纬度定位,可以为空
     * @param mContext 传入Activity实例,不允许为空
     * @param title toolbar的标题,可以为空
     * @param url Marker中图片的url,可以为空
     * @param radius 围栏(也是检测范围)的半径
     * @param listener 返回选择的目标点的POI信息
     *
     * */
    public void gotosetLocate(Context mContext, Location location , String url, String title, float radius, OnSelectedPoiListener listener){

        if(mContext == null ){
            return;
        }

        LocateLogic.getIns(mContext).setSelectedPoiListener(listener);

        Intent intent = new Intent(mContext, GdmapAdminActivity.class);
        intent.putExtra("location",location);
        intent.putExtra("url",url);
        intent.putExtra("title",title);
        intent.putExtra("radius",radius+"");
        mContext.startActivity(intent);
    }

    /**
     * 对外开放的接口,进入签到页面
     * @param mContext 不允许为空
     * @param logoUrl 被设置为基准的Marker图片的url,可以为空
     * @param headUrl 定位Marker图片的url,可以为空
     * @param title toolbar 的title,可以为空
     * @param judgement 下方栏提示语,可以为空
     * */
    public void gotoSignMark(Context mContext, Location location , String logoUrl , String headUrl, String title, String alertCenter, String alertYou, String judgement, String nonjudgement, float radius, OnCheckWhinAreaListener listener){

        if(mContext == null ){

            return;
        }

        LocateLogic.getIns(mContext).setWhinListener(listener);
        Intent intent = new Intent(mContext , GdmapTestLocateActivity.class);
        intent.putExtra("location",location);
        intent.putExtra("url",logoUrl);
        intent.putExtra("title",title);
        intent.putExtra("headUrl",headUrl);
        intent.putExtra("alertc",alertCenter);
        intent.putExtra("alertu",alertYou);
        intent.putExtra("judgement",judgement);
        intent.putExtra("nonjudgement",nonjudgement);
        intent.putExtra("radius",radius+"");
        mContext.startActivity(intent);

    }


    /**
     * 对外开放的接口,当前位置是否在范围内
     * @param mContext
     * @param list 目标点的集合
     * @param distance 检测的范围
     * @param listener 检测范围的回调 返回码:Const.BAD_REQUEST 请求错误,Const.WITHIN_AREA在范围内 , Const.WITHOUT_AREA 不再范围内
     * */
    public void checkWithinArea(final Context mContext, final List<Location> list, final float distance , final OnCheckWhinAreaListener listener){
        if(mContext == null || list == null || list.size() == 0 ){
            listener.getWithinAreaCode(Const.BAD_REQUEST);
            return;
        }

        listLatlng.clear();
        for(Location location : list){
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            listLatlng.add(latLng);
        }

        LocationTask locationTask = new LocationTask(mContext.getApplicationContext());
        locationTask.setmOnLocationCommonListener(new OnLocationCommonListener() {
            @Override
            public void onLocationDataBack(PositionEntity entity) {
                LatLng latLng = new LatLng(entity.latitue , entity.longitude);
                int code = LocateLogic.getIns(mContext).isWithinGroup(latLng,listLatlng,distance);
                listener.getWithinAreaCode(code);
            }
        });
        locationTask.startSingleLocate();

    }

    /**
     * 获取当前定位
     * */
    public void getCurrentLocation(Context mContext , OnLocationCommonListener listener){
        LocationTask locationTask = new LocationTask(mContext);
        locationTask.setmOnLocationCommonListener(listener);
        locationTask.startSingleLocate();
    }

//    public void requestReadStoragePermission() {
//        permissionsBuilder = new YXTPermissionsBuilder.Builder(mContext)
//                .setOnGrantedListener(new OnPermissionsGrantedListener() {
//                    @Override
//                    public void onPermissionsGranted(int requestCode, List<String> perms) {
//                        Log.e("permission","获取STORAGE授权");
//                        requestGetLocationPermission();
//                    }
//                })
//                .setOnDeniedAgainListener(new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Log.e("permission","拒绝STORAGE授权");
//                    }
//                })
//                .isNeedClose4PositiveNeverAskAgain(true)
//                .setRationale4NeverAskAgain(String.format(mContext.getString(R.string.permission_tips), mContext.getString(R.string.map_msg_storage), mContext.getString(R.string.app_name), mContext.getString(R.string.app_name)) )
//                .setRequestCode(Const.READ_STORAGE)//必需
//                .build();
//        permissionsBuilder.requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//    }
//
//    public void requestGetLocationPermission() {
//        permissionsBuilder = new YXTPermissionsBuilder.Builder(mContext)
//                .setOnGrantedListener(new OnPermissionsGrantedListener() {
//                    @Override
//                    public void onPermissionsGranted(int requestCode, List<String> perms) {
//                        Log.e("permission","获取定位授权");
//                    }
//                })
//                .setOnDeniedAgainListener(new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Log.e("permission","拒绝定位授权");
//                    }
//                })
//                .isNeedClose4PositiveNeverAskAgain(true)
//                .setRationale4NeverAskAgain(String.format(mContext.getString(R.string.permission_tips), mContext.getString(R.string.map_msg_location), mContext.getString(R.string.app_name), mContext.getString(R.string.app_name)) )
//                .setRequestCode(Const.GET_LOCATIONS)//必需
//                .build();
//        permissionsBuilder.requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION);
//    }


}
