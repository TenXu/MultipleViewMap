package com.seekhoney.library_gdmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.seekhoney.library_gdmap.R;

import java.util.List;


public class MapListAdapter extends BaseAdapter {
    private List<String> baiduBeanList;
    private LayoutInflater inflater;

    public MapListAdapter(Context mContext, List<String> list) {
        this.baiduBeanList = list;
        if (mContext != null) {
            inflater = LayoutInflater.from(mContext);
        }

    }

    @Override
    public int getCount() {
        return baiduBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return baiduBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.resolution_item_map, null);
            viewHolder.tv = (TextView) convertView.findViewById(R.id.resolution_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv.setText(baiduBeanList.get(position));
        return convertView;
    }

    class ViewHolder {
        private TextView tv;
    }
}
