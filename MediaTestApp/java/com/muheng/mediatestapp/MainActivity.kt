package com.muheng.mediatestapp

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Toast
import java.io.File

const val MILLISECONDS = 1000

class MainActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private var mediaPlayer: MediaPlayer? = null

    private var surfaceView: SurfaceView? = null
    private var surfaceHolder: SurfaceHolder? = null

    private var seekBar: SeekBar? = null

    private var btnContainer: View? = null
    private var playBtn: ImageButton? = null
    private var pauseBtn: ImageButton? = null
    private var stopBtn: ImageButton? = null

    enum class PlayState {
        STOPPED,
        STARTED,
        PAUSED
    }

    private var playState = PlayState.STOPPED

    private var updateProgressThread: Thread? = null

    private val playProgressTask = Runnable {
        while(getState() != PlayState.STOPPED) {
            val curPos = mediaPlayer!!.currentPosition / MILLISECONDS

            runOnUiThread {
                seekBar?.progress = curPos
            }

            Thread.sleep(1000)
        }
        runOnUiThread {
            seekBar?.progress = 0
        }
    }

    private val playClickListener = View.OnClickListener {
        setState(PlayState.STARTED)
        mediaPlayer?.start()
        updateProgressThread = Thread(playProgressTask)
        updateProgressThread?.start()
    }

    private val pauseClickListener = View.OnClickListener {
        setState(PlayState.PAUSED)
        mediaPlayer?.pause()
    }

    private val stopClickListener = View.OnClickListener {
        setState(PlayState.STOPPED)
        mediaPlayer?.stop()
        mediaPlayer?.prepareAsync()
    }

    private val preparedListener = MediaPlayer.OnPreparedListener { mp ->
        btnContainer?.visibility = View.VISIBLE
        seekBar?.max = mp.duration / MILLISECONDS
        seekBar?.progress = 0
    }

    private val completionListener = MediaPlayer.OnCompletionListener {
        mediaPlayer?.stop()
        mediaPlayer?.prepareAsync()
    }

    private val errorListener = MediaPlayer.OnErrorListener { mp, what, extra ->
        Toast.makeText(this@MainActivity, "Error : what=$what, extra=$extra", Toast.LENGTH_SHORT).show()
        true
    }

    private val seekbarChangedListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
            mediaPlayer?.let {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress * MILLISECONDS)
                }
            }
        }

        override fun onStartTrackingTouch(sb: SeekBar?) {

        }

        override fun onStopTrackingTouch(sb: SeekBar?) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        surfaceView = findViewById(R.id.surface_view)

        seekBar = findViewById(R.id.seek_bar)
        seekBar?.setOnSeekBarChangeListener(seekbarChangedListener)

        btnContainer = findViewById(R.id.btn_container)
        playBtn = findViewById(R.id.play_btn)
        pauseBtn = findViewById(R.id.pause_btn)
        stopBtn = findViewById(R.id.stop_btn)

        playBtn?.setOnClickListener(playClickListener)
        pauseBtn?.setOnClickListener(pauseClickListener)
        stopBtn?.setOnClickListener(stopClickListener)

        btnContainer?.visibility = View.INVISIBLE

        surfaceHolder = surfaceView?.holder
        surfaceHolder?.addCallback(this)

        mediaPlayer = createMediaPlayer()
        loadMedia()
    }

    override fun onDestroy() {
        surfaceHolder?.removeCallback(this)
        releaseMediaPlayer()
        super.onDestroy()
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder?) {

    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder?) {
        surfaceHolder?.let {
            mediaPlayer?.setDisplay(it)
        }
    }

    private fun setState(state: PlayState) {
        synchronized(playState) {
            if (playState != state) {
                playState = state
            }
        }
    }

    private fun getState(): PlayState {
        synchronized(playState) {
            return playState
        }
    }

    private fun createMediaPlayer(): MediaPlayer {
        val mp = MediaPlayer()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mp.setAudioAttributes(AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(getStreamTypeOrUsage())
                .build())
        } else {
            @Suppress("deprecation")
            mp.setAudioStreamType(getStreamTypeOrUsage())
        }
        mp.setOnPreparedListener(preparedListener)
        mp.setOnCompletionListener(completionListener)
        mp.setOnErrorListener(errorListener)
        return mp
    }

    private fun getStreamTypeOrUsage(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes.USAGE_MEDIA
        } else {
            AudioManager.STREAM_MUSIC
        }
    }

    private fun loadMedia(mediaUri: Uri? = null) {
        val rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(rootDir, "12_us_01.mp4")

        if (file.exists()) {
            mediaPlayer?.setDataSource(this, Uri.fromFile(file))
            mediaPlayer?.prepareAsync()
        } else {
            Toast.makeText(this, "File ${file.name} does not exist!!", Toast.LENGTH_SHORT).show()
        }
    }

    // Release MediaPlayer resources
    private fun releaseMediaPlayer() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
        }

        mediaPlayer?.release()
        mediaPlayer = null
    }
}
