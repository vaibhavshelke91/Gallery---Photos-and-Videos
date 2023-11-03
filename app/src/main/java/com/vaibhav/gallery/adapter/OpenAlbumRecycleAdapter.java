package com.vaibhav.gallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

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

public class OpenAlbumRecycleAdapter extends SelectableRecycleViewAdapter<OpenAlbumRecycleAdapter.ViewHolder> {

    private Context context;

    private List<ImageModel> models=new ArrayList<>();

    public OpenAlbumRecycleAdapter(Context context){
        this.context=context;
    }

    public void setModels(List<ImageModel> models){
        this.models=models;
        notifyDataSetChanged();
    }
    @Override
    public OpenAlbumRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.all_recycle,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull OpenAlbumRecycleAdapter.ViewHolder holder, int position) {
        if (holder.imageView.getContext()!=null) {
            Glide.with(holder.imageView.getContext())
                    .load(new File(models.get(position).getPath()))
                    .apply(new RequestOptions().override(500, 500))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.imageView);
        }
        if (isSelected(position)){
            holder.mView.setVisibility(View.VISIBLE);
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(true);
        }else {
            holder.mView.setVisibility(View.GONE);
            holder.checkBox.setVisibility(View.GONE);
            holder.checkBox.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public ArrayList<ImageModel> getSelectedImages(){
        List<Integer> integers=getSelectedItems();
        ArrayList<ImageModel> modelsList=new ArrayList<>();
        for (int i=0;i<integers.size();i++){
            modelsList.add(models.get(integers.get(i)));
        }
        return modelsList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        ImageView imageView;
        View mView;
        CheckBox checkBox;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.gridImage);
            mView=itemView.findViewById(R.id.mView);
            checkBox=itemView.findViewById(R.id.mCheckBox);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (onClick!=null){
                onClick.onItemClick(v,models.get(getAdapterPosition()),getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (onClick!=null){
                onClick.onLongPress(v,models.get(getAdapterPosition()),getAdapterPosition());
            }
            return true;
        }
    }


    private OnItemClick onClick;
    public void setOnClickListeners(OnItemClick onClick){
        this.onClick=onClick;
    }

    public interface OnItemClick {
        void onItemClick(View view, ImageModel model, int position);
        void onLongPress(View view, ImageModel model, int position);
    }
}
