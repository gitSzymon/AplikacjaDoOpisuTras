package com.example.aplikacjadoopisutras;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.aplikacjadoopisutras.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import logic.Route;

public class PointListAdapter extends RecyclerView.Adapter<PointListAdapter.ViewHolder> {

    private List<Route> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    PointListAdapter(Context context, List<Route> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.list_item_route, parent, false);
        //mInflater.inflate(R.layout.list_item_route, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.textRouteId= v.findViewById(R.id.route_id);
        viewHolder.textRouteName= v.findViewById(R.id.route_name);
        return viewHolder;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Route route = mData.get(position);
        Integer tmp = new Integer(String.valueOf(route.getRouteId()));
        TextView textRouteId = holder.textRouteId;
        textRouteId.setText(tmp);
       // ((ViewHolder) holder).textRouteId.setText(tmp);
       // ((ViewHolder) holder).textRouteName.setText(route.getRouteName());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textRouteId;
        TextView textRouteName;

        ViewHolder(View itemView) {
            super(itemView);
            textRouteId = itemView.findViewById(R.id.route_id);
            textRouteName = itemView.findViewById(R.id.route_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Route getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}