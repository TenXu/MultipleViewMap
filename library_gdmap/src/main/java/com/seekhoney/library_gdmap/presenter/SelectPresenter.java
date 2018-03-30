package com.seekhoney.library_gdmap.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seekhoney.library_gdmap.constant.Const;
import com.seekhoney.library_gdmap.model.PoiIEntity;
import com.seekhoney.library_gdmap.mvp.ISelect;
import com.seekhoney.library_gdmap.utils.LogUtil;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ryan on 18/3/23.
 */

public class SelectPresenter implements PoiSearch.OnPoiSearchListener, Inputtips.InputtipsListener {
    private ISelect view;

    /**
     * oi查询条件类
     */
    private PoiSearch.Query query;

    /**
     * 查询到的POI数据
     */
    private LinkedList<PoiIEntity> poiList = new LinkedList<>();

    @Inject
    public SelectPresenter(ISelect view) {
        this.view = view;
    }

    public void addSearchHistory(final PoiIEntity history) {

        final SharedPreferences sp = view.getActivity().getSharedPreferences(Const.SP_SELECT, Context.MODE_PRIVATE);
        String s = sp.getString(Const.KEY_HISTORY, "");
        Observable.just(s)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {

                        if (!TextUtils.isEmpty(s)) {
                            Gson gson = new Gson();
                            //TypeToken擦除泛型
                            LinkedList<PoiIEntity> list = gson.fromJson(s, new TypeToken<LinkedList<PoiItem>>() {
                            }.getType());
                            if (list.size() < 10) {
                                list.add(history);
                            } else {
                                list.remove(9);
                                list.add(9, history);
                            }

                            String newJson = gson.toJson(list);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.clear();
                            editor.putString(Const.KEY_HISTORY, newJson);

                            LogUtil.getIns("定位").i("存储搜索历史 : newJson=" + newJson);

                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        LogUtil.getIns("定位").i("存储搜索历史 : 出错e=" + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    public void showSearchHistory() {

        final SharedPreferences sp = view.getActivity().getSharedPreferences(Const.SP_SELECT, Context.MODE_PRIVATE);
        String s = sp.getString(Const.KEY_HISTORY, "");
        Observable.just(s)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {

                        if (!TextUtils.isEmpty(s)) {
                            Gson gson = new Gson();
                            LinkedList<PoiIEntity> list = gson.fromJson(s, new TypeToken<LinkedList<PoiIEntity>>() {
                            }.getType());
                            view.showHistoryDate(list);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        LogUtil.getIns("定位").i("获取搜索历史 : 出错e=" + e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void doSearchQuery(String keyWord) {
        int currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        PoiSearch poiSearch = new PoiSearch(view.getActivity(), query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS && poiResult != null && poiResult.getQuery() != null) {
            if (poiResult.getQuery().equals(query)) {
                List<PoiItem> poiItems = poiResult.getPois();
                if (poiItems != null && poiItems.size() > 0) {
                    poiList.clear();
                    for (PoiItem item : poiItems) {
                        PoiIEntity entity = new PoiIEntity(item.getTitle(), item.getSnippet(), item.getLatLonPoint().getLatitude(), item.getLatLonPoint().getLongitude());
                        poiList.add(entity);
                    }
                    view.showHistoryDate(poiList);
                }

            }
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int rCode) {

    }


    @Override
    public void onGetInputtips(List<Tip> list, int i) {

        Tip [] tips = list.toArray(new Tip[0]);
        if(tips == null && tips.length == 0) return;
        Observable.fromArray(tips).

                map(new Function<Tip, PoiIEntity>() {
                    @Override
                    public PoiIEntity apply(Tip tip) throws Exception {

                        if(tip != null){
                            return new PoiIEntity(tip.getName(), tip.getAddress(),tip.getPoint().getLatitude(),tip.getPoint().getLongitude());
                        }
                        return null;
                    }
                }).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Observer<PoiIEntity>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(PoiIEntity entity) {
                if(entity != null){
                    poiList.clear();
                    poiList.add(entity);
                }

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                view.showHistoryDate(poiList);
            }
        });
    }


    /**
     * Poi搜索
     */
    public void SearchQuery(String keyWord) {

        InputtipsQuery inputquery = new InputtipsQuery(keyWord, "");
        Inputtips inputTips = new Inputtips(view.getActivity(), inputquery);
        inputTips.setInputtipsListener(this);
        inputTips.requestInputtipsAsyn();

    }
}
