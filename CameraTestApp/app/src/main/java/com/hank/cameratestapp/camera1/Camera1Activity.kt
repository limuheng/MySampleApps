package com.hank.cameratestapp.camera1

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.hank.cameratestapp.utils.PermissionUtils
import android.view.WindowManager
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.os.Build
import android.util.Log
import android.widget.*
import com.hank.cameratestapp.R
import com.hank.cameratestapp.utils.CameraIndicatorView


private const val CODE_REQUEST_PERMISSION = 1001

class Camera1Activity : AppCompatActivity() {

    private val TAG = Camera1Activity::class.java.simpleName

    private var captureBtn: ImageButton? = null
    private var evTv: TextView? = null
    private var seekbar: SeekBar? = null

    private var preview: CameraPreview? = null

    private var cameraManager: ICameraManager = CameraManager(this)
    private val cameraPresenter: ICameraPresenter = CameraPresenter(cameraManager)

    private lateinit var permissionUtils: PermissionUtils

    private val seekbarChangedListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
            val max = sb?.max ?: 0
            val evCompensation = progress - (max / 2)
            cameraPresenter.takeIf { it.isReady() }?.setExposureCompensation(evCompensation)
            evTv?.text = getString(R.string.ev_display, evCompensation.toString())
        }

        override fun onStartTrackingTouch(sb: SeekBar?) { }

        override fun onStopTrackingTouch(sb: SeekBar?) { }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera1)

        translucentStatusBar()

        captureBtn = findViewById(R.id.capture_photo)
        captureBtn?.setOnClickListener {
            cameraPresenter.takeIf {
                it.isReady()
            }?.takePicture()
        }

        evTv = findViewById(R.id.ev_value)
        evTv?.text = getString(R.string.ev_display, "0")
        seekbar = findViewById(R.id.ev_adjust_bar)
        seekbar?.isEnabled = false
    }

    override fun onResume() {
        super.onResume()

        permissionUtils = PermissionUtils.getInstance(applicationContext)
        val missingPermissions = permissionUtils.checkPermissions(permissionUtils.getRequiredPermissions())
        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this,
                permissionUtils.getRequiredPermissions(), CODE_REQUEST_PERMISSION)
        } else {
            cameraPresenter.openCamera { camera, throwable ->
                preview = camera?.let {
                    CameraPreview(this, cameraPresenter)
                }

                // Set the Preview view as the content of our activity.
                preview?.also {
                    val previewContainer: FrameLayout = findViewById(R.id.preview_container)
                    val indicatorView: CameraIndicatorView = findViewById(R.id.indicator_view)
                    cameraPresenter.setCameraIndicatorView(indicatorView)
                    previewContainer.addView(it)
                } ?: run {
                    Toast.makeText(this, throwable?.message, Toast.LENGTH_SHORT).show()
                }

                // Init exposure steps bar
                seekbar?.max = cameraPresenter.getEvSteps()
                seekbar?.progress = seekbar?.max!! / 2
                seekbar?.setOnSeekBarChangeListener(seekbarChangedListener)
                seekbar?.isEnabled = true
                Log.d("MainActivity", "seekbar?.max=${seekbar?.max}, seekbar?.progress=${seekbar?.progress}")
            }
        }
    }

    override fun onPause() {
        cameraPresenter.closeCamera()
        super.onPause()
    }

    private fun translucentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 4.4
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 5.0
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) // 確認取消半透明設置。
            window.decorView.systemUiVisibility =
                // SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN: 全螢幕顯示，status bar 不隱藏，activity 上方 layout 會被 status bar 覆蓋。
                // SYSTEM_UI_FLAG_LAYOUT_STABLE: 配合其他 flag 使用，防止 system bar 改變後 layout 的變動。
                (SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS) // 跟系統表示要渲染 system bar 背景。
            window.statusBarColor = Color.TRANSPARENT
        }
    }
}
