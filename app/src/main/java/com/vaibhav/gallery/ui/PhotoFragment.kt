package com.vaibhav.gallery.ui

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.appcompat.view.ActionMode
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.elevation.SurfaceColors
import com.google.android.material.snackbar.Snackbar
import com.vaibhav.gallery.BuildConfig
import com.vaibhav.gallery.FullScreenActivity
import com.vaibhav.gallery.MainActivity
import com.vaibhav.gallery.R
import com.vaibhav.gallery.adapter.DoubleGridAdapter
import com.vaibhav.gallery.databinding.FragmentPhotoBinding
import com.vaibhav.gallery.datastore.DataStoreManager
import com.vaibhav.gallery.datastore.StoreOjb
import com.vaibhav.gallery.datastore.getGridStoreLiveData
import com.vaibhav.gallery.datastore.getTitleStateLiveData
import com.vaibhav.gallery.model.ImageModel
import com.vaibhav.gallery.model.ImageViewModel
import com.vaibhav.gallery.collectiondb.store.tool.IntentConstant
import com.vaibhav.gallery.collectiondb.store.tool.NestedScrollCoordinatorLayout
import com.vaibhav.gallery.collectiondb.store.tool.TopMargin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


class PhotoFragment : Fragment() {

    lateinit var binding: FragmentPhotoBinding
    lateinit var mainActivity: MainActivity
    lateinit var model:ImageViewModel
    var actionMode:ActionMode?=null

    var order=DataStoreManager.LATEST

   // lateinit var adapter: PhotoRecycleAdapter

    lateinit var mAdapter: DoubleGridAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model=ViewModelProvider(this)[ImageViewModel::class.java]
        readOrder()
        if (savedInstanceState==null){
                model.getAllImages(requireContext(),order)
            }

    }

    private fun readOrder(){
        order= activity?.getSharedPreferences(DataStoreManager.STORE,Context.MODE_PRIVATE)
            ?.getString(DataStoreManager.KEY,DataStoreManager.LATEST) ?:DataStoreManager.LATEST
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("new","old")
        super.onSaveInstanceState(outState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity=activity as MainActivity
        initObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentPhotoBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)





       binding.mCoordinate.setPassMode(NestedScrollCoordinatorLayout.PASS_MODE_PARENT_FIRST)
        binding.photoRecycle.addItemDecoration(TopMargin(15))

       mAdapter=DoubleGridAdapter(this)



      /*  val layoutManager=GridLayoutManager(requireContext(),4,GridLayoutManager.VERTICAL,false)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter.getItemViewType(position) == PhotoRecycleAdapter.VIEW_TYPE_HEADER) 4 else 1
            }
        }*/

     //   binding.photoRecycle.layoutManager=layoutManager
      //  binding.photoRecycle.adapter=adapter

        val sManager=StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        sManager.gapStrategy=StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS

        binding.photoRecycle.layoutManager=sManager
        binding.photoRecycle.adapter=mAdapter

       // binding.photoRecycle.addItemDecoration(MarginItemDecoration(10))

        context?.getGridStoreLiveData()?.observe(viewLifecycleOwner){
            val pref=it[StoreOjb.ORDER]
            pref?.let { sManager.spanCount=pref }
        }

        context?.getTitleStateLiveData()?.observe(viewLifecycleOwner){
            val pref=it[StoreOjb.TITLE_VISIBILITY]
            pref?.let {
                mAdapter.titleVisibility=pref
                mAdapter.notifyDataSetChanged()
            }
            }


        mAdapter.onItemClick=object :DoubleGridAdapter.OnItemClick{
            override fun onItemClick(view: View, model: ImageModel, position: Int) {

                if (actionMode==null){
                    openFullScreenActivity(view!!,position, model!!.path,model.orientation)
                }else{
                    toggleSelection(position)
                }

            }

            override fun onLongPress(view: View, model: ImageModel, position: Int) {
                if (actionMode==null){
                    actionMode=mainActivity.startSupportActionMode(ActionCallback())
                }
                toggleSelection(position)
            }

        }


        model.getAudioList().observe(viewLifecycleOwner){
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

            binding.count.text="${it.size} ${resources.getString(R.string.images)}"

            Log.d("Photo","Observing...")

        }
        check(order)
        binding.sort.setOnClickListener {
            val sheet=SortListDialogFragment()

            sheet.show(childFragmentManager,"SORT")
            sheet.lifecycle.addObserver(LifecycleEventObserver { source, event ->
                if (event==Lifecycle.Event.ON_DESTROY){
                    readOrder()
                    check(order)
                    model.getAllImages(requireContext(),order)
                }
            })
        }



    }

    private fun check(it:String){
        when(it){
            DataStoreManager.NAME->{binding.sort.text="${resources.getString(R.string.name_a_z)}"}
            DataStoreManager.NAME_REVERSE->{binding.sort.text="${resources.getString(R.string.name_z_a)}"}
            DataStoreManager.LATEST->{binding.sort.text="${resources.getString(R.string.latest)}"}
            DataStoreManager.OLDEST->{binding.sort.text="${resources.getString(R.string.oldest)}"}
            DataStoreManager.SIZE->{binding.sort.text="${resources.getString(R.string.size_largest)}"}
            DataStoreManager.SIZE_REVERSE->{binding.sort.text="${resources.getString(R.string.size_smallest)}"}

        }

    }



    private fun openFullScreenActivity(view: View,pos:Int,s:String,orientation:Int){
        val intent=Intent(requireContext(),FullScreenActivity::class.java)
        intent.action= IntentConstant.ACTION_ALL
        intent.putExtra("p",pos)
        intent.putExtra("cp",s)
        intent.putExtra("o",order)
        intent.putExtra("or",orientation)
        ViewCompat.setTransitionName(view, "full")
        val option=ActivityOptions.makeSceneTransitionAnimation(activity,view,ViewCompat.getTransitionName(view))
        startActivity(intent,option.toBundle())
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
                       startFileShareIntent(mAdapter.getSelectedImages())
                   }
                }
                R.id.action_delete->{
                    lifecycleScope.launch {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("${resources.getString(R.string.delete)}")
                            .setMessage("${resources.getString(R.string.delete_permanently)}")
                            .setPositiveButton("${resources.getString(R.string.yes)}") { i, j ->
                                lifecycleScope.launch {
                                    val dialog=MaterialAlertDialogBuilder(requireContext())
                                    val view=layoutInflater.inflate(R.layout.delete_layout,null)
                                    dialog.setView(view)
                                    val d=  dialog.create()
                                    d.show()
                                    mainActivity.deletePhoto(mAdapter.getSelectedImages())
                                    d.dismiss()
                                    model.getAllImages(requireContext(),order)

                                }
                                mode!!.finish()
                                mAdapter.clearSelection()
                                Snackbar.make(mainActivity.binding.mCoordinate,"${resources.getString(R.string.deleting)}",Snackbar.LENGTH_SHORT).show()

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
            mainActivity.window.statusBarColor=Color.TRANSPARENT

        }
    }

    private fun startFileShareIntent(itemsList:ArrayList<ImageModel>) {
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
                        intent.type = "image/*"
                        startActivity(Intent.createChooser(intent, null))
                    }
                }
            }
                .onFailure {
                    it.message?.let { it1 -> Log.e("SHARING_FAILED", it1) }
                }
        }
    }

    lateinit var contentObserver:ContentObserver

    fun initObserver(){
        contentObserver=object :ContentObserver(null){
            override fun onChange(selfChange: Boolean) {
                if (mainActivity!=null){
                    model.getAllImages(mainActivity,order)
                }
            }
        }
       mainActivity.contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,true,contentObserver)
    }

    override fun onDetach() {
        super.onDetach()
        mainActivity.contentResolver.unregisterContentObserver(contentObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        actionMode?.let {
            actionMode!!.finish()
        }
    }
}