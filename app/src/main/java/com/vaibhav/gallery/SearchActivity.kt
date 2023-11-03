package com.vaibhav.gallery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.elevation.SurfaceColors
import com.vaibhav.gallery.adapter.AlbumRecycleAdapter
import com.vaibhav.gallery.adapter.SearchViewAdapter
import com.vaibhav.gallery.adapter.VideoSearchAdapter
import com.vaibhav.gallery.databinding.ActivitySearchBinding
import com.vaibhav.gallery.model.ImageModel
import com.vaibhav.gallery.model.VideoModel
import com.vaibhav.gallery.search.SearchModel
import com.vaibhav.gallery.collectiondb.store.tool.IntentConstant

class SearchActivity : AppCompatActivity() {
    lateinit var binding: ActivitySearchBinding
    var searchText=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor=SurfaceColors.SURFACE_2.getColor(this)
        binding.searchView.setVisible(true)
        binding.searchView.toolbar.setNavigationOnClickListener {
            binding.searchView.clearFocusAndHideKeyboard()
            finishAfterTransition()
        }
        binding.searchView.requestFocusAndShowKeyboard()
        val searchModel= ViewModelProvider(this)[SearchModel::class.java]
        val adapter=SearchViewAdapter(this)
        val albumAdapter=AlbumRecycleAdapter(this)
        val videoAdapter=VideoSearchAdapter(this)


        binding.sRecycle.layoutManager=LinearLayoutManager(this)
        binding.sRecycle.adapter=adapter

        searchModel.getAlbumData().observe(this){
            albumAdapter.setModels(it)
            if (it.size==0){
                binding.contentText.visibility=View.VISIBLE
            }else{
                binding.contentText.visibility=View.GONE
            }
        }
        searchModel.getLiveData().observe(this){
            adapter.setList(it)
            if (it.size==0){
                binding.contentText.visibility=View.VISIBLE
            }else{
                binding.contentText.visibility=View.GONE
            }
        }

        searchModel.getVideoData().observe(this){
            videoAdapter.list=it
            videoAdapter.notifyDataSetChanged()
            if (it.size==0){
                binding.contentText.visibility=View.VISIBLE
            }else{
                binding.contentText.visibility=View.GONE
            }
        }
        binding.searchView.editText.addTextChangedListener {
            searchText=it.toString()
            when(binding.chipGroup.checkedChipId){
                R.id.name->{
                    searchModel.query(this,searchText)
                }
                R.id.album->{
                    searchModel.setAlbumQuery(this,searchText)
                }
                R.id.video->{
                    searchModel.setVideoQuery(this,searchText)
                }
            }

        }

        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            when(group.checkedChipId){
                R.id.name->{
                    binding.sRecycle.layoutManager=LinearLayoutManager(this)
                    binding.sRecycle.adapter=adapter
                    searchModel.query(this,searchText)
                }
                R.id.album->{
                   binding.sRecycle.layoutManager=GridLayoutManager(this,2)
                    binding.sRecycle.adapter=albumAdapter
                    searchModel.setAlbumQuery(this,searchText)
                }
                R.id.video->{
                    binding.sRecycle.layoutManager=LinearLayoutManager(this)
                    binding.sRecycle.adapter=videoAdapter
                    searchModel.setVideoQuery(this,searchText)
                }
            }
        }
        albumAdapter.setListeners { view, id, name ->
            val intent= Intent(this,OpenAlbumActivity::class.java)
            intent.putExtra("id",id)
            intent.putExtra("name",name)
            startActivity(intent)
        }
        adapter.onClickListeners=object :SearchViewAdapter.OnClickListeners{
            override fun onClick(view: View, position: Int, model: ImageModel) {
                val intent=Intent(this@SearchActivity,FullScreenActivity::class.java)
                intent.action= IntentConstant.ACTION_SEARCH
                intent.putExtra("p",position)
                intent.putExtra("cp",model.path)
                intent.putExtra("q",searchText)
                startActivity(intent)
            }

        }

        videoAdapter.onClickListeners=object :VideoSearchAdapter.OnClickListeners{
            override fun onClick(view: View, position: Int, model: VideoModel) {
                val intent=Intent(this@SearchActivity,VideoViewActivity::class.java)
                intent.putExtra("path",model.path)
                intent.putExtra("pos",position)
                intent.action= IntentConstant.ACTION_VIDEO_SINGLE
                startActivity(intent)
            }

        }

    }

}