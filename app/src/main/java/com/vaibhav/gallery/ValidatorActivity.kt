package com.vaibhav.gallery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.vaibhav.gallery.databinding.ActivityValidatorBinding

class ValidatorActivity : AppCompatActivity() {

    lateinit var binding: ActivityValidatorBinding
    var isPermissionAllowed=false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen=installSplashScreen()
        super.onCreate(savedInstanceState)
        binding=ActivityValidatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(isPermission()){
            permissionOk()
        }else{
            binding.permissionButton.setOnClickListener {
                if (isPermissionAllowed){
                    permissionOk()
                }else{
                    requestPermission()
                }
            }

        }
    }
    private fun permissionOk(){
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

    private fun isPermission():Boolean{

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ( (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) ==PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_MEDIA_VIDEO) ==PackageManager.PERMISSION_GRANTED))
        } else {
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
        }
    }
    private fun requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            request.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.READ_MEDIA_VIDEO))
        }else{
            request.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }
    }

    private var request=registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (it[Manifest.permission.READ_MEDIA_IMAGES]!! && it[Manifest.permission.READ_MEDIA_VIDEO]!!){
                isPermissionAllowed=true
                binding.permissionButton.text="${resources.getString(R.string.next)}"
            }
            else{
                Toast.makeText(this, "Permission Denied !", Toast.LENGTH_SHORT).show()
            }
        } else if(it[Manifest.permission.WRITE_EXTERNAL_STORAGE]!!){
            isPermissionAllowed=true
            binding.permissionButton.text="Next"

        }
        else{
            Toast.makeText(this, "Permission Denied !", Toast.LENGTH_SHORT).show()
        }
    }
}