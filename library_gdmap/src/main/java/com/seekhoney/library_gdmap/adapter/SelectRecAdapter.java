package com.seekhoney.library_gdmap.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.seekhoney.library_gdmap.R;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by ryan on 18/3/22.
 */

public class SelectRecAdapter extends RecyclerView.Adapter
{

    private Context mContext;
    private List<String> dates;

    @Inject
    public SelectRecAdapter(Context mContext, List<String>dates){
        this.mContext = mContext;
        this.dates = dates;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adpter_selectrec,parent,false);
        return new RecTaggerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RecTaggerViewHolder)holder).tv_recAdapter.setText(dates.get(position));
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }



    class RecTaggerViewHolder extends RecyclerView.ViewHolder{

        TextView tv_recAdapter;
        ImageView img_recAdapter;

        public RecTaggerViewHolder(View itemView) {
            super(itemView);
            tv_recAdapter = itemView.findViewById(R.id.tv_recAdapter);
            img_recAdapter = itemView.findViewById(R.id.img_recAdapter);
        }
    }
}
