package com.seekhoney.library_gdmap.mvp;

import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.services.core.PoiItem;

import java.util.List;

/**
 * Created by ryan on 18/3/7.
 */

public interface ILocateView extends IView
{
    void notifyChanged(List<PoiItem> list);

    void setCenterMark(LatLng latLng ,float radius);

    MapView getMapView();

    LatLngBounds.Builder getBounds();

}
