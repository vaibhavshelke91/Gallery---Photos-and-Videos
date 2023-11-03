package com.vaibhav.gallery.adapter;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.vaibhav.gallery.R;
import com.vaibhav.gallery.model.ImageModel;

import java.util.ArrayList;
import java.util.List;

public class SlideRecycleAdapter extends RecyclerView.Adapter<SlideRecycleAdapter.ViewHolder> {
    private Context context;
    private List<ImageModel> imageModels=new ArrayList<>();

    private TextView textView;

    public void setDateTextView(TextView textView){
        this.textView=textView;
    }

    public SlideRecycleAdapter(Context context){
        this.context=context;
    }
    public void setImageModels(List<ImageModel> models){
        this.imageModels=models;
    }

    @NonNull
    @Override
    public SlideRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SlideRecycleAdapter.ViewHolder holder, int position) {
        if (imageModels.get(position).isBreakable()){
            holder.view.setVisibility(View.VISIBLE);
            holder.arrow.setVisibility(View.VISIBLE);
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText("Next Collections of \n \n"+ imageModels.get(position).getTextDate() +" \n \nSwipe To View");
        }else {
            holder.view.setVisibility(View.GONE);
            holder.arrow.setVisibility(View.GONE);
            holder.textView.setVisibility(View.GONE);

        }
        holder.imageView.setImage(ImageSource.uri(imageModels.get(position).getPath()));
        holder.imageView.setOrientation(imageModels.get(position).getOrientation());
      //  holder.imageView.setOrientation(SubsamplingScaleImageView.ORIENTATION_0);
    }



    @Override
    public int getItemCount() {
        return imageModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       public SubsamplingScaleImageView imageView;
        ImageView arrow;
        TextView textView;
        View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.fullImageView);
            arrow=itemView.findViewById(R.id.arrowNext);
            textView=itemView.findViewById(R.id.texView);
            view=itemView.findViewById(R.id.bgView);
            if (listener!=null){

                imageView.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener() {
                    @Override
                    public void onReady() {
                        listener.onLoad();
                    }

                    @Override
                    public void onImageLoaded() {
                        listener.onLoad();
                    }

                    @Override
                    public void onPreviewLoadError(Exception e) {

                    }

                    @Override
                    public void onImageLoadError(Exception e) {

                    }

                    @Override
                    public void onTileLoadError(Exception e) {

                    }

                    @Override
                    public void onPreviewReleased() {

                    }
                });

                imageView.setOnStateChangedListener(new SubsamplingScaleImageView.OnStateChangedListener() {
                    @Override
                    public void onScaleChanged(float newScale, int origin) {
                        if (newScale==imageView.getMinScale()){

                            listener.onStable();
                        }else {
                            listener.onZoom();
                        }
                    }

                    @Override
                    public void onCenterChanged(PointF newCenter, int origin) {

                    }
                });

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick();
                    }
                });

            }

        }
    }

    public interface OnImageLoadListeners{
        void onZoom();
        void onStable();
        void onClick();
        void onLoad();
    }


    private OnImageLoadListeners listener;

    public void setListener(OnImageLoadListeners listener){
        this.listener=listener;
    }
}
