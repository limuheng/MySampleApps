package com.hank.cameratestapp.camera2

import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.hank.cameratestapp.utils.PermissionUtils
import android.view.WindowManager
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.os.Build
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.widget.*
import com.hank.cameratestapp.R
import com.hank.cameratestapp.utils.AutoFitTextureView
import com.hank.cameratestapp.utils.CameraIndicatorView
import com.hank.cameratestapp.utils.CompareSizesByArea


private const val CODE_REQUEST_PERMISSION = 1001

class Camera2Activity : AppCompatActivity() {

    private val TAG = Camera2Activity::class.java.simpleName

    private var captureBtn: ImageButton? = null
    private var evTv: TextView? = null
    private var seekbar: SeekBar? = null

    private var textureView: AutoFitTextureView? = null

    private lateinit var permissionUtils: PermissionUtils

    private lateinit var cameraManager: ICamera2Manager
    private lateinit var cameraPresenter: ICamera2Presenter

    private val seekbarChangedListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {

        }

        override fun onStartTrackingTouch(sb: SeekBar?) { }

        override fun onStopTrackingTouch(sb: SeekBar?) { }
    }

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture?, width: Int, height: Int) { }

        override fun onSurfaceTextureUpdated(texture: SurfaceTexture?) { }

        override fun onSurfaceTextureDestroyed(texture: SurfaceTexture?): Boolean {
            return true
        }

        override fun onSurfaceTextureAvailable(texture: SurfaceTexture?, width: Int, height: Int) {
            openCamera()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera2)

        translucentStatusBar()

        captureBtn = findViewById(R.id.capture_photo)
        captureBtn?.setOnClickListener {

        }

        textureView = findViewById(R.id.texture_view)

        evTv = findViewById(R.id.ev_value)
        evTv?.text = getString(R.string.ev_display, "0")
        seekbar = findViewById(R.id.ev_adjust_bar)
        seekbar?.isEnabled = false

        cameraManager = Camera2Manager(this)
        cameraPresenter = Camera2Presenter(cameraManager)
    }

    override fun onResume() {
        super.onResume()

        permissionUtils = PermissionUtils.getInstance(applicationContext)
        val missingPermissions = permissionUtils.checkPermissions(permissionUtils.getRequiredPermissions())
        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this,
                permissionUtils.getRequiredPermissions(), CODE_REQUEST_PERMISSION
            )
        } else {
            if (textureView?.isAvailable == true) {
                openCamera()
            } else {
                textureView?.surfaceTextureListener = surfaceTextureListener
            }
//            Log.d(TAG, "NumberOfCameras=${cameraManager?.getNumberOfCameras()}")
//            Log.d(TAG, "BackFacingCameraId=${cameraManager?.getBackFacingCameraId()}")
//            val stringBuilder = StringBuilder()
//            stringBuilder.append("NumberOfCameras=${cameraManager?.getNumberOfCameras()}\n")
//                .append("BackFacingCameraId=${cameraManager?.getBackFacingCameraId()}\n")
//            evTv?.text = stringBuilder.toString()
        }
    }

    override fun onPause() {
        cameraPresenter.closeCamera()
        super.onPause()
    }

    private fun openCamera() {
        Log.d(TAG, "openCamera")
        cameraPresenter.openCamera { camera, throwable ->
            when {
                throwable != null -> {
                    Log.e(TAG, "openCamera callback : throwable=$throwable")
                }
                camera != null -> {
                    runOnUiThread {
                        val indicatorView: CameraIndicatorView = findViewById(R.id.indicator_view)
                        cameraPresenter.setCameraIndicatorView(indicatorView)

                        val sizes = cameraPresenter.getOutputSize(ImageFormat.JPEG)
                        val previewSize = CompareSizesByArea.getSmallestSize(sizes)
                        cameraPresenter.setPreviewSize(previewSize)

                        textureView?.setTouchListener(object : AutoFitTextureView.TouchListener {
                            override fun onTouch(point: Point, parent: Rect) {
                                cameraPresenter.focusOnTouch(point, parent)
                            }
                        })
                        //textureView?.setAspectRatio(previewSize.width, previewSize.height)
                        //textureView?.surfaceTexture?.setDefaultBufferSize(previewSize.width, previewSize.height)

                        cameraPresenter.setPreviewSurface(Surface(textureView?.surfaceTexture))

                        cameraPresenter.startCaptureSession()
                    }
                }
                else -> {
                    Log.e(TAG, "openCamera callback : camera=$camera, throwable=$throwable")
                }
            }
        }
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
