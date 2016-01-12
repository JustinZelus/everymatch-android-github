package com.everymatch.saas.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by dor on 23/10/2014.
 */
public abstract class BaseRecyclerViewAdapter <T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {


    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public void onBindViewHolder(T viewHolder, final int position) {
        if (viewHolder != null && viewHolder.itemView != null && mOnItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(position);
                }
            });
        }
    }
}
