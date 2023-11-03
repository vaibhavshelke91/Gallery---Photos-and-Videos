package com.vaibhav.gallery.ui

import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.elevation.SurfaceColors
import com.vaibhav.gallery.MainActivity
import com.vaibhav.gallery.databinding.BottomSheetSortBinding
import com.vaibhav.gallery.datastore.DataStoreManager

class VideoSortSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetSortBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var mainActivity: MainActivity
    var order= DataStoreManager.LATEST
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity=activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = BottomSheetSortBinding.inflate(inflater, container, false)
        return binding.root

    }

    private fun check(string: String){
        val r = 90f // the border radius in pixel
        val shape = ShapeDrawable(RoundRectShape(floatArrayOf(r, r, r, r, r, r, r, r), null, null))
        shape.paint.color = SurfaceColors.SURFACE_5.getColor(requireContext())

        when(string){
            DataStoreManager.V_NAME->{binding.name.background=shape}
            DataStoreManager.V_NAME_REVERSE->{binding.nameR.background=shape}
            DataStoreManager.V_LATEST->{binding.latest.background=shape}
            DataStoreManager.V_OLDEST->{binding.oldest.background=shape}
            DataStoreManager.V_SIZE->{binding.size.background=shape}
            DataStoreManager.V_SIZE_REVERSE->{binding.sizeR.background=shape}
        }
    }
    private fun readOrder(){
        order= activity?.getSharedPreferences(DataStoreManager.STORE_VIDEO, Context.MODE_PRIVATE)
            ?.getString(DataStoreManager.KEY, DataStoreManager.V_LATEST) ?: DataStoreManager.V_LATEST
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val pref=activity?.getSharedPreferences(DataStoreManager.STORE_VIDEO, Context.MODE_PRIVATE)?.edit()
        readOrder()
        check(order)

        binding.name.setOnClickListener {
            pref?.apply {
                putString(DataStoreManager.KEY, DataStoreManager.V_NAME)
                commit()
            }
            dismiss()
        }
        binding.nameR.setOnClickListener {
            pref?.apply {
                putString(DataStoreManager.KEY, DataStoreManager.V_NAME_REVERSE)
                commit()
            }
            dismiss()
        }
        binding.latest.setOnClickListener{
            pref?.apply {
                putString(DataStoreManager.KEY, DataStoreManager.V_LATEST)
                commit()
            }
            dismiss()
        }
        binding.oldest.setOnClickListener {
            pref?.apply {
                putString(DataStoreManager.KEY, DataStoreManager.V_OLDEST)
                commit()
            }
            dismiss()
        }
        binding.size.setOnClickListener {
            pref?.apply {
                putString(DataStoreManager.KEY, DataStoreManager.V_SIZE)
                commit()
            }
            dismiss()
        }
        binding.sizeR.setOnClickListener {
            pref?.apply {
                putString(DataStoreManager.KEY, DataStoreManager.V_SIZE_REVERSE)
                commit()
            }
            dismiss()
        }



    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}