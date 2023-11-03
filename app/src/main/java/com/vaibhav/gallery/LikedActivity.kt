package com.vaibhav.gallery

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.ActionMode
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.elevation.SurfaceColors
import com.vaibhav.gallery.adapter.OpenAlbumRecycleAdapter
import com.vaibhav.gallery.databinding.ActivityLikedBinding
import com.vaibhav.gallery.db.LikedViewModel
import com.vaibhav.gallery.model.ImageModel
import com.vaibhav.gallery.collectiondb.store.tool.IntentConstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.ArrayList

class LikedActivity : AppCompatActivity() ,ActionMode.Callback{
    lateinit var binding: ActivityLikedBinding

    lateinit var adapter:OpenAlbumRecycleAdapter
    var actionMode:ActionMode?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLikedBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val model=ViewModelProvider(this)[LikedViewModel::class.java]
        binding.mRecycle.layoutManager=GridLayoutManager(this,4)
        adapter=OpenAlbumRecycleAdapter(this)
        binding.mRecycle.adapter=adapter
        binding.topAppBar.title="Liked"
        model.getLiveData().observe(this){
            adapter.setModels(it)
            val c = android.view.animation.AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
            binding.mRecycle.layoutAnimation=c
            binding.mRecycle.scheduleLayoutAnimation()
        }

        adapter.setOnClickListeners(object :OpenAlbumRecycleAdapter.OnItemClick{
            override fun onItemClick(view: View?, model: ImageModel?, position: Int) {

                if (actionMode==null) {
                    val intent = Intent(this@LikedActivity, FullScreenActivity::class.java)
                    intent.action = IntentConstant.ACTION_LIKED
                    intent.putExtra("p", position)
                    intent.putExtra("cp", model!!.path)

                    startActivity(intent)
                }else{
                    toggleSelection(position)
                }
            }

            override fun onLongPress(view: View?, model: ImageModel?, position: Int) {
                if (actionMode==null){
                    actionMode=startSupportActionMode(this@LikedActivity)
                }
                toggleSelection(position)

            }

        })



        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun toggleSelection(position: Int) {
        adapter.toggleSelection(position)
        val count = adapter.selectedItemCount
        if (count == 0) {
            actionMode!!.finish()
        } else {
            actionMode!!.title = count.toString()
            actionMode!!.invalidate()
        }
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {

        mode!!.menuInflater.inflate(R.menu.action_menu,menu)
        val color= SurfaceColors.SURFACE_2.getColor(this)
        window.statusBarColor=color
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {

        return true
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.action_share->{
                CoroutineScope(Dispatchers.IO).launch {
                    startFileShareIntent(adapter.selectedImages)
                }
            }
        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        window.statusBarColor=Color.TRANSPARENT
        adapter.clearSelection()
        actionMode=null


    }

    private fun startFileShareIntent(itemsList: ArrayList<ImageModel>) {
        val uriArrayList: ArrayList<Uri> = ArrayList()
        GlobalScope.launch(Dispatchers.IO) {
            runCatching {
                itemsList!!.forEach {
                    uriArrayList.add(
                        FileProvider.getUriForFile(
                            this@LikedActivity,
                            "${BuildConfig.APPLICATION_ID}.provider",
                            File(it.path)
                        )
                    )
                }

            }.onSuccess {
                runOnUiThread {
                    if (uriArrayList.size > 0) {
                        val intent = Intent()
                        intent.action = Intent.ACTION_SEND_MULTIPLE
                        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriArrayList)
                        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        intent.type = "image/*"
                        startActivity(Intent.createChooser(intent, "Share"))
                    }
                }
            }
                .onFailure {
                    it.message?.let { it1 -> Log.e("SHARING_FAILED", it1) }
                }
        }
    }
}