package com.vaibhav.gallery.ui

import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.vaibhav.gallery.MainActivity
import com.vaibhav.gallery.OpenAlbumActivity
import com.vaibhav.gallery.R
import com.vaibhav.gallery.adapter.AlbumRecycleAdapter
import com.vaibhav.gallery.databinding.FragmentAlbumBinding
import com.vaibhav.gallery.datastore.DataStoreManager
import com.vaibhav.gallery.model.AlbumViewModel
import com.vaibhav.gallery.collectiondb.store.tool.NestedScrollCoordinatorLayout
import com.vaibhav.gallery.collectiondb.store.tool.TopMargin


class AlbumFragment : Fragment() {

    lateinit var binding: FragmentAlbumBinding
    lateinit var mainActivity: MainActivity
    lateinit var model:AlbumViewModel
    var order= DataStoreManager.LATEST


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model=ViewModelProvider(this)[AlbumViewModel::class.java]
        readOrder()
        if (savedInstanceState==null){
            model.getAllImages(requireContext(),order)
        }

    }

    private fun readOrder(){
        order= activity?.getSharedPreferences(DataStoreManager.STORE_ALBUM,Context.MODE_PRIVATE)
            ?.getString(DataStoreManager.KEY,DataStoreManager.LATEST) ?:DataStoreManager.LATEST
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("new","old")
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
        binding= FragmentAlbumBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mCoordinate.setPassMode(NestedScrollCoordinatorLayout.PASS_MODE_PARENT_FIRST)
        val adapter=AlbumRecycleAdapter(requireContext())
        binding.photoRecycle.layoutManager=GridLayoutManager(requireContext(),2)
        binding.photoRecycle.adapter=adapter
        binding.photoRecycle.addItemDecoration(TopMargin(15))
        model.getAlbumList().observe(viewLifecycleOwner){
            binding.progressIndiactor.visibility=View.GONE
            if (it.isEmpty()){
                binding.lottieView.visibility=View.VISIBLE
                binding.nothing.visibility=View.VISIBLE
            }else{
                binding.lottieView.visibility=View.GONE
                binding.nothing.visibility=View.GONE
            }
           adapter.setModels(it)
            Log.d("Album","Observing...")
            adapter.setListeners { view, id ,name->
                val intent=Intent(requireContext(),OpenAlbumActivity::class.java)
                intent.putExtra("id",id)
                intent.putExtra("name",name)
                startActivity(intent)
            }
            binding.count.text="${it.size} ${resources.getString(R.string.albums)}"

        }
        check(order)
        binding.sort.setOnClickListener {
            val sheet=AlbumSortSheet()

            sheet.show(childFragmentManager,"SORT_ALBUM")
            sheet.lifecycle.addObserver(LifecycleEventObserver { source, event ->
                if (event== Lifecycle.Event.ON_DESTROY){
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




}