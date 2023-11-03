package com.vaibhav.gallery.editor

import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.skydoves.colorpickerview.listeners.ColorPickerViewListener
import com.vaibhav.gallery.databinding.ActivityDrawBinding
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.SaveSettings
import ja.burhanrashid52.photoeditor.shape.Shape
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder
import ja.burhanrashid52.photoeditor.shape.ShapeType
import kotlinx.coroutines.launch


class DrawActivity : AppCompatActivity() {
    lateinit var binding:ActivityDrawBinding
    var color=Color.BLACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDrawBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch{
            Glide.with(this@DrawActivity)
                .load(Static.bitmap)
                .into(binding.editorView.source)
        }

      val  editor = PhotoEditor.Builder(this, binding.editorView)
            .setPinchTextScalable(true)
            .setClipSourceImage(true)
            .build()
        editor.setBrushDrawingMode(true)
        editor.setShape(getShape())



        binding.redo.setOnClickListener {
            editor.redo()
        }
        binding.undo.setOnClickListener {
            editor.undo()
        }
        binding.close.setOnClickListener {
            finish()
        }
        binding.colorPicker.setOnClickListener {
           MaterialColorPickerDialog.Builder(this)
               .attachAlphaSlideBar(false)
               .setPositiveButton("Select",object :ColorEnvelopeListener{
                   override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                      envelope?.let { this@DrawActivity.color=envelope.color }
                       editor.setShape(getShape())
                       binding.colorPicker.setBackgroundColor(color)
                   }

               })
               .setNegativeButton("Cancel"){d,i->
                   d.dismiss()
               }.create().show()
        }

        binding.opaSlider.addOnChangeListener { slider, value, fromUser ->
            editor.setShape(getShape())
        }
        binding.brushSlider.addOnChangeListener { slider, value, fromUser ->
            editor.setShape(getShape())
        }
        binding.save.setOnClickListener {
           val saveSettings= SaveSettings.Builder()
               .setCompressFormat(Bitmap.CompressFormat.JPEG)
               .setCompressQuality(100)
               .build()
            lifecycleScope.launch {
                Static.bitmap=editor.saveAsBitmap(saveSettings)
                finish()
            }
        }
    }

    private fun getShape():ShapeBuilder{
        return ShapeBuilder()
            .withShapeColor(color)

            .withShapeOpacity(binding.opaSlider.value.toInt())
            .withShapeSize(binding.brushSlider.value)
    }
}