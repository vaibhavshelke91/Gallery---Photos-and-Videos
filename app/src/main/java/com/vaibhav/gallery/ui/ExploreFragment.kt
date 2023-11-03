package com.vaibhav.gallery.ui


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vaibhav.gallery.MainActivity
import com.vaibhav.gallery.R
import com.vaibhav.gallery.collectiondb.CollectionAdapter
import com.vaibhav.gallery.collectiondb.collection.Collection
import com.vaibhav.gallery.collectiondb.collection.CollectionViewModel
import com.vaibhav.gallery.collectiondb.store.StoreViewModel

import com.vaibhav.gallery.databinding.FragmentExploreBinding
import com.vaibhav.gallery.db.LikedViewModel
import com.vaibhav.gallery.model.ImageModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ExploreFragment : Fragment() {

    lateinit var binding: FragmentExploreBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentExploreBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity=activity as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter=CollectionAdapter()
        binding.mRecycle.layoutManager=GridLayoutManager(requireContext(),2)
        binding.mRecycle.adapter=adapter

        val model=ViewModelProvider(this)[LikedViewModel::class.java]
        val collectionModel=ViewModelProvider(this)[CollectionViewModel::class.java]
        val storeModel=ViewModelProvider(this)[StoreViewModel::class.java]

        model.getLiveData().observe(viewLifecycleOwner){

        }

        collectionModel.getLiveData().observe(viewLifecycleOwner){

            CoroutineScope(Dispatchers.Main).launch {
                for (i in it){
                    storeModel.getLiveData(i.id).observe(viewLifecycleOwner){
                        val l=ArrayList<ImageModel>()
                        for (j in it){
                            l.add(ImageModel((j.path)))
                        }
                        adapter.map.put(i.name!!,l)
                        adapter.update()
                    }
                }
            }


        }

        binding.newCollection.setOnClickListener {
            val inflater=LayoutInflater.from(requireContext())
            val view=inflater.inflate(R.layout.dialog_edittext,null)
            val editText=view.findViewById<EditText>(R.id.edittext)
                    val builder=MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Create New Collection")

                        .setView(view)
                        .setPositiveButton("Create"){
                            d,i->
                            if (editText.text.toString().isNotBlank()){
                                collectionModel.insert(Collection(editText.text.toString()))
                            }
                        }
                        .setNegativeButton("Cancel"){d,i->
                            d.dismiss()
                        }
                        .create()

           builder.show()

        }

    }
}