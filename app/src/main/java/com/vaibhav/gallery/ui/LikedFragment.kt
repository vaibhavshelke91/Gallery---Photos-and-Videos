package com.vaibhav.gallery.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.vaibhav.gallery.FullScreenActivity
import com.vaibhav.gallery.MainActivity
import com.vaibhav.gallery.R
import com.vaibhav.gallery.adapter.DoubleGridAdapter
import com.vaibhav.gallery.databinding.FragmentLikedBinding
import com.vaibhav.gallery.db.LikedViewModel
import com.vaibhav.gallery.model.ImageModel
import com.vaibhav.gallery.collectiondb.store.tool.IntentConstant
import com.vaibhav.gallery.collectiondb.store.tool.NestedScrollCoordinatorLayout
import com.vaibhav.gallery.collectiondb.store.tool.TopMargin


class LikedFragment : Fragment() {

    lateinit var binding:FragmentLikedBinding
    lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity=activity as MainActivity
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentLikedBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mCoordinate.setPassMode(NestedScrollCoordinatorLayout.PASS_MODE_PARENT_FIRST)

        val sManager= StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        sManager.gapStrategy=StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        val adapter=DoubleGridAdapter(this)
        binding.photoRecycle.layoutManager=sManager
        binding.photoRecycle.adapter=adapter
        binding.photoRecycle.addItemDecoration(TopMargin(15))
        val model=ViewModelProvider(this)[LikedViewModel::class.java]
        model.getLiveData().observe(viewLifecycleOwner){
            adapter.list=it as ArrayList
            if (it.isEmpty()){
                binding.lottieView.visibility=View.VISIBLE
                binding.nothing.visibility=View.VISIBLE
            }else{
                binding.lottieView.visibility=View.GONE
                binding.nothing.visibility=View.GONE
            }
            adapter.notifyDataSetChanged()

            binding.count.text="${it.size} ${resources.getString(R.string.liked)}"


        }
        adapter.onItemClick=object :DoubleGridAdapter.OnItemClick{
            override fun onItemClick(view: View, model: ImageModel, position: Int) {
                val intent=Intent(requireContext(),FullScreenActivity::class.java)
                intent.putExtra("p",position)
                intent.putExtra("cp",model.path)
                intent.putExtra("or",model!!.orientation)
                intent.action= IntentConstant.ACTION_LIKED
                startActivity(intent)
            }

            override fun onLongPress(view: View, model: ImageModel, position: Int) {

            }

        }
        adapter.setErrorListeners(object :DoubleGridAdapter.OnErrorListeners{
            override fun onFileNoFound(modelI: ImageModel, position: Int) {
                model.remove(modelI)
            }

        },true)
    }

}