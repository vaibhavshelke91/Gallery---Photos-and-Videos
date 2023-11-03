package com.vaibhav.gallery.adapter;

import android.view.View;

import com.vaibhav.gallery.model.ImageModel;

import java.util.List;

public interface OnItemClickListeners {
    void onClick(View view, List<ImageModel> list, int pos);
}
