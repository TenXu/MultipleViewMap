package com.seekhoney.library_gdmap.task;

import android.content.Context;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

/**
 * Created by ryan on 17/6/13.
 */

public class PoiSearchTask implements PoiSearch.OnPoiSearchListener {

    private Context mContext;
    private PoiSearch poiSearch;
    private PoiSearch.Query query;
//    private LatLonPoint poiSearchPoint;

    public PoiSearchTask(Context mContext , LatLonPoint poiSearchPoint){
        this.mContext = mContext;
//        this.poiSearchPoint = poiSearchPoint;
        query = new PoiSearch.Query("", "", "");
        query.setPageNum(0); //查询第一页
        poiSearch = new PoiSearch(mContext, query);
        poiSearch.setOnPoiSearchListener(this);

    }

    public void search(LatLonPoint poiSearchPoint , int radius){

        poiSearch.setBound(new PoiSearch.SearchBound(poiSearchPoint, radius, true));//
        // 设置搜索区域为以lp点为圆心，其周围5000米范围
        poiSearch.searchPOIAsyn();// 异步搜索
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int rcode) {

        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            if (poiResult != null && poiResult.getQuery() != null) {// 搜索poi的结果
                if (poiResult.getQuery().equals(query)) {// 是否是同一条
//                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                }
            }
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int rcode) {

    }
}
