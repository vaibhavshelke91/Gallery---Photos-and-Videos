package com.vaibhav.gallery.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.vaibhav.gallery.R;
import com.vaibhav.gallery.model.AlbumModel;
import com.vaibhav.gallery.model.ImageModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AlbumRecycleAdapter extends RecyclerView.Adapter<AlbumRecycleAdapter.ViewHolder> {

    private Context context;
    private List<AlbumModel> models=new ArrayList<>();


    public void setModels(List<AlbumModel> list){
        this.models=list;
        notifyDataSetChanged();

    }
    public AlbumRecycleAdapter(Context context) {
        this.context = context;

    }

    @Override
    public AlbumRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.album_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumRecycleAdapter.ViewHolder holder, int position) {

        if (holder.imageView.getContext()!=null) {
            Glide.with(holder.imageView.getContext())
                    .load(new File(models.get(position).getPath()))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.imageView);
        }
        holder.name.setText(models.get(position).getName());
        holder.items.setText(models.get(position).getModels().size()+" Images");
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name,items;
        CardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.albumImg);
            name=itemView.findViewById(R.id.albumName);
            items=itemView.findViewById(R.id.albumImgCount);
            card=itemView.findViewById(R.id.mCard);
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listeners!=null){
                        listeners.onClick(v,models.get(getAbsoluteAdapterPosition()).getId(),models.get(getAbsoluteAdapterPosition()).getName());
                    }
                }
            });
        }
    }

    public interface OnClickListeners{
        void onClick(View view,int id,String name);
    }
    private OnClickListeners listeners;

    public void setListeners(OnClickListeners listeners){
        this.listeners=listeners;
    }
}
