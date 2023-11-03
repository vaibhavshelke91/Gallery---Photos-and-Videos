package com.vaibhav.gallery

import android.app.WallpaperManager
import android.content.DialogInterface
import android.content.Intent
import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.Window
import android.widget.NumberPicker
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.print.PrintHelper
import androidx.viewpager2.widget.ViewPager2
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vaibhav.gallery.BuildConfig.APPLICATION_ID
import com.vaibhav.gallery.adapter.SlideRecycleAdapter
import com.vaibhav.gallery.collectiondb.collection.CollectionViewModel
import com.vaibhav.gallery.collectiondb.store.Store
import com.vaibhav.gallery.collectiondb.store.StoreViewModel
import com.vaibhav.gallery.databinding.ActivityFullScreenBinding
import com.vaibhav.gallery.datastore.DataStoreManager
import com.vaibhav.gallery.db.LikedViewModel
import com.vaibhav.gallery.model.AlbumSelectionModel
import com.vaibhav.gallery.model.ImageModel
import com.vaibhav.gallery.model.ImageViewModel
import com.vaibhav.gallery.search.SearchModel
import com.vaibhav.gallery.collectiondb.store.tool.IntentConstant
import com.vaibhav.gallery.transformer.DepthPageTransformer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


class FullScreenActivity : AppCompatActivity() ,SlideRecycleAdapter.OnImageLoadListeners{
    lateinit var binding: ActivityFullScreenBinding
    var isSlideShowEnabled=false
    var pos=0;
    var cPath=""
    var orientation=0
    var isImmersiveScreen=true
    var list= ArrayList<ImageModel>()
    var isLiked=false
    var isSlideShow=false
    lateinit var model:LikedViewModel
    lateinit var allModel: ImageViewModel
    lateinit var albumSelectionModel: AlbumSelectionModel
    lateinit var searchModel:SearchModel
    lateinit var contentObserver: ContentObserver
    lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    lateinit var adapter: SlideRecycleAdapter



    override fun onCreate(savedInstanceState: Bundle?) {

        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())

        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 300L
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 250L
        }

        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding=ActivityFullScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setMargin()

        model=ViewModelProvider(this)[LikedViewModel::class.java]
        adapter=SlideRecycleAdapter(this)
        binding.mPage.adapter=adapter
        setDate()
        binding.mPage.setPageTransformer(DepthPageTransformer())
        val intent=intent
        pos = intent.getIntExtra("p", 0)
        cPath= intent.getStringExtra("cp")!!
        orientation=intent.getIntExtra("or",0)

        binding.fullImageView.setImage(ImageSource.uri(cPath))
        binding.fullImageView.orientation=orientation

        // fixed bug on android 11 with HEIC Images
        if (Build.VERSION.SDK_INT==Build.VERSION_CODES.R){
            SubsamplingScaleImageView.setPreferredBitmapConfig(Bitmap.Config.ARGB_8888)
        }


        if (intent.action== IntentConstant.ACTION_COLLECTION) {
            list=getList(intent.getStringExtra("d")!!)
            adapter.setImageModels(list)
            initView(pos,cPath,list)


        }else if (intent.action== IntentConstant.ACTION_ALL){
             allModel=ViewModelProvider(this)[ImageViewModel::class.java]
            val order=intent.getStringExtra("o")
            allModel.getAudioList().observe(this){
                list= it
                adapter.setImageModels(list)
                initView(pos,cPath,it)
            }
            allModel.getAllImages(this,order?:DataStoreManager.LATEST)

        }else if (intent.action== IntentConstant.ACTION_ALBUM){
            val id=intent.getIntExtra("id",0)
            albumSelectionModel=ViewModelProvider(this)[AlbumSelectionModel::class.java]
            albumSelectionModel.getAlbumList().observe(this){
                list=it
                adapter.setImageModels(list)
                initView(pos,cPath,it)
            }
            albumSelectionModel.getAllImages(this,id)
        }else if(intent.action== IntentConstant.ACTION_LIKED){
            model=ViewModelProvider(this)[LikedViewModel::class.java]
            model.getLiveData().observe(this){
                list= it as ArrayList<ImageModel>
                adapter.setImageModels(list)
                initView(pos,cPath,list)
            }
        }else if (intent.action== IntentConstant.ACTION_SEARCH){
            val q=intent.getStringExtra("q")
            searchModel=ViewModelProvider(this)[SearchModel::class.java]
            searchModel.getLiveData().observe(this){
                list=it
                adapter.setImageModels(list)
                initView(pos,cPath,it)
            }
            searchModel.query(this,q!!)
        }

        binding.back.setOnClickListener {
            finish()
        }

        binding.like.setOnClickListener {
            if (list.isNotEmpty()) {
                if (!isLiked) {
                    model.insert(list[getCurrentPosition()])

                } else {
                    model.remove(list[getCurrentPosition()])
                }
                model.getLiveData().observe(this) {
                    handleLikeEvent()
                }
            }
        }

        binding.edit.setOnClickListener {
            if (list.isNotEmpty()){
                val intent=Intent(this,EditorActivity::class.java)
                intent.putExtra("p",list[getCurrentPosition()].path)
                startActivity(intent)
            }
        }


        binding.share.setOnClickListener {
            if (list.isNotEmpty()){
                val shareList=ArrayList<String>()
                shareList.add(list[getCurrentPosition()].path)
                startFileShareIntent(shareList)
            }

        }


        binding.slideShow.setOnClickListener {
                        makeSlideShow()
        }
        binding.info.setOnClickListener {
            if (list.isNotEmpty()){
                val name=list[getCurrentPosition()].name
                val path=list[getCurrentPosition()].path
                val time=getDateWithTime(list[getCurrentPosition()].date*1000)
                val res="${list[getCurrentPosition()].height} x ${list[getCurrentPosition()].width}"
                val size=formatSize(list[getCurrentPosition()].size)

                MaterialAlertDialogBuilder(this)
                    .setTitle("${resources.getString(R.string.details)}")
                    .setMessage(
                        "${resources.getString(R.string.name)} : $name \n \n" +
                                "${resources.getString(R.string.path)} : $path \n \n"+
                                "${resources.getString(R.string.timestamp)} : $time \n \n"+
                                "${resources.getString(R.string.resolution)} : $res \n \n"+
                                "${resources.getString(R.string.size)} : $size")

                    .setPositiveButton("${resources.getString(R.string.close)}"){i,j->
                        i.dismiss()
                    }.create().show()
            }
        }
        initContentObserver()
        intentSenderLauncher=registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()){
            if (it.resultCode== RESULT_OK){
                val i=binding.mPage.currentItem
                list.removeAt(i)
                adapter.notifyItemRemoved(i)
            }else{
                Log.d("Delete","Something Error")
            }
        }


        binding.more.setOnClickListener {
            val menu=PopupMenu(this,it)
            menu.menuInflater.inflate(R.menu.fullscreen_menu,menu.menu)
            menu.gravity=Gravity.END
            menu.setOnMenuItemClickListener {item->
            when(item.itemId){
                R.id.setAs->{
                    //setWallpaper()
                    setAs()

                }
                R.id.print->{
                    print()
                }

            }
                return@setOnMenuItemClickListener true
            }
            menu.show()
        }

    }
  private fun collection(){
      if (list.isEmpty()){
          return
      }
      var items= mutableListOf<String>()
      val ids= mutableListOf<Int>()
      val cModel=ViewModelProvider(this)[CollectionViewModel::class.java]
      val sModel=ViewModelProvider(this)[StoreViewModel::class.java]
      cModel.getLiveData().observe(this){
         for (i in it){
            items.add(i.name!!)
             ids.add(i.id)
         }
          MaterialAlertDialogBuilder(this)
              .setTitle("Select Collection")
              .setItems(items.toTypedArray(),
                  DialogInterface.OnClickListener { dialog, which ->
                     sModel.insert(Store(list[getCurrentPosition()].path,ids[which]))
                  })
              .setNegativeButton("Cancel"){
                  d,i->
                  d.dismiss()
              }.create().show()
      }
  }

    private fun makeSlideShow(){
        if (list.isNotEmpty())
        {   var ms=2000
            val sec=resources.getString(R.string.seconds)
            val values= arrayOf("2 $sec","3 $sec","4 $sec","5 $sec","6 $sec","7 $sec","8 $sec","9 $sec","10 $sec")
            val view=layoutInflater.inflate(R.layout.number_picker,null)
           val builder= MaterialAlertDialogBuilder(this)
                .setTitle("${resources.getString(R.string.time_intervals)}")
                .setView(view)
                .setPositiveButton("${resources.getString(R.string.start)}"){d,i->
                    Snackbar.make(binding.root,"${resources.getString(R.string.slide_show_started)}",Snackbar.LENGTH_LONG).show()
                    lifecycleScope.launch{
                        onClick()
                        isSlideShow=true
                        binding.mPage.keepScreenOn=true
                        asyncSlideShow(ms)
                        isSlideShow=false
                        binding.mPage.keepScreenOn=false
                       }

                }
                .setNegativeButton("${resources.getString(R.string.cancel)}"){d,i->
                    d.dismiss()
                }.create()
            val picker=view.findViewById<NumberPicker>(R.id.numberPicker)
            picker!!.minValue=0
            picker!!.maxValue=values.size-1
            picker.displayedValues=values
            picker.setOnValueChangedListener { picker, oldVal, newVal ->
              when(picker.value){
                  0->ms=2000
                  1->ms=3000
                  2->ms=4000
                  3->ms=5000
                  4->ms=6000
                  5->ms=7000
                  6->ms=8000
                  7->ms=9000
                  8->ms=10000
              }
            }

            builder.show()


        }
    }

    private suspend fun asyncSlideShow(time:Int){
        withContext(Dispatchers.Main){
            while (!isImmersiveScreen){

                if (adapter.itemCount>=getCurrentPosition()+1){
                    binding.mPage.setCurrentItem(getCurrentPosition()+1,true)
                }
                delay(time.toLong())
            }
        }

    }

    private fun print(){
        if (list.isNotEmpty()){
            val p=list[getCurrentPosition()].path
            val n=list[getCurrentPosition()].name
            CoroutineScope(Dispatchers.IO).launch {
                val bmp=BitmapFactory.decodeFile(File(p).absolutePath)
                CoroutineScope(Dispatchers.Main).launch {
                    val helper=PrintHelper(this@FullScreenActivity)
                    helper.printBitmap("$n",bmp)
                }
            }
        }
    }
    @Deprecated(message = "No Longer Maintained")
    private fun setWallpaper(){
        if (list.isNotEmpty()){
            val model=list[getCurrentPosition()]
            val item= arrayOf("${resources.getString(R.string.home_screen)}","${resources.getString(R.string.lock_screen)}")
            MaterialAlertDialogBuilder(this)
                .setTitle("${resources.getString(R.string.set_wallpaper)}")
                .setItems(item,DialogInterface.OnClickListener { dialog, which ->
                    when(which){
                        0->{
                           CoroutineScope(Dispatchers.IO).launch {
                               val bitmap=BitmapFactory.decodeFile(File(model.path).absolutePath)
                               WallpaperManager.getInstance(this@FullScreenActivity)
                                   .setBitmap(bitmap,null,true,WallpaperManager.FLAG_SYSTEM)
                               CoroutineScope(Dispatchers.Main).launch {
                                   Snackbar.make(binding.root,"Wallpaper Applied to Home Screen",Snackbar.LENGTH_SHORT).show()
                               }
                           }
                        }
                        1->{
                            CoroutineScope(Dispatchers.IO).launch {
                                val bitmap=BitmapFactory.decodeFile(File(model.path).absolutePath)
                                WallpaperManager.getInstance(this@FullScreenActivity)
                                    .setBitmap(bitmap,null,true,WallpaperManager.FLAG_LOCK)
                                CoroutineScope(Dispatchers.Main).launch {
                                    Snackbar.make(binding.root,"Wallpaper Applied to Lock Screen",Snackbar.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }).setNegativeButton("${resources.getString(R.string.cancel)}") { d, w ->
                    d.dismiss()
                }
                .create().show()


        }
    }
    private fun initContentObserver(){
        contentObserver=object :ContentObserver(null){
            override fun onChange(selfChange: Boolean) {

            }
        }
        contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,true,contentObserver)
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
                val intentSender= MediaStore.createDeleteRequest(contentResolver,uriList).intentSender
                intentSender.let {
                    intentSenderLauncher.launch(
                        it?.let { it1 -> IntentSenderRequest.Builder(it1).build() }
                    )
                }
            }else{
                try {
                    for (uri in uriList){
                        contentResolver.delete(uri,null,null)
                        val i=binding.mPage.currentItem
                        this@FullScreenActivity.list.removeAt(i)
                        CoroutineScope(Dispatchers.Main).launch {
                            adapter.notifyItemRemoved(i)
                        }

                    }
                }catch (e:SecurityException){
                    e.printStackTrace()
                }
            }

        }
    }


    fun formatSize(v: Long): String? {
        if (v < 1024) return "$v B"
        val z = (63 - java.lang.Long.numberOfLeadingZeros(v)) / 10
        return String.format("%.1f %sB", v.toDouble() / (1L shl z * 10), " KMGTPE"[z])
    }

    private fun getDateWithTime(stamp:Long):String{
        val formatter=SimpleDateFormat("dd MMMM yyyy hh:mm:ss")
        return formatter.format(Date(stamp))
    }

    fun startFileShareIntent(itemsList:ArrayList<String>) {
        val uriArrayList: ArrayList<Uri> = ArrayList()
        GlobalScope.launch(Dispatchers.IO) {
            runCatching {
                itemsList!!.forEach {
                    uriArrayList.add(
                        FileProvider.getUriForFile(
                            this@FullScreenActivity,
                            "$APPLICATION_ID.provider",
                            File(it)
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

    private fun setDate(){
        binding.mPage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                pos=position
                if (list.isNotEmpty()){
                 //   binding.dateText.text=list[position].textDate
                    handleLikeEvent()
                }

            }
        })
    }


    private fun getList(s:String):ArrayList<ImageModel>{
        val gson= Gson()
        val type = object : TypeToken<ArrayList<ImageModel?>?>() {}.type
        return gson.fromJson(s,type)

    }

    private fun initView(pos:Int,cp:String,list: ArrayList<ImageModel>,srcoll:Boolean=true){

        //binding.fullImageView.setImage(ImageSource.uri(cp))


     //  binding.mRecycle.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        adapter.setDateTextView(binding.dateText)
        adapter.notifyDataSetChanged()
        binding.mPage.setCurrentItem(pos,false)

        adapter.setListener(this)
        handleLikeEvent()
        binding.delete.setOnClickListener {
            MaterialAlertDialogBuilder(this).setTitle("${resources.getString(R.string.delete)}")
                .setMessage("${resources.getString(R.string.do_you_want_to_delete)}")
                .setPositiveButton("${resources.getString(R.string.yes)}"){ it, _ ->
                    if (list.isNotEmpty()){
                        lifecycleScope.launch{
                            val uriList=ArrayList<ImageModel>()
                            val i=binding.mPage.currentItem
                            uriList.add(list[i])
                            deletePhoto(uriList)



                        }

                    }

                }.setNegativeButton("${resources.getString(R.string.no)}"){it,_->

                }
                .show()
        }
    }

    public fun getCurrentPosition():Int{
        return binding.mPage.currentItem
    }

    private fun handleLikeEvent(){
        if (list.isNotEmpty()){
            binding.dateText.text=list[getCurrentPosition()].textDate
            CoroutineScope(Dispatchers.IO).launch {
                if (model.isLiked(list[getCurrentPosition()])){
                    isLiked=true
                    CoroutineScope(Dispatchers.Main).launch { binding.like.setImageResource(R.drawable.liked) }

                }else{
                    isLiked=false
                    CoroutineScope(Dispatchers.Main).launch { binding.like.setImageResource(R.drawable.like)  }

                }
            }

        }
    }


    private fun show() {
        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        window.decorView.setOnApplyWindowInsetsListener { view, windowInsets ->
            view.onApplyWindowInsets(windowInsets)
        }

       binding.mController.visibility=View.VISIBLE
    }

    private fun hide() {
       binding.mController.visibility=View.GONE
        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

    }

    override fun onZoom() {

    }

    override fun onStable() {

    }

    override fun onClick() {
        isImmersiveScreen = if (isImmersiveScreen){
            hide()
            false
        }else{
            show()
            true
        }
        if (isSlideShow){
            Snackbar.make(binding.root,"${resources.getString(R.string.slide_show_terminated)}",Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onLoad() {
       binding.fullImageView.visibility=View.GONE

    }

    private fun setAs(){
        if(list.isEmpty()) return
        val model=list[getCurrentPosition()]
        val intent = Intent(Intent.ACTION_ATTACH_DATA)
            .apply {
                addCategory(Intent.CATEGORY_DEFAULT)
                setDataAndType(Uri.parse(model.contentUri), "image/*")
                putExtra("mimeType", "image/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        startActivity(Intent.createChooser(intent, "Set as:"))
    }

    private fun setMargin(){
        ViewCompat.setOnApplyWindowInsetsListener(binding.mController) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val mlp = view.layoutParams as MarginLayoutParams
            mlp.bottomMargin = insets.bottom
            mlp.topMargin=insets.top
            view.layoutParams = mlp
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setStatusBarHeight(){
        val statusBarHeightId = resources.getIdentifier("status_bar_height", "dimen", "android")

        val statusBarHeight = resources.getDimensionPixelSize(statusBarHeightId)
        binding.topHeader.setPadding(0,statusBarHeight,0,0)
    }

    private fun setNavBarHeight(){
        val navHeight = resources.getIdentifier("navigation_bar_height", "dimen", "android")

        val navBarHeight = resources.getDimensionPixelSize(navHeight)
        binding.bottomHeader.setPadding(0,0,0,navBarHeight)
    }
}