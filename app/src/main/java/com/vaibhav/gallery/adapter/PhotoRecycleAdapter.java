package com.vaibhav.gallery.adapter;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Dao;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.vaibhav.gallery.R;
import com.vaibhav.gallery.model.ImageModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhotoRecycleAdapter extends SelectableRecycleViewAdapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ImageModel> modelList=new ArrayList<>();

    private String currentDate="";
    public static final int  VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_ITEM = 1;

    private OnItemClick itemClick;

    public void setItemClick(OnItemClick itemClick) {
        this.itemClick = itemClick;
    }
    public PhotoRecycleAdapter(Context context){
        this.context=context;
    }

    public void addImageModels(List<ImageModel> models){
         this.modelList=models;
         notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==VIEW_TYPE_HEADER){
            return new SectionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.section_head,parent,false));
        }else {
            return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.all_recycle,parent,false),itemClick);
        }

    }



    @Override
    public int getItemViewType(int position) {
        if (!modelList.get(position).isBreakable()){
            return VIEW_TYPE_ITEM;
        }
        else {
            return VIEW_TYPE_HEADER;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        if (holder.getItemViewType()==VIEW_TYPE_ITEM){
            ItemViewHolder holder1=(ItemViewHolder)holder;
            int pos=holder1.getAdapterPosition();
            Glide.with(holder1.imageView.getContext())
                    .load(new File(modelList.get(pos).getPath()))
                    .apply(new RequestOptions().override(300,300))
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder1.imageView);
            if (isSelected(position)){
                holder1.mView.setVisibility(View.VISIBLE);
                holder1.checkBox.setVisibility(View.VISIBLE);
                holder1.checkBox.setChecked(true);
            }else {
                holder1.mView.setVisibility(View.GONE);
                holder1.checkBox.setVisibility(View.GONE);
                holder1.checkBox.setChecked(false);
            }

        }else {
           SectionViewHolder holder1=(SectionViewHolder) holder;
            holder1.textView.setText(modelList.get(position).getTextDate());

        }


      /*  Log.d("Photo "+position,"Size : "+modelList.get(position).getSize()
        +" Height : "+modelList.get(position).getHeight()+" Width :"+modelList.get(position).getWidth() +" Date : "+modelList.get(position).getDate()
        +" Name : "+modelList.get(position).getName()+" Bucket Name : "+modelList.get(position).getBucketName());*/
    }



    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public ArrayList<ImageModel> getSelectedImages(){
        List<Integer> integers=getSelectedItems();
        ArrayList<ImageModel> models=new ArrayList<>();
        for (int i=0;i<integers.size();i++){
            models.add(modelList.get(integers.get(i)));
        }
        return models;
    }
    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        ImageView imageView;
        View mView;
        CheckBox checkBox;

        OnItemClick onItemClick;
      //  private final SparseBooleanArray selectedItems=new SparseBooleanArray();
        public ItemViewHolder(@NonNull View itemView,OnItemClick onItemClick) {
            super(itemView);
            this.onItemClick=onItemClick;
            imageView=itemView.findViewById(R.id.gridImage);
            mView=itemView.findViewById(R.id.mView);
            checkBox=itemView.findViewById(R.id.mCheckBox);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }




        @Override
        public void onClick(View v) {
            if (onItemClick!=null){
                onItemClick.onItemClick(v,modelList.get(getAdapterPosition()),getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (onItemClick!=null){
                onItemClick.onLongPress(v,modelList.get(getAdapterPosition()),getAdapterPosition());
            }

            return true;
        }


    }

    public class SectionViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.header_title);
        }
    }

    public interface OnItemClick {
        void onItemClick(View view, ImageModel model, int position);
        void onLongPress(View view, ImageModel model, int position);
    }
}
