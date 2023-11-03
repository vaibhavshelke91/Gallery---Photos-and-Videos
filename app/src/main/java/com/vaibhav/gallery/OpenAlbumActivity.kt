package com.vaibhav.gallery

import android.content.Intent
import android.database.ContentObserver
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.view.ActionMode
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.elevation.SurfaceColors
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.Snackbar
import com.vaibhav.gallery.adapter.OpenAlbumRecycleAdapter
import com.vaibhav.gallery.databinding.ActivityOpenAlbumBinding
import com.vaibhav.gallery.model.AlbumSelectionModel
import com.vaibhav.gallery.model.ImageModel
import com.vaibhav.gallery.collectiondb.store.tool.IntentConstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

import kotlin.collections.ArrayList

class OpenAlbumActivity : AppCompatActivity() ,ActionMode.Callback{
    lateinit var binding: ActivityOpenAlbumBinding

    lateinit var adapter:OpenAlbumRecycleAdapter
    var actionMode:ActionMode?=null
    lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    lateinit var contentObserver:ContentObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOpenAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent=intent
        val id=intent.getIntExtra("id",0)
        val name=intent.getStringExtra("name")
        val model=ViewModelProvider(this)[AlbumSelectionModel::class.java]

        binding.mAppBar.statusBarForeground= MaterialShapeDrawable.createWithElevationOverlay(this)
        adapter=OpenAlbumRecycleAdapter(this)
        binding.mRecycle.layoutManager=GridLayoutManager(this,4)
        binding.mRecycle.adapter=adapter


        model.getAlbumList().observe(this){
            adapter.setModels(it)
            try {
                val c = android.view.animation.AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
                binding.mRecycle.layoutAnimation=c
                adapter.notifyDataSetChanged()
                binding.mRecycle.scheduleLayoutAnimation()
            }catch (e:Exception){
                e.printStackTrace()
            }

        }
        model.getAllImages(this,id)
        binding.topAppBar.title=name

        contentObserver=object :ContentObserver(null){
            override fun onChange(selfChange: Boolean) {
                model.getAllImages(this@OpenAlbumActivity,id)
            }

        }
        contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,true,contentObserver)


        adapter.setOnClickListeners(object :OpenAlbumRecycleAdapter.OnItemClick{
            override fun onItemClick(view: View?, model: ImageModel?, position: Int) {

                if (actionMode==null) {
                    val intent = Intent(this@OpenAlbumActivity, FullScreenActivity::class.java)
                    intent.action = IntentConstant.ACTION_ALBUM
                    intent.putExtra("p", position)
                    intent.putExtra("cp", model!!.path)
                    intent.putExtra("or",model!!.orientation)
                    intent.putExtra("id", id)
                    startActivity(intent)
                }else{
                    toggleSelection(position)
                }
            }

            override fun onLongPress(view: View?, model: ImageModel?, position: Int) {
               if (actionMode==null){
               actionMode=startSupportActionMode(this@OpenAlbumActivity)
               }
                   toggleSelection(position)

            }

        })

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        intentSenderLauncher=registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()){
            if (it.resultCode== RESULT_OK){
                Snackbar.make(binding.mRecycle,"${resources.getString(R.string.deleted)}",Snackbar.LENGTH_SHORT).show()
            }else{
                Log.d("Delete","Something Error")
            }
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

    override fun onDestroy() {
        super.onDestroy()
        contentResolver.unregisterContentObserver(contentObserver)
    }
    public suspend fun deletePhoto(list: ArrayList<ImageModel>){
        withContext(Dispatchers.IO) {
            val uriList=ArrayList<Uri>()
            for (i in list){
                uriList.add(Uri.parse(i.contentUri))
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                val intentSender=MediaStore.createDeleteRequest(contentResolver,uriList).intentSender
                intentSender.let {
                    intentSenderLauncher.launch(
                        it?.let { it1 -> IntentSenderRequest.Builder(it1).build() }
                    )
                }
            }else{
                try {
                    for (uri in uriList){
                        contentResolver.delete(uri,null,null)
                    }
                }catch (e:SecurityException){
                    e.printStackTrace()
                }
            }

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
            R.id.action_delete->{
                MaterialAlertDialogBuilder(this)
                    .setTitle("${resources.getString(R.string.delete)}")
                    .setMessage("${resources.getString(R.string.delete_permanently)}")
                    .setPositiveButton("Yes") { i, j ->
                        lifecycleScope.launch {
                            val list= ArrayList<ImageModel>(adapter.selectedImages)
                            val dialog=MaterialAlertDialogBuilder(this@OpenAlbumActivity)
                            val view=layoutInflater.inflate(R.layout.delete_layout,null)
                            dialog.setView(view)
                            val d=  dialog.create()
                            d.show()
                            deletePhoto(list)
                            d.dismiss()
                            Snackbar.make(binding.mRecycle,"${resources.getString(R.string.deleted)}",Snackbar.LENGTH_SHORT).show()

                        }
                        mode!!.finish()
                        adapter.clearSelection()
                     //   Snackbar.make(binding.mRecycle,"Deleting...",Snackbar.LENGTH_SHORT).show()

                    }.setNegativeButton("${resources.getString(R.string.no)}"){i,j->
                        i.dismiss()
                    }.create().show()
            }
        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        window.statusBarColor= Color.TRANSPARENT
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
                            this@OpenAlbumActivity,
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