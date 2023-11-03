package com.vaibhav.gallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.vaibhav.gallery.R;
import com.vaibhav.gallery.model.ImageModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CarouselRecycleAdapter extends RecyclerView.Adapter<CarouselRecycleAdapter.ViewHolder> {

    private Context context;
    private List<ImageModel> modelList=new ArrayList<>();

    public CarouselRecycleAdapter(Context context){
        this.context=context;
    }

    public void setModelList(List<ImageModel> models){
        this.modelList=models;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CarouselRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.corousel,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselRecycleAdapter.ViewHolder holder, int position) {

        Glide.with(holder.imageView.getContext())
                .load(new File(modelList.get(position).getPath()))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageView);

        holder.stamp.setText(modelList.get(position).getTextDate());
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView stamp;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.carousel_image_view);
            stamp=itemView.findViewById(R.id.stamp);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listeners!=null){
                        listeners.onClick(v,modelList,getAdapterPosition());
                    }
                }
            });
        }
    }
    private OnItemClickListeners listeners;

    public void setListeners(OnItemClickListeners listeners) {
        this.listeners = listeners;
    }
}
