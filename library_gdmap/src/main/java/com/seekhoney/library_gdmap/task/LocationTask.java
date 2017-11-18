/**  
 * Project Name:Android_Car_Example  
 * File Name:LocationTask.java  
 * Package Name:com.amap.api.car.example  
 * Date:2015年4月3日上午9:27:45  
 *  
 */

package com.seekhoney.library_gdmap.task;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.PoiItem;
import com.seekhoney.library_gdmap.listener.OnLocationCommonListener;
import com.seekhoney.library_gdmap.listener.OnLocationGetListener;
import com.seekhoney.library_gdmap.model.PositionEntity;

import java.util.List;


/**
 * ClassName:LocationTask <br/>
 * Function: 简单封装了定位请求，可以进行单次定位和多次定位，注意的是在app结束或定位结束时注意销毁定位 <br/>
 * Date: 2015年4月3日 上午9:27:45 <br/>
 * 
 * @author yiyi.qi
 * @version
 * @since JDK 1.6
 * @see
 */
public class LocationTask implements AMapLocationListener,
		OnLocationGetListener {

	private AMapLocationClient mLocationClient;

	private static LocationTask mLocationTask;

	private Context mContext;

	private OnLocationGetListener mOnLocationGetlisGetListener; //签到页面用

	private OnLocationCommonListener mOnLocationCommonListener; //检查是否在范围内接口用,以免影响签到页面的定位生命周期

	private RegeocodeTask mRegecodeTask;

	public LocationTask(Context context) {
		mLocationClient = new AMapLocationClient(context);
		mLocationClient.setLocationListener(this);
		mRegecodeTask = new RegeocodeTask(context);
		mRegecodeTask.setOnLocationGetListener(this);
		mContext = context;
	}

	public void setOnLocationGetListener(
			OnLocationGetListener onGetLocationListener) {
		mOnLocationGetlisGetListener = onGetLocationListener;
	}

	public void setmOnLocationCommonListener(OnLocationCommonListener mOnLocationCommonListener){
		this.mOnLocationCommonListener = mOnLocationCommonListener;
	}

	public static synchronized LocationTask getInstance(Context context) {
		if (mLocationTask == null) {
			mLocationTask = new LocationTask(context);
		}
		return mLocationTask;
	}

	/**  
	 * 开启单次定位
	 */
	public void startSingleLocate() {
		AMapLocationClientOption option=new AMapLocationClientOption();
		option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
		option.setOnceLocation(true);
		mLocationClient.setLocationOption(option);
		mLocationClient.startLocation();


	}

	/**  
	 * 开启多次定位
	 */
	public void startLocate() {

		AMapLocationClientOption option=new AMapLocationClientOption();
		option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
		option.setOnceLocation(false);
		option.setInterval(8*1000);
		mLocationClient.setLocationOption(option);
		mLocationClient.startLocation();

	}

	/**  
	 * 结束定位，可以跟多次定位配合使用
	 */
	public void stopLocate() {
		mLocationClient.stopLocation();

	}

	/**  
	 * 销毁定位资源
	 */
	public void onDestroy() {
		mLocationClient.stopLocation();
		mLocationClient.onDestroy();
		mLocationTask = null;
	}



	@Override
	public void onLocationChanged(AMapLocation amapLocation) {			//定位完成后回调
		if (amapLocation != null ) {

			if(amapLocation.getErrorCode() == 0){

				PositionEntity entity = new PositionEntity();
				entity.latitue = amapLocation.getLatitude();
				entity.longitude = amapLocation.getLongitude();

				if (!TextUtils.isEmpty(amapLocation.getAddress())) {
					entity.address = amapLocation.getAddress();
				}

				if(mOnLocationGetlisGetListener != null){
					mOnLocationGetlisGetListener.onLocationGet(entity);
				}

				if(mOnLocationCommonListener != null){
					mOnLocationCommonListener.onLocationDataBack(entity);
				}
			}

			if(amapLocation.getErrorCode() == 0){
				Log.e("MAP","onLocationChanged--定位成功");
			}else {
				Log.e("MAP","onLocationChanged--定位失败"+"--code="+amapLocation.getErrorCode()+"--detail="+amapLocation.getLocationDetail());
			}

		}
	}


	@Override
	public void onLocationGet(PositionEntity entity) {


	}

	@Override
	public void onRegecodeGet(PositionEntity entity) {

	}

	@Override
	public void onRegecodeCallback(List<PoiItem> poiItems) {

	}

}
