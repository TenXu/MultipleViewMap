/**  
 * Project Name:Android_Car_Example  
 * File Name:RegeocodeTask.java  
 * Package Name:com.amap.api.car.example  
 * Date:2015年4月2日下午6:24:53  
 *  
 */

package com.seekhoney.library_gdmap.task;

import android.content.Context;
import android.widget.Toast;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.seekhoney.library_gdmap.R;
import com.seekhoney.library_gdmap.listener.OnLocationGetListener;
import com.seekhoney.library_gdmap.model.PositionEntity;

import java.util.List;

/**
 * ClassName:RegeocodeTask <br/>
 * Function: 简单的封装的逆地理编码功能 <br/>
 * Date: 2015年4月2日 下午6:24:53 <br/>
 * 
 * @author yiyi.qi
 * @version
 * @since JDK 1.6
 * @see
 */
public class RegeocodeTask implements OnGeocodeSearchListener {
	private static final float SEARCH_RADIUS = 100f;
	private OnLocationGetListener mOnLocationGetListener;
	private Context mContext;
	private GeocodeSearch mGeocodeSearch;

	public RegeocodeTask(Context context) {
		mGeocodeSearch = new GeocodeSearch(context);
		mGeocodeSearch.setOnGeocodeSearchListener(this);
		this.mContext = context;
	}

	public void search(double latitude, double longitude , float radius) {
		RegeocodeQuery regecodeQuery = new RegeocodeQuery(new LatLonPoint(
				latitude, longitude), radius, GeocodeSearch.AMAP);
		mGeocodeSearch.getFromLocationAsyn(regecodeQuery);
	}

	public void setOnLocationGetListener(
			OnLocationGetListener onLocationGetListener) {
		mOnLocationGetListener = onLocationGetListener;
	}

	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {

	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult regeocodeReult,
			int resultCode) {
		if (resultCode == AMapException.CODE_AMAP_SUCCESS) {
			if (regeocodeReult != null
					&& regeocodeReult.getRegeocodeAddress() != null
					&& mOnLocationGetListener != null) {
				String address = regeocodeReult.getRegeocodeAddress()
						.getFormatAddress();
				String city = regeocodeReult.getRegeocodeAddress().getCityCode();
		 
				PositionEntity entity = new PositionEntity();
				entity.address = address;
				entity.city = city;
				mOnLocationGetListener.onRegecodeGet(entity);

				if (regeocodeReult != null && regeocodeReult.getRegeocodeAddress() != null
						&& regeocodeReult.getRegeocodeAddress().getFormatAddress() != null) {
					List<PoiItem> poiItemList = regeocodeReult.getRegeocodeAddress().getPois();
					mOnLocationGetListener.onRegecodeCallback(poiItemList);
				} else {
					Toast.makeText(mContext, R.string.no_result, Toast.LENGTH_SHORT).show();
				}

			}
		}
		//TODO 可以根据app自身需求对查询错误情况进行相应的提示或者逻辑处理
	}

}
