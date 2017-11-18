package com.seekhoney.library_gdmap.logic;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.PoiItem;
import com.seekhoney.library_gdmap.constant.Const;
import com.seekhoney.library_gdmap.listener.OnCheckWhinAreaListener;
import com.seekhoney.library_gdmap.listener.OnSelectedPoiListener;
import com.seekhoney.library_gdmap.utils.LogUtil;

import java.util.List;

/**
 * Created by ryan on 17/6/7.
 */

public class LocateLogic
{
    private static LocateLogic ins;
    private Context mContext;
    private static String TAG = "MAP";
    private OnCheckWhinAreaListener listener;
    private OnSelectedPoiListener selectedPoiListener;

    private LocateLogic(Context mContext){
        this.mContext = mContext;
    }

    public static synchronized LocateLogic getIns(Context mContext){
        if(ins == null){
            ins = new LocateLogic(mContext);
        }
        return ins;
    }

    public void setSelectedPoiListener(OnSelectedPoiListener selectedPoiListener){
        this.selectedPoiListener = selectedPoiListener;
    }

    public void setWhinListener(OnCheckWhinAreaListener listener){

        this.listener = listener;
    }

    public void notifyIsWhin(int code){
        if(listener != null){
            listener.getWithinAreaCode(code);
        }
    }

    public void selectedPoiCallback(PoiItem item){

        if(selectedPoiListener != null){
            selectedPoiListener.selectedPoiCallback(item);
        }

    }

    /**
     * 检测定位点是否在圆的范围内
     * 废弃
     * */
    public boolean isContainPoint(Circle circle, LatLng latLng){

        if(latLng != null && circle != null){
            return circle.contains(latLng);
        }

        return false;
    }

    /**
     * 计算两点间距离,返回是否在范围内
     * */
    public boolean isWithin(LatLng a, LatLng b, float distance){

        if(a == null || b == null){
            return false;
        }
        float d = AMapUtils.calculateLineDistance(a, b);
        if(distance - d > 0){
            return true;
        }

        return false;
    }

    /**
     * 计算点是否在一组定位的范围内
     * */
    public int isWithinGroup(LatLng a , List<LatLng> list , float distance){

        if(a == null || list == null || list.size() == 0 || distance <= 0){

            return Const.BAD_REQUEST;
        }

        for (LatLng latLng : list){

            float d = AMapUtils.calculateLineDistance(a, latLng);
            if(distance - d > 0){
                LogUtil.getIns(TAG).i(latLng.toString());
                return Const.WITHIN_AREA;
            }
        }

        return Const.WITHOUT_AREA;
    }

    /**
     * 获取当前的网络状态
     * */
    /**
     * 检测当的网络（WLAN、3G/2G）状态
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }
}
