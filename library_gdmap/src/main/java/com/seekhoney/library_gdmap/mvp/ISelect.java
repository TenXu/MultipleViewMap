package com.seekhoney.library_gdmap.mvp;

import com.seekhoney.library_gdmap.listener.OnRecItemclickListener;
import com.seekhoney.library_gdmap.model.PoiIEntity;

import java.util.LinkedList;

/**
 * Created by ryan on 18/3/23.
 */

public interface ISelect extends  IView
{
    void showHistoryDate(LinkedList<PoiIEntity> list);

    OnRecItemclickListener getRecItemListener();
}
