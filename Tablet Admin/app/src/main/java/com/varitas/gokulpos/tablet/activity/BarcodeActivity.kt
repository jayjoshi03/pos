package com.varitas.gokulpos.tablet.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.view.SurfaceHolder
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.varitas.gokulpos.tablet.R
import com.varitas.gokulpos.tablet.databinding.ActivityBarcodeBinding
import com.varitas.gokulpos.tablet.response.ScanBarcode
import com.varitas.gokulpos.tablet.utilities.Default
import com.varitas.gokulpos.tablet.utilities.Utils
import com.varitas.gokulpos.tablet.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException


@AndroidEntryPoint class BarcodeActivity : BaseActivity() {

    private lateinit var binding : ActivityBarcodeBinding
    private lateinit var barcodeDetector : BarcodeDetector
    private lateinit var cameraSource : CameraSource
    private val requestCameraPermission = 201
    private val viewModel : ProductViewModel by viewModels()
    private var intentData = ""
    private lateinit var scanDetails : ScanBarcode
    private var parentId = 0
    private var isFromOrder = false

    companion object {
        lateinit var Instance : BarcodeActivity
    }

    init {
        Instance = this
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.layoutToolbar.toolbarCommon)
        initData()
        postInitView()
        loadData()
        manageClicks()
    }

    //region To init data
    private fun initData() {
        binding.apply {
            binding.layoutToolbar.textViewToolbarName.text = resources.getString(R.string.lbl_ScanBarcode)
            binding.layoutToolbar.imageViewAction.setImageDrawable(ContextCompat.getDrawable(this@BarcodeActivity, R.drawable.ic_home))
            binding.layoutToolbar.imageViewBack.visibility = View.VISIBLE
        }
        parentId = if (intent.extras?.getInt(Default.PARENT_ID) != null) intent.extras?.getInt(Default.PARENT_ID)!! else 0
        isFromOrder = if (intent.extras?.getBoolean(Default.ORDER) != null) intent.extras?.getBoolean(Default.ORDER)!! else false
        scanDetails = ScanBarcode()
        barcodeDetector = BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()
        cameraSource = CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(1920, 1080).setAutoFocusEnabled(true) //you should add this feature
            .build()
    }

    private fun postInitView() {
        binding.apply {

        }
    } //endregion

    //region To manage click events
    private fun manageClicks() {
        binding.apply {
            layoutToolbar.imageViewAction.clickWithDebounce {
                moveToOrder(parentId)
            }
            layoutToolbar.imageViewBack.clickWithDebounce {
                val intent = Intent(this@BarcodeActivity, AddProductActivity::class.java)
                intent.putExtra(Default.PRODUCT, ScanBarcode())
                intent.putExtra(Default.PARENT_ID, parentId)
                intent.putExtra(Default.ORDER, isFromOrder)
                openActivity(intent)
            }
            imageViewSearch.clickWithDebounce {
                if(!TextUtils.isEmpty(intentData)) {
                    cameraSource.stop()
                    viewModel.getBarcodeDetails(intentData).observe(this@BarcodeActivity) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if(it != null) {
                                scanDetails = it
                                binding.apply {
                                    textInputName.setText(scanDetails.name)
                                    textInputDepartment.setText(scanDetails.sDepartment)
                                    textInputCategory.setText(scanDetails.sCategory)
                                }
                            } else scanDetails = ScanBarcode()
                        }
                    }
                }
            }

            imageViewReset.clickWithDebounce {
                cameraSource.start(binding.surfaceView.holder)
                scanDetails = ScanBarcode()
                binding.apply {
                    textInputBarcode.setText("")
                    textInputName.setText("")
                    textInputDepartment.setText("")
                    textInputCategory.setText("")
                }
            }

            buttonFetch.clickWithDebounce {
                if(!TextUtils.isEmpty(scanDetails.name)) {
                    val intent = Intent(this@BarcodeActivity, AddProductActivity::class.java)
                    intent.putExtra(Default.PRODUCT, scanDetails)
                    intent.putExtra(Default.PARENT_ID, parentId)
                    intent.putExtra(Default.ORDER, isFromOrder)
                    openActivity(intent)
                }
            }
        }
    } //endregion

    @Deprecated("Deprecated in Java") override fun onBackPressed() {
        val intent = Intent(this@BarcodeActivity, AddProductActivity::class.java)
        intent.putExtra(Default.PRODUCT, ScanBarcode())
        intent.putExtra(Default.PARENT_ID, parentId)
        intent.putExtra(Default.ORDER, isFromOrder)
        openActivity(intent)
        onBackPressedDispatcher.onBackPressed()
    }

    //region To load data
    private fun loadData() {
        viewModel.showProgress.observe(this) {
            manageProgress(it)
        }

        viewModel.errorMsg.observe(this) {
            if(!TextUtils.isEmpty(it)) {
                try {
                    showSweetDialog(this@BarcodeActivity, it)
                } catch(e : Exception) {
                    Utils.printAndWriteException(e)
                }
            }
        }
    } //endregion

    private fun initialiseDetectorsAndSources() {
        binding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder : SurfaceHolder) {
                try {
                    if(ActivityCompat.checkSelfPermission(this@BarcodeActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(binding.surfaceView.holder)
                    } else {
                        ActivityCompat.requestPermissions(this@BarcodeActivity, arrayOf(Manifest.permission.CAMERA), requestCameraPermission)
                    }
                } catch(e : IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(holder : SurfaceHolder, format : Int, width : Int, height : Int) {}
            override fun surfaceDestroyed(holder : SurfaceHolder) {
                cameraSource.stop()
            }
        })

        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {

            }

            override fun receiveDetections(detections : Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if(barcodes.size() != 0) {
                    binding.textInputBarcode.post {
                        intentData = if(barcodes.valueAt(0).email != null) {
                            binding.textInputBarcode.removeCallbacks(null)
                            barcodes.valueAt(0).email.address
                        } else barcodes.valueAt(0).displayValue
                        binding.textInputBarcode.setText(intentData)
                    }
                }
            }
        })

    }

    override fun onPause() {
        super.onPause()
        cameraSource.release()
    }

    override fun onResume() {
        super.onResume()
        initialiseDetectorsAndSources()
    }
}