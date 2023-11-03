package com.vaibhav.gallery

import android.content.pm.ActivityInfo
import android.content.res.Configuration

import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player.Listener
import com.google.android.exoplayer2.Tracks
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.video.VideoSize
import com.vaibhav.gallery.databinding.ActivityVideoViewBinding
import com.vaibhav.gallery.datastore.DataStoreManager
import com.vaibhav.gallery.model.VideoViewModel
import com.vaibhav.gallery.collectiondb.store.tool.IntentConstant

class VideoViewActivity : AppCompatActivity() ,Listener{

    lateinit var binding:ActivityVideoViewBinding

    lateinit var player: ExoPlayer

    val TAG=VideoViewActivity::class.java.simpleName

    fun init(){
        player= ExoPlayer.Builder(this).build()
        player.addListener(this)
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }


    override fun onRestart() {
        super.onRestart()
        player.play()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityVideoViewBinding.inflate(layoutInflater)


        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setMargin()
        init()
        val path=intent.getStringExtra("path")
        val pos=intent.getIntExtra("pos",0)
        val model=ViewModelProvider(this)[VideoViewModel::class.java]

        if (intent.action== IntentConstant.ACTION_VIDEO_ALL) {
            val order=intent.getStringExtra("o")?:DataStoreManager.V_LATEST
            model.getLiveData().observe(this) {
                val list = mutableListOf<MediaItem>()
                for (i in it) {
                    list.add(createMediaItem(i.path))
                }
                player.setMediaItems(list, pos, 0)
                player.prepare()
                player.play()
            }
            model.getVideos(this,order)
        }else if(intent.action== IntentConstant.ACTION_VIDEO_SINGLE){
            player.setMediaItem(createMediaItem(path!!))
            player.prepare()
            player.play()
        }


        binding.playerView.player=player

        findViewById<ImageButton>(R.id.back).setOnClickListener {
            finish()
        }
        findViewById<ImageButton>(R.id.rotate).setOnClickListener {
            when(resources.configuration.orientation){
                Configuration.ORIENTATION_PORTRAIT->{
                    requestedOrientation=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
                Configuration.ORIENTATION_LANDSCAPE->{
                    requestedOrientation=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            }
        }
        findViewById<ImageButton>(R.id.resize).setOnClickListener {
            when(binding.playerView.resizeMode){
                AspectRatioFrameLayout.RESIZE_MODE_FIT->{
                    binding.playerView.resizeMode=AspectRatioFrameLayout.RESIZE_MODE_FILL
                    Toast.makeText(this,"Stretch To Screen",Toast.LENGTH_SHORT).show()
                }
                AspectRatioFrameLayout.RESIZE_MODE_FILL->{
                    binding.playerView.resizeMode=AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    Toast.makeText(this,"Crop To Screen",Toast.LENGTH_SHORT).show()
                }
                AspectRatioFrameLayout.RESIZE_MODE_ZOOM->{
                    binding.playerView.resizeMode=AspectRatioFrameLayout.RESIZE_MODE_FIT
                    Toast.makeText(this,"Fit To Screen",Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.playerView.setControllerVisibilityListener(StyledPlayerView.ControllerVisibilityListener {
            if (it==View.GONE){
            hide()
            }

        })

        val audioManager=getSystemService(AUDIO_SERVICE) as AudioManager
        val audioAttributes= AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()
        player.setAudioAttributes(audioAttributes,true)


    }

    private fun createMediaItem(path: String): MediaItem {
        val uri = Uri.parse(path)
        return MediaItem.fromUri(uri)
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    private fun setMargin(){
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.controllerRoot)) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val mlp = view.layoutParams as ViewGroup.MarginLayoutParams
            mlp.bottomMargin = insets.bottom
            mlp.topMargin=insets.top
            view.layoutParams = mlp
            WindowInsetsCompat.CONSUMED
        }
    }



    override fun onTracksChanged(tracks: Tracks) {

    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        Log.d(TAG,mediaMetadata.displayTitle.toString())
    }

    override fun onVideoSizeChanged(videoSize: VideoSize) {
        Log.d(TAG,"${videoSize.height}  x ${videoSize.width}")
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {

        Log.d(TAG,"${mediaItem!!.mediaMetadata.title} ${mediaItem.toString()}")
    }



    override fun onMetadata(metadata: Metadata) {
        Log.d(TAG,metadata.toString())
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


    }

    private fun hide() {

        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

    }


}