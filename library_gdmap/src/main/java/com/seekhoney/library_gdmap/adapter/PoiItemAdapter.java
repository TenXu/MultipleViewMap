package com.seekhoney.library_gdmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.amap.api.services.core.PoiItem;
import com.seekhoney.library_gdmap.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryan on 17/6/6.
 */

public class PoiItemAdapter extends BaseAdapter
{
    private List<PoiItem> items = new ArrayList<>();
    private LayoutInflater inflater;
    private Context mContext;
    private ViewHolder viewHolder;
    private int selectedPosition;

    public PoiItemAdapter(Context mContext, List<PoiItem> items){
        this.items = items;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    public void setItems(List<PoiItem> items){
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = inflater.inflate(R.layout.view_item_poi,null);
            viewHolder = new ViewHolder();
            viewHolder.address_poi = (TextView) convertView.findViewById(R.id.address_poi);
            viewHolder.title_poi = (TextView) convertView.findViewById(R.id.title_poi);
            viewHolder.img_selected = (ImageView) convertView.findViewById(R.id.img_selected);
            viewHolder.tv_currentLocation = (TextView) convertView.findViewById(R.id.tv_currentLocation);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title_poi.setText(items.get(position).getTitle());
        viewHolder.address_poi.setText(items.get(position).getSnippet());
        if(position == 0){
            viewHolder.tv_currentLocation.setVisibility(View.VISIBLE);
        }else{
            viewHolder.tv_currentLocation.setVisibility(View.GONE);
        }
        if(position == selectedPosition){
            viewHolder.img_selected.setVisibility(View.VISIBLE);
        }else{
            viewHolder.img_selected.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder{
        TextView tv_currentLocation;
        TextView title_poi;
        TextView address_poi;
        ImageView img_selected;
    }

    public void setSelectedPosition(int selectedPosition){
        this.selectedPosition = selectedPosition;
    }
}
