package com.skilrock.infinitylotoapp.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.response_data_class.BalanceResponseData
import com.skilrock.infinitylotoapp.data_class.response_data_class.LogoutResponseData
import com.skilrock.infinitylotoapp.data_class.response_data_class.UploadAvatarResponseData
import com.skilrock.infinitylotoapp.databinding.MyProfileFragmentBinding
import com.skilrock.infinitylotoapp.permissions.AppPermissions
import com.skilrock.infinitylotoapp.utility.*
import com.skilrock.infinitylotoapp.viewmodels.MyProfileViewModel
import kotlinx.android.synthetic.main.my_profile_fragment.*
import java.io.File
import java.io.IOException

class MyProfileFragment : BaseFragment() {

    private lateinit var viewModel: MyProfileViewModel
    private lateinit var binding: MyProfileFragmentBinding

    private var mImageFileLocation = ""
    private var photoFile: File? = null
    private var postPath: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.my_profile_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbarElements(getText(R.string.profile).toString(), View.GONE)
        setViewModel()
        setClickListeners()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this).get(MyProfileViewModel::class.java)
        binding.lifecycleOwner = this
        binding.myProfileViewModel = viewModel

        viewModel.liveDataVibrator.observe(viewLifecycleOwner, observerVibrator)
        viewModel.liveDataLoader.observe(viewLifecycleOwner, observerLoader)
        viewModel.liveDataLogoutStatus.observe(viewLifecycleOwner, observerLogoutStatus)
        viewModel.liveDataBalanceStatus.observe(viewLifecycleOwner, observerBalanceStatus)
        viewModel.liveDataProfileUpdateStatus.observe(viewLifecycleOwner, observerUpdateProfileStatus)
    }

    private fun setClickListeners() {
        llMyTransactions.onClick(context, R.id.action_myProfileFragment_to_myTransactionFragment)
        llChangePassword.onClick(context, R.id.action_myProfileFragment_to_changePasswordFragment)
        iv_profilePic.setOnClickListener { checkPermissions() }
    }

    private fun LinearLayoutCompat.onClick(context: Context?, navigationId: Int) {
        setOnClickListener {
            Navigation.findNavController(it).navigate(navigationId)
        }
    }

    private val observerLogoutStatus = Observer<ResponseStatus<LogoutResponseData>> {
        activity?.let {
            if (isNetworkConnected(it)) {
                if (BuildConfig.IS_LOGIN_MANDATORY)
                    performLogoutCleanUp(it)
                else {
                    SharedPrefUtils.clearAppSharedPref(master)
                    master.setToolbarLoginOrPlayerInfo()
                    master.navigateToHome()
                }
            }
        }
    }

    private val observerVibrator = Observer<String> { context?.let { vibrate(it) } }

    private val observerLoader = Observer<Boolean> {
        context?.let { cxt ->
            if (it) Loader.showLoader(cxt) else Loader.dismiss()
        }
    }

    private val observerBalanceStatus = Observer<ResponseStatus<BalanceResponseData>> {
        when (it) {
            is ResponseStatus.Success -> {
                context?.let { cxt ->
                    cxt.greenToast(getString(R.string.balance_refreshed))
                    it.response.wallet?.let { wallet ->
                        updateToolbarBalance(wallet.totalBalance.toString())
                        PlayerInfo.setBalance(cxt, wallet)
                    }
                }
            }

            is ResponseStatus.Error -> {
                context?.let { cxt -> cxt.redToast(it.errorMessage.getMsg(cxt)) }
            }

            is ResponseStatus.TechnicalError -> {
                context?.redToast(getString(it.errorMessageCode))
            }
        }
    }

    private val observerUpdateProfileStatus = Observer<ResponseStatus<UploadAvatarResponseData>> {
        photoFile?.let { file -> deleteTempFiles(file) }
        when (it) {
            is ResponseStatus.Success -> {
                PlayerInfo.getPlayerInfo()?.playerLoginInfo?.avatarPath =
                    it.response.avatarPath?.removePrefix(
                        PlayerInfo.getPlayerInfo()?.playerLoginInfo?.commonContentPath.toString()
                    )
                context?.greenToast(getString(R.string.image_uploaded_successfully))
            }

            is ResponseStatus.Error -> {
                context?.let { cxt -> cxt.redToast(it.errorMessage.getMsg(cxt)) }
            }

            is ResponseStatus.TechnicalError -> {
                context?.redToast(getString(it.errorMessageCode))
            }
        }
    }

    private fun checkPermissions() {
        if (AppPermissions.checkPermissionFoStorage(activity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)))
            AppPermissions.requestPermissionForStorageAndCamera(activity)
        else
            showOptionsDialog()
    }

    private fun showOptionsDialog() {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.pick_option)
        builder.setItems(resources.getStringArray(R.array.pick_options)) {
                dialog, which ->
            dialog.dismiss()
            when (which) {
                0 -> captureImageFromCamera()
                1 -> chooseImageFromGallery()
            }
        }
        builder.show()
    }

    private fun captureImageFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                context?.redToast(getString(R.string.something_went_wrong))
            }
            val photoURI = photoFile?.let {
                FileProvider.getUriForFile(
                    requireContext(), BuildConfig.APPLICATION_ID + ".provider", it
                )
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(intent, REQUEST_TAKE_PHOTO)
        }
    }

    private fun chooseImageFromGallery() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, REQUEST_PICK_PHOTO)
    }

    private fun createImageFile(): File {

        val imageFileName =
            "Infiniti_" + PlayerInfo.getPlayerMobileNumber()
        val storageDir =
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        mImageFileLocation = image.absolutePath
        return image
    }

    private fun deleteTempFiles(file: File): Boolean {
        if (file.isDirectory) {
            val files = file.listFiles()
            if (files != null) {
                for (f in files) {
                    if (f.isDirectory) {
                        deleteTempFiles(f)
                    } else {
                        f.delete()
                    }
                }
            }
        }
        return file.delete()
    }

    companion object {
        private const val REQUEST_PICK_PHOTO = 100
        private const val REQUEST_TAKE_PHOTO = 200
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PICK_PHOTO) {
                if (data != null) {
                    // Get the Image from data
                    val selectedImage = data.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

                    selectedImage?.let { selectedImageNonNull ->
                        val cursor = context?.contentResolver?.query(selectedImageNonNull, filePathColumn, null, null, null)

                        cursor?.let {
                            it.moveToFirst()

                            val columnIndex = it.getColumnIndex(filePathColumn[0])
                            val mediaPath = it.getString(columnIndex)
                            iv_profilePic.setImageBitmap(BitmapFactory.decodeFile(mediaPath))
                            it.close()
                            postPath = mediaPath
                            postPath?.let { path -> viewModel.onProfileUpload(path) }
                        }
                    }
                }

            } else if (requestCode == REQUEST_TAKE_PHOTO) {
                postPath = mImageFileLocation
                postPath?.let { viewModel.onProfileUpload(postPath!!) }
            }

        } else if (resultCode != Activity.RESULT_CANCELED) {
            context?.redToast(getString(R.string.something_went_wrong))
        }
    }

}


