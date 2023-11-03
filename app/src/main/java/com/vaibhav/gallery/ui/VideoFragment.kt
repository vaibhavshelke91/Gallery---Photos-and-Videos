package com.vaibhav.gallery.ui

import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ActionMode
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.elevation.SurfaceColors
import com.google.android.material.snackbar.Snackbar
import com.vaibhav.gallery.BuildConfig
import com.vaibhav.gallery.MainActivity
import com.vaibhav.gallery.R
import com.vaibhav.gallery.VideoViewActivity
import com.vaibhav.gallery.adapter.VideoRecycleAdapter
import com.vaibhav.gallery.databinding.FragmentVideoBinding
import com.vaibhav.gallery.datastore.DataStoreManager
import com.vaibhav.gallery.model.VideoModel
import com.vaibhav.gallery.model.VideoViewModel
import com.vaibhav.gallery.collectiondb.store.tool.IntentConstant
import com.vaibhav.gallery.collectiondb.store.tool.NestedScrollCoordinatorLayout
import com.vaibhav.gallery.collectiondb.store.tool.SingleTopMargin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.ArrayList

class VideoFragment : Fragment() {

    lateinit var binding:FragmentVideoBinding
    lateinit var mainActivity: MainActivity
    lateinit var model:VideoViewModel
    var actionMode: ActionMode?=null
    lateinit var mAdapter:VideoRecycleAdapter
    var order= DataStoreManager.V_LATEST

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentVideoBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model=ViewModelProvider(this)[VideoViewModel::class.java]
        readOrder()
        if (savedInstanceState==null){
            model.getVideos(requireContext(),order)
        }
    }

    private fun readOrder(){
        order= activity?.getSharedPreferences(DataStoreManager.STORE_VIDEO,Context.MODE_PRIVATE)
            ?.getString(DataStoreManager.KEY,DataStoreManager.V_LATEST) ?:DataStoreManager.V_LATEST
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity=activity as MainActivity
        initObserver()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("key","key")
        super.onSaveInstanceState(outState)
    }

    private fun check(it:String){
        when(it){
            DataStoreManager.V_NAME->{binding.sort.text="${resources.getString(R.string.name_a_z)}"}
            DataStoreManager.V_NAME_REVERSE->{binding.sort.text="${resources.getString(R.string.name_z_a)}"}
            DataStoreManager.V_LATEST->{binding.sort.text="${resources.getString(R.string.latest)}"}
            DataStoreManager.V_OLDEST->{binding.sort.text="${resources.getString(R.string.oldest)}"}
            DataStoreManager.V_SIZE->{binding.sort.text="${resources.getString(R.string.size_largest)}"}
            DataStoreManager.V_SIZE_REVERSE->{binding.sort.text="${resources.getString(R.string.size_smallest)}"}

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
       actionMode?.let{
            actionMode!!.finish()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mCoordinate.setPassMode(NestedScrollCoordinatorLayout.PASS_MODE_PARENT_FIRST)
        mAdapter=VideoRecycleAdapter(this)
        val manager=StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        manager.gapStrategy=StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        binding.photoRecycle.layoutManager=manager
        binding.photoRecycle.adapter=mAdapter
        binding.photoRecycle.addItemDecoration(SingleTopMargin(15))
        model.getLiveData().observe(viewLifecycleOwner){
            binding.progressIndiactor.visibility=View.GONE
            mAdapter.list=it
            if (it.isEmpty()){
                binding.lottieView.visibility=View.VISIBLE
                binding.nothing.visibility=View.VISIBLE
            }else{
                binding.lottieView.visibility=View.GONE
                binding.nothing.visibility=View.GONE
            }
            mAdapter.notifyDataSetChanged()
            binding.count.text="${it.size} ${resources.getString(R.string.videos)}"
        }

        mAdapter.onClickListeners=object :VideoRecycleAdapter.OnClickListeners{
            override fun onClick(view: View, position: Int, model: VideoModel) {
                if (actionMode==null){
                    startVideo(position, model)
                }else{
                    toggleSelection(position)
                }
            }

            override fun onLongPress(view: View, position: Int, model: VideoModel) {
                if (actionMode==null){
                    actionMode=mainActivity.startSupportActionMode(ActionCallback())
                }
                toggleSelection(position)
            }

        }
        check(order)
        binding.sort.setOnClickListener {
            val sheet=VideoSortSheet()
            sheet.show(childFragmentManager,"VIDEO_SORT")
            sheet.lifecycle.addObserver(LifecycleEventObserver { source, event ->
                if (event== Lifecycle.Event.ON_DESTROY){
                    readOrder()
                    check(order)
                    model.getVideos(requireContext(),order)
                }
            })
        }
    }

    private inner class ActionCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode!!.menuInflater.inflate(R.menu.action_menu,menu)
            mainActivity.binding.searchBar.visibility=View.GONE
            val color= SurfaceColors.SURFACE_2.getColor(requireContext())
            mainActivity.window.statusBarColor=color
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when(item!!.itemId){
                R.id.action_share->{
                    CoroutineScope(Dispatchers.IO).launch {
                        startFileShareIntent(mAdapter.getSelectedVideos())
                    }
                }
                R.id.action_delete->{
                    lifecycleScope.launch {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("${resources.getString(R.string.delete)}")
                            .setMessage("${resources.getString(R.string.do_you_want_to_delete)}")
                            .setPositiveButton("${resources.getString(R.string.yes)}") { i, j ->
                                lifecycleScope.launch {
                                    val dialog=MaterialAlertDialogBuilder(requireContext())
                                    val view=layoutInflater.inflate(R.layout.delete_layout,null)
                                    dialog.setView(view)
                                    val d=  dialog.create()
                                    d.show()
                                    mainActivity.deleteVideos(mAdapter.getSelectedVideos())
                                    d.dismiss()
                                    model.getVideos(requireContext(),order)

                                }
                                mode!!.finish()
                                mAdapter.clearSelection()
                                Snackbar.make(mainActivity.binding.mCoordinate,"${resources.getString(R.string.deleting)}",
                                    Snackbar.LENGTH_SHORT).show()

                            }.setNegativeButton("${resources.getString(R.string.no)}"){i,j->
                                i.dismiss()
                            }.create().show()

                    }

                }
            }
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            mAdapter.clearSelection()
            actionMode=null
            mainActivity.binding.searchBar.visibility=View.VISIBLE
            mainActivity.window.statusBarColor= Color.TRANSPARENT

        }
    }

    private fun startFileShareIntent(itemsList: ArrayList<VideoModel>) {
        val uriArrayList: ArrayList<Uri> = ArrayList()
        GlobalScope.launch(Dispatchers.IO) {
            runCatching {
                itemsList!!.forEach {
                    uriArrayList.add(
                        FileProvider.getUriForFile(
                            requireContext(),
                            "${BuildConfig.APPLICATION_ID}.provider",
                            File(it.path)
                        )
                    )
                }

            }.onSuccess {
                mainActivity.runOnUiThread {
                    if (uriArrayList.size > 0) {
                        val intent = Intent()
                        intent.action = Intent.ACTION_SEND_MULTIPLE
                        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriArrayList)
                        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        intent.type = "video/*"
                        startActivity(Intent.createChooser(intent, null))
                    }
                }
            }
                .onFailure {
                    it.message?.let { it1 -> Log.e("SHARING_FAILED", it1) }
                }
        }
    }

    private fun toggleSelection(position: Int) {
        mAdapter.toggleSelection(position)
        val count = mAdapter.selectedItemCount
        if (count == 0) {
            actionMode!!.finish()
        } else {
            actionMode!!.title = "$count ${resources.getString(R.string.selected)}"
            actionMode!!.invalidate()
        }
    }
    private fun startVideo(position:Int,model: VideoModel){
        val intent=Intent(requireContext(),VideoViewActivity::class.java)
        intent.putExtra("path",model.path)
        intent.putExtra("pos",position)
        intent.putExtra("o",order)
        intent.action= IntentConstant.ACTION_VIDEO_ALL
        startActivity(intent)
    }


    lateinit var contentObserver: ContentObserver

    private fun initObserver(){
        contentObserver=object : ContentObserver(null){
            override fun onChange(selfChange: Boolean) {
                if (mainActivity!=null){
                    model.getVideos(mainActivity,order)
                }
            }
        }
        mainActivity.contentResolver.registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,true,contentObserver)
    }

}