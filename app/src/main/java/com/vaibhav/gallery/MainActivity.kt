package com.vaibhav.gallery

import android.app.ActivityOptions
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.elevation.SurfaceColors
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.vaibhav.gallery.databinding.ActivityMainBinding
import com.vaibhav.gallery.datastore.StoreOjb
import com.vaibhav.gallery.datastore.getGridStoreLiveData
import com.vaibhav.gallery.datastore.getTitleStateLiveData
import com.vaibhav.gallery.datastore.saveGridStoreState
import com.vaibhav.gallery.datastore.saveTitleState
import com.vaibhav.gallery.model.ImageModel
import com.vaibhav.gallery.model.VideoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    var isInitCarousel=false
    var isInit=false
    lateinit var contentObserver: ContentObserver

    fun initObserver(){
        contentObserver=object :ContentObserver(null){
            override fun onChange(selfChange: Boolean) {

            }
        }
        contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,true,contentObserver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val color=SurfaceColors.SURFACE_2.getColor(this)
        window.navigationBarColor=color
        val controller=Navigation.findNavController(this,R.id.fragmentContainer)
        binding.bottomNav.setupWithNavController(controller)


        /*
        binding.fragmentContainer.offscreenPageLimit=4
        binding.fragmentContainer.adapter=MainPageAdapter(supportFragmentManager,lifecycle)
        binding.fragmentContainer.isUserInputEnabled=false
*/
      /*  binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.photo->{
                    binding.fragmentContainer.setCurrentItem(0,false)
                }
                R.id.album->{
                    binding.fragmentContainer.setCurrentItem(1,false)
                }
                R.id.video->{
                    binding.fragmentContainer.setCurrentItem(3,false)
                }
                R.id.liked->{
                    binding.fragmentContainer.setCurrentItem(2,false)
                }
            }
            return@setOnItemSelectedListener true
        }*/

        binding.searchBar.inflateMenu(R.menu.search_menu)
        binding.searchBar.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.camera->openCamera()
                    R.id.setting->{
                        startActivity(Intent(this,SettingActivity::class.java))
                    }

                }
            return@setOnMenuItemClickListener true
        }

        val callback = onBackPressedDispatcher.addCallback(this,false) {
            binding.drawer.closeDrawer(GravityCompat.START)
        }
        binding.drawer.addDrawerListener(object :DrawerLayout.DrawerListener{
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {
                callback.isEnabled=true
            }

            override fun onDrawerClosed(drawerView: View) {
                callback.isEnabled=false
            }

            override fun onDrawerStateChanged(newState: Int) {

            }

        })

        binding.searchBar.setNavigationOnClickListener {

            binding.drawer.openDrawer(GravityCompat.START)

        }

        binding.navView.setNavigationItemSelectedListener {

            when(it.itemId){

                R.id.about->{startActivity(Intent(this,SettingActivity::class.java))}
                R.id.camera->{openCamera()}
                R.id.triple_grid->{
                    lifecycleScope.launchWhenStarted { saveGridStoreState(3)
                    }
                }
                R.id.double_grid->{
                    lifecycleScope.launchWhenStarted { saveGridStoreState(2) }
                }
                R.id.titles->{
                    val checkBox=it.actionView as CheckBox
                    checkBox.isChecked = !checkBox.isChecked
                    lifecycleScope.launchWhenStarted { saveTitleState(checkBox.isChecked)}

                }
            }
            binding.drawer.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true
        }

        getTitleStateLiveData().observe(this){
            val pref=it[StoreOjb.TITLE_VISIBILITY]
            val menu= binding.navView.menu[0]
            val checkBox=menu.subMenu?.get(2)?.actionView as CheckBox
            pref?.let {

                checkBox.isChecked=pref

            }?: kotlin.run {  checkBox.isChecked=true }
        }

        getGridStoreLiveData().observe(this){
            val pref=it[StoreOjb.ORDER]
            pref?.let {
                when(pref){
                    2->{binding.navView.setCheckedItem(R.id.double_grid)}
                    3->{binding.navView.setCheckedItem(R.id.triple_grid)}
                }
            }?: kotlin.run {binding.navView.setCheckedItem(R.id.double_grid)  }
        }

        intentSenderLauncher=registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()){
            if (it.resultCode== RESULT_OK){
                Snackbar.make(binding.mCoordinate,"${resources.getString(R.string.deleted)}",Snackbar.LENGTH_SHORT).show()
            }else{
                Log.d("Delete","Something Error")
            }
        }

        binding.searchBar.setOnClickListener {
            val option=ActivityOptions.makeSceneTransitionAnimation(this,binding.searchBar,"search")
            startActivity(Intent(this,SearchActivity::class.java),option.toBundle())
        }

    }

    private fun openCamera() {

            try {
                val intent = Intent("android.media.action.IMAGE_CAPTURE")
                startActivity(
                    packageManager.getLaunchIntentForPackage(
                        intent.resolveActivity(packageManager).packageName
                    )
                )
            }
            catch (exc: Exception) {
                // handle exception
                exc.printStackTrace()
                Toast.makeText(this,"Something Went Wrong !",Toast.LENGTH_SHORT).show()
            }

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

    public suspend fun deleteVideos(list: ArrayList<VideoModel>){
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





    override fun onDestroy() {
        super.onDestroy()

    }
}