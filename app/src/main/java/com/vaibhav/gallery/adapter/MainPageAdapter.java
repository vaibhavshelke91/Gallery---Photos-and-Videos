package com.vaibhav.gallery.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.vaibhav.gallery.ui.AlbumFragment;
import com.vaibhav.gallery.ui.ExploreFragment;
import com.vaibhav.gallery.ui.LikedFragment;
import com.vaibhav.gallery.ui.PhotoFragment;
import com.vaibhav.gallery.ui.VideoFragment;

public class MainPageAdapter extends FragmentStateAdapter {
    public MainPageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:return new PhotoFragment();
            case 1:return new AlbumFragment();
            case 2:return new LikedFragment();
            case 3:return new VideoFragment();

        }
        return new PhotoFragment();
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
