package com.skilrock.infinitylotoapp.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.zxing.Result
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.databinding.FundTransferFragmentBinding
import com.skilrock.infinitylotoapp.permissions.AppPermissions
import com.skilrock.infinitylotoapp.utility.*
import com.skilrock.infinitylotoapp.viewmodels.FundTransferViewModel
import kotlinx.android.synthetic.main.fund_transfer_fragment.*

class FundTransferFragment : BaseFragment() {

    private lateinit var viewModel: FundTransferViewModel
    private lateinit var binding: FundTransferFragmentBinding

    private var mCodeScanner: CodeScanner? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fund_transfer_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbarElements(getText(R.string.fund_transfer).toString(), View.GONE)
        setViewModel()
        checkScannerPermission()
        setClickListeners()
        etMachineId.setText("")
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this).get(FundTransferViewModel::class.java)
        binding.viewModel = viewModel
    }

    private fun checkScannerPermission() {
        if (AppPermissions.checkPermission(activity))
            startScanning()
        else
            AppPermissions.checkPermission(activity)
    }

    private fun setClickListeners() {
        scannerView.setOnClickListener {
            etMachineId.setText("")
            mCodeScanner?.startPreview()
        }

        btnProceed.setOnClickListener {
            if (validate()) {
                val direction: NavDirections = FundTransferFragmentDirections
                    .actionFundTransferFragmentToFundTransferAmountFragment(
                        etMachineId.getTrimText().split("###")[0],
                        etMachineId.getTrimText().split("###")[1],
                        etMachineId.getTrimText().split("###")[2]
                    )

                Navigation.findNavController(it).navigate(direction)
            }
        }

        etMachineId.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    btnProceed.hideKeyboard()
                    btnProceed.performClick()
                    true
                }
                else -> false
            }
        }
    }

    private fun validate(): Boolean {
        if (etMachineId.getTrimText().isEmpty()) {
            context?.redToast(getString(R.string.machine_id_required))
            tilMachineId.startAnimation(shakeError())
            return false
        }

        if (etMachineId.getTrimText().split("###").size != 3) {
            context?.redToast(getString(R.string.invalid_machine_id))
            tilMachineId.startAnimation(shakeError())
            return false
        }

        try {
            if (etMachineId.getTrimText().split("###")[0].isBlank() ||
                etMachineId.getTrimText().split("###")[1].isBlank() ||
                etMachineId.getTrimText().split("###")[2].isBlank()
            ) {
                context?.redToast(getString(R.string.invalid_machine_id))
                tilMachineId.startAnimation(shakeError())
                return false
            }
        } catch (e: Exception) {
            context?.redToast(getString(R.string.invalid_machine_id))
            tilMachineId.startAnimation(shakeError())
            return false
        }

        Log.d("log", "Machine Id: ${etMachineId.getTrimText().split("###")[0]}")
        Log.d("log", "Merchant Id: ${etMachineId.getTrimText().split("###")[1]}")
        Log.d("log", "Version: ${etMachineId.getTrimText().split("###")[2]}")
        return true
    }

    private fun startScanning() {
        mCodeScanner = CodeScanner(master, scannerView)

        mCodeScanner?.camera = CodeScanner.CAMERA_BACK
        mCodeScanner?.formats = CodeScanner.ALL_FORMATS
        mCodeScanner?.autoFocusMode = AutoFocusMode.SAFE
        mCodeScanner?.scanMode = ScanMode.SINGLE
        mCodeScanner?.isAutoFocusEnabled = true
        mCodeScanner?.isFlashEnabled = false

        mCodeScanner?.let { codeScanner ->
            codeScanner.decodeCallback = DecodeCallback { result: Result ->
                master.runOnUiThread { onScanned(result) }
            }

            scannerView.setOnClickListener {
                codeScanner.startPreview()
                etMachineId.setText("")
            }
        }
    }

    private fun onScanned(result: Result) {
        etMachineId.setText(result.text)
        vibrate(master)
        btnProceed.performClick()
    }

    override fun onResume() {
        super.onResume()
        mCodeScanner?.startPreview()
    }

    override fun onPause() {
        mCodeScanner?.releaseResources()
        super.onPause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // PERMISSION GRANTED
                startScanning()
            } else {
                // PERMISSION DENIED
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    context?.let {
                        if (ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            AppPermissions.showMessageOKCancel(
                                activity,
                                getString(R.string.allow_permission),
                                etMachineId
                            )
                        }
                    }
                }
            }
        }
    }

}
