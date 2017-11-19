/**  
 * Project Name:Android_Car_Example  
 * File Name:Utils.java  
 * Package Name:com.amap.api.car.example  
 * Date:2015年4月7日下午3:43:05  
 *  
*/  
  
package com.seekhoney.library_gdmap.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.amap.api.maps.model.Marker;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.seekhoney.library_gdmap.R;
import com.seekhoney.library_gdmap.adapter.MapListAdapter;
import com.seekhoney.library_gdmap.listener.OnImgLoadFinish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**  
 * ClassName:Utils <br/>  
 * Function: TODO ADD FUNCTION. <br/>  
 * Reason:   TODO ADD REASON. <br/>  
 * Date:     2015年4月7日 下午3:43:05 <br/>  
 * @author   yiyi.qi  
 * @version    
 * @since    JDK 1.6  
 * @see        
 */
public class Utils {

	private static ArrayList<Marker> markers=new ArrayList<Marker>();


	/**
	 * 移除marker
	 */
	public static void removeMarkers() {
		for(Marker marker:markers){
			marker.remove();
			marker.destroy();
		}
		markers.clear();
	}

	/**
	 * 使用glide加载网络图片
	 * */
	public static void load(final Context mContext, final String photoUrl, final ImageView photo , final OnImgLoadFinish onImgLoadFinish){

		if(photoUrl == null || photo == null ){
			return;
		}

		Glide.with(mContext).load(photoUrl).asBitmap().centerCrop().listener(new RequestListener<String, Bitmap>() {
			@Override
			public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
				Log.e("load","Exception="+e.toString());
				return false;
			}

			@Override
			public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
				Log.e("load","Ready=");
				return false;
			}
		}).into(new BitmapImageViewTarget(photo) {
			@Override
			protected void setResource(Bitmap resource) {
				RoundedBitmapDrawable circularBitmapDrawable =
						RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
				circularBitmapDrawable.setCircular(true);
				photo.setImageDrawable(circularBitmapDrawable);

				if(onImgLoadFinish != null){
					onImgLoadFinish.loadFinish(circularBitmapDrawable);
				}


			}
		});
	}

	public static void checkMaps(Activity activity , Location location , String targetName){
		HashMap<String, String> pakages = new HashMap();

		List<String> packageList = new ArrayList<>();
		pakages.put("com.tencent.map", activity.getString(R.string.tencent));
		pakages.put("com.autonavi.minimap", activity.getString(R.string.minimap));
		pakages.put("com.baidu.BaiduMap", activity.getString(R.string.baidu));

		for (String p : pakages.keySet()) {
			if (isAvilible(activity, p)) {
				packageList.add(pakages.get(p));

			}
		}
		if (packageList.size() > 0) {
			if(TextUtils.isEmpty(targetName)) targetName = activity.getString(R.string.target_name);
			showPopupWindow(activity, packageList, location , targetName);
		}
	}

	/**
	 * 检查是否安装导航app
	 */
	public static boolean isAvilible(Context context, String packageName) {
		//获取packagemanager
		final PackageManager packageManager = context.getPackageManager();
		//获取所有已安装程序的包信息
		List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
		//用于存储所有已安装程序的包名
		List<String> packageNames = new ArrayList<String>();
		//从pinfo中将包名字逐一取出，压入pName list中
		if (packageInfos != null) {
			for (int i = 0; i < packageInfos.size(); i++) {
				String packName = packageInfos.get(i).packageName;
				packageNames.add(packName);
			}
		}
		//判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
		return packageNames.contains(packageName);
	}


	//显示码率切换的popwindow
	public static void showPopupWindow(final Activity activity, final List<String> list, final Location location , final String targetName) {
		// 一个自定义的布局，作为显示的内容
		View contentView = LayoutInflater.from(activity).inflate(
				R.layout.popuwindow_list_map, null);
		if (location == null) {
			Toast.makeText(activity, activity.getString(R.string.no_location), Toast.LENGTH_SHORT).show();
			return;
		}

		// 设置按钮的点击事件
		ListView playList = (ListView) contentView.findViewById(R.id.play_more_sourse);
		TextView tvQuit = (TextView) contentView.findViewById(R.id.text_quit_show);
		MapListAdapter adapter = new MapListAdapter(activity, list);
		playList.setAdapter(adapter);

		int heighPx = measureListViewHeight(playList, dip2px(activity, 50)) ;
		final PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, heighPx, true);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				if (popupWindow.isShowing())
					popupWindow.dismiss();
			}
		});
		popupWindow.setAnimationStyle(R.style.MyPopupWindow_anim_style);
		popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
		setBackgroundAlpha(activity,0.5f);
		tvQuit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				popupWindow.dismiss();
			}
		});

		playList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (activity.getString(R.string.tencent).equals(list.get(position))) {

					Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("qqmap://map/routeplan?type=drive&to="+targetName+"&tocoord=" + location.getLatitude() + "," + location.getLongitude() + "&policy=0&referer=DingWei"));
					activity.startActivity(intent);
					popupWindow.dismiss();

				} else if (activity.getString(R.string.baidu).equals(list.get(position))) {
					Intent intent = new Intent();
					intent.setData(Uri.parse("baidumap://map/geocoder?location=" + location.getLatitude() + "," + location.getLongitude()));
					activity.startActivity(intent);
					popupWindow.dismiss();
				} else if (activity.getString(R.string.minimap).equals(list.get(position))) {
					Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("androidamap://navi?sourceApplication=DingWei &lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&dev=0"));
					intent.setPackage("com.autonavi.minimap");
					activity.startActivity(intent);
					popupWindow.dismiss();
				}

			}
		});


		popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				setBackgroundAlpha(activity,1f);
			}
		});

	}


	/**
	 * 测量ListView实际高度
	 *
	 * @param listView 传入需要测量的listview
	 * @param extraPx  传入额外的大小,例如listview距离上下边距之和
	 */
	public static int measureListViewHeight(ListView listView, int extraPx) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return 0;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}


		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);  // 如果listview

		return (params.height + extraPx);
	}

	/**
	 * 设置添加屏幕的背景透明度
	 *
	 * @param bgAlpha
	 *            屏幕透明度0.0-1.0 1表示完全不透明
	 */
	public static void setBackgroundAlpha( Activity activity ,float bgAlpha) {
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		lp.alpha = bgAlpha;
		activity.getWindow().setAttributes(lp);
	}

	public static int dip2px(Context context, float dpValue) {
		final double scale = (double) context.getResources().getDisplayMetrics().density;
		return (int) ((double) dpValue * scale + 0.5);
	}

}
  
