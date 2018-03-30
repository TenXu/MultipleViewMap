package com.seekhoney.library_gdmap.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.seekhoney.library_gdmap.R;
import com.seekhoney.library_gdmap.listener.OnRecItemclickListener;
import com.seekhoney.library_gdmap.model.PoiIEntity;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by ryan on 18/3/22.
 */

public class SelectRecAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<PoiIEntity> dates;
    private OnRecItemclickListener listener;


    @Inject
    public SelectRecAdapter(Context mContext, List<PoiIEntity> dates, OnRecItemclickListener listener) {
        this.mContext = mContext;
        this.dates = dates;
        this.listener = listener;
    }

    public void setDates(List<PoiIEntity> dates) {
        this.dates = dates;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adpter_selectrec, parent, false);
        return new RecTaggerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RecTaggerViewHolder) holder).img_recAdapter.setVisibility(View.VISIBLE);
        ((RecTaggerViewHolder) holder).title_recAdapter.setVisibility(View.VISIBLE);
        ((RecTaggerViewHolder) holder).address_recAdapter.setVisibility(View.VISIBLE);
        ((RecTaggerViewHolder) holder).title_recAdapter.setText(dates.get(position).getTitle());
        ((RecTaggerViewHolder) holder).address_recAdapter.setText(dates.get(position).getAddress());
        ((RecTaggerViewHolder) holder).root.setOnClickListener(new ItemClick(dates.get(position)));
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    @Override
    public long getItemId(int position) {
        return position;

    }

    class RecTaggerViewHolder extends RecyclerView.ViewHolder {

        TextView title_recAdapter;
        TextView address_recAdapter;
        ImageView img_recAdapter;
        ConstraintLayout root;

        public RecTaggerViewHolder(View itemView) {
            super(itemView);
            title_recAdapter = itemView.findViewById(R.id.title_recAdapter);
            address_recAdapter = itemView.findViewById(R.id.address_recAdapter);
            img_recAdapter = itemView.findViewById(R.id.img_recAdapter);
            root = itemView.findViewById(R.id.adpter_selectrec_root);
        }
    }

    class ItemClick implements View.OnClickListener{

        PoiIEntity poiIEntity;

        public ItemClick(PoiIEntity entity){
            this.poiIEntity = entity;
        }
        @Override
        public void onClick(View v) {
            listener.recItemCallback(poiIEntity);
        }
    }

}
