package com.vaibhav.gallery.editor;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vaibhav.gallery.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class FilterRecycleAdapter extends RecyclerView.Adapter<FilterRecycleAdapter.ViewHolder> {
    private Context context;


    private ArrayList<ToolModel> hashMap;

    public FilterRecycleAdapter(Context context){
        this.context=context;
        hashMap=getToolList();
    }

    public ToolModel getFirst(){
        return hashMap.get(0);
    }

    @NonNull
    @Override
    public FilterRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_recycle,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull FilterRecycleAdapter.ViewHolder holder, int position) {

        holder.editorView.setImageResource(hashMap.get(position).drawable);
        holder.textView.setText(hashMap.get(position).title);

    }

    @Override
    public int getItemCount() {
        return hashMap.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
      ImageView editorView;
      TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            editorView=itemView.findViewById(R.id.filterView);
            textView=itemView.findViewById(R.id.title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listeners!=null){
                        listeners.onClick(hashMap.get(getAdapterPosition()));
                    }
                }
            });
        }
    }
    private OnClickListeners listeners;

    public void setListeners(OnClickListeners listeners){
        this.listeners=listeners;
    }

    public interface OnClickListeners{
        void onClick(ToolModel model);
    }

    public class ToolModel{
        private int drawable;
        private String title;

        private float value;

        private float max;

        private float min;

        public ToolModel(int drawable, String title, float value, float max, float min) {
            this.drawable = drawable;
            this.title = title;
            this.value = value;
            this.max = max;
            this.min = min;
        }

        public float getMax() {
            return max;
        }

        public void setMax(float max) {
            this.max = max;
        }

        public float getMin() {
            return min;
        }

        public void setMin(float min) {
            this.min = min;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }

        public ToolModel(int drawable, String title) {
            this.drawable = drawable;
            this.title = title;
        }

        public int getDrawable() {
            return drawable;
        }

        public void setDrawable(int drawable) {
            this.drawable = drawable;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    private ArrayList<ToolModel> getToolList(){
        ArrayList<ToolModel> hashMap=new ArrayList<>();
       hashMap.add(new ToolModel(R.drawable.brightness,BRIGHTNESS,0.0f,1.0f,-1.0f));
       hashMap.add(new ToolModel(R.drawable.contrast,CONTRAST,1.0f,4.0f,0.0f));
       hashMap.add(new ToolModel(R.drawable.staturation,SATURATION,1.0f,2.0f,-0.0f));
       hashMap.add(new ToolModel(R.drawable.exposure,EXPOSURE,0.0f,10.0f,-10.f));
        return hashMap;
    }

    public static final String BRIGHTNESS="Brightness";
    public static final String CONTRAST="Contrast";
    public static final String SATURATION="Saturation";
    public static final String EXPOSURE="Exposure";
}
