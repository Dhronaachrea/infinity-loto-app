package com.skilrock.infinitylotoapp.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.adapter.ProfileAdapter
import com.skilrock.infinitylotoapp.data_class.response_data_class.*
import com.skilrock.infinitylotoapp.databinding.ActivityProfileSabanzuriBinding
import com.skilrock.infinitylotoapp.dialogs.EmailVerifyBottomSheetDialog
import com.skilrock.infinitylotoapp.dialogs.ErrorDialog
import com.skilrock.infinitylotoapp.dialogs.LoginDialog
import com.skilrock.infinitylotoapp.dialogs.SuccessDialog
import com.skilrock.infinitylotoapp.permissions.AppPermissions
import com.skilrock.infinitylotoapp.utility.*
import com.skilrock.infinitylotoapp.viewmodels.ProfileViewModel
import kotlinx.android.synthetic.main.activity_profile_sabanzuri.*
import kotlinx.android.synthetic.main.toolbar_without_login_option.*
import java.io.File
import java.io.IOException

class ProfileActivity : AppCompatActivity() {

    private lateinit var viewModel: ProfileViewModel
    private var adapter: ProfileAdapter? = null
    private var emailVerificationDialog: EmailVerifyBottomSheetDialog? = null

    private var mImageFileLocation = ""
    private var photoFile: File? = null
    private var postPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAppTheme()
        setDataBindingAndViewModel()
        performThemeRelatedChanges()
        setClickListeners()
    }

    private fun setAppTheme() {
        if (SharedPrefUtils.getAppTheme(this) == THEME_DARK)
            setTheme(R.style.DarkTheme)
        else
            setTheme(R.style.LightTheme)
    }

    override fun onResume() {
        super.onResume()
        Log.i("log", "Profile Pic Url: ${PlayerInfo.getProfilePicPath()}")
        viewModel.refreshPlayerInformation()
        setRecyclerView()
        Glide.with(applicationContext)
            .load(PlayerInfo.getProfilePicPath())
            .apply(
                RequestOptions()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
            )
            .into(iv_profilePic)
    }

    private fun setDataBindingAndViewModel() {
        val binding : ActivityProfileSabanzuriBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile_sabanzuri)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        binding.lifecycleOwner = this
        binding.myProfileViewModel = viewModel

        viewModel.liveDataVibrator.observe(this, observerVibrator)
        viewModel.liveDataLoader.observe(this, observerLoader)
        viewModel.liveDataLogoutStatus.observe(this, observerLogoutStatus)
        viewModel.liveDataBalanceStatus.observe(this, observerBalanceStatus)
        viewModel.liveDataEmailCodeStatus.observe(this, observerEmailCode)
        viewModel.liveDataEmailVerifyStatus.observe(this, observerEmailVerify)
        viewModel.liveDataProfileUpdateStatus.observe(this, observerUpdateProfileStatus)
    }

    private fun performThemeRelatedChanges() {
        if (SharedPrefUtils.getAppTheme(this) == THEME_DARK)
            ivRefresh.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY)
        else
            ivWallet.setColorFilter(ContextCompat.getColor(this, R.color.persimmon), android.graphics.PorterDuff.Mode.MULTIPLY)
    }

    private fun setRecyclerView() {
        val infoMap = LinkedHashMap<String, String>()

        if (PlayerInfo.getPlayerFirstName().isNotBlank() || PlayerInfo.getPlayerLastName().isNotBlank())
            infoMap["name"] = PlayerInfo.getPlayerFirstName() + " " + PlayerInfo.getPlayerLastName()
        if (PlayerInfo.getPlayerMobileNumber().isNotBlank()) infoMap["mobile"] = PlayerInfo.getPlayerMobileNumber()
        if (PlayerInfo.getPlayerEmailId().isNotBlank()) infoMap["email"] = PlayerInfo.getPlayerEmailId()
        if (PlayerInfo.getPlayerDob().isNotBlank()) infoMap["dob"] = viewModel.getDateOfBirth()
        if (PlayerInfo.getPlayerGender().isNotBlank()) infoMap["gender"] = PlayerInfo.getPlayerGender()
        if (PlayerInfo.getPlayerAddress().isNotBlank()) infoMap["address"] = PlayerInfo.getPlayerAddress()
        if (PlayerInfo.getPlayerCountry().isNotBlank()) infoMap["country"] = PlayerInfo.getPlayerCountry()

        rvProfile.layoutManager = LinearLayoutManager(this)
        rvProfile.setHasFixedSize(true)
        adapter = ProfileAdapter(this, infoMap) { viewModel.callEmailCodeApi(true) }
        rvProfile.adapter = adapter
    }

    private fun setClickListeners() {
        ivBack.setOnClickListener { finish() }
        iv_profilePic.setOnClickListener { checkPermissions() }
    }

    private val observerEmailCode = Observer<ResponseStatus<EmailCodeResponseData>> {
        when(it) {
            is ResponseStatus.Success -> {
                if (it.response.openDialog) {
                    emailVerificationDialog = EmailVerifyBottomSheetDialog(PlayerInfo.getPlayerEmailId(), ::callEmailVerifyApi) { viewModel.callEmailCodeApi(false) }.apply {
                        show(supportFragmentManager, EmailVerifyBottomSheetDialog.TAG)
                    }
                } else {
                    greenToast("Verification Code Sent Again")
                }
            }

            is ResponseStatus.Error -> {
                if (it.errorCode == SESSION_EXPIRE_CODE) {
                    redToast(it.errorMessage.getMsg(this))
                    openLoginDialog()
                } else {
                    ErrorDialog(this, getString(R.string.email_verification_error), it.errorMessage.getMsg(this), it.errorCode, Intent(this, HomeKenyaActivity::class.java)) {}.showDialog()
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorDialog(this, getString(R.string.email_verification_error), getString(it.errorMessageCode)) {}.showDialog()
            }
        }
    }

    private fun callEmailVerifyApi(code : String) {
        viewModel.callEmailVerifyApi(code)
    }

    private val observerEmailVerify = Observer<ResponseStatus<EmailVerifyResponseData>> {
        when(it) {
            is ResponseStatus.Success -> {
                emailVerificationDialog?.dismiss()
                SuccessDialog(this, getString(R.string.email_verified), getString(R.string.email_verified_successfully)) {
                    PlayerInfo.setEmailVerified()
                    adapter?.notifyDataSetChanged()
                }.showDialog()
            }

            is ResponseStatus.Error -> {
                if (it.errorCode == SESSION_EXPIRE_CODE) {
                    redToast(it.errorMessage.getMsg(this))
                    openLoginDialog()
                } else {
                    ErrorDialog(this, getString(R.string.email_verification_error), it.errorMessage.getMsg(this), it.errorCode, Intent(this, HomeKenyaActivity::class.java)) {}.showDialog()
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorDialog(this, getString(R.string.email_verification_error), getString(it.errorMessageCode)) {}.showDialog()
            }
        }
    }

    private val observerLogoutStatus = Observer<ResponseStatus<LogoutResponseData>> {
        if (isNetworkConnected(this)) {
            if (BuildConfig.IS_LOGIN_MANDATORY)
                performLogoutCleanUp(this)
            else {
                SharedPrefUtils.clearAppSharedPref(this)
            }
        }
    }

    private val observerVibrator = Observer<String> { vibrate(this) }

    private val observerLoader = Observer<Boolean> {
        if (it) Loader.showLoader(this) else Loader.dismiss()
    }

    private val observerBalanceStatus = Observer<ResponseStatus<BalanceResponseData>> {
        when (it) {
            is ResponseStatus.Success -> {
                greenToast(getString(R.string.balance_refreshed))
                it.response.wallet?.let { wallet ->
                    PlayerInfo.setBalance(this, wallet)
                }
            }

            is ResponseStatus.Error -> { redToast(it.errorMessage.getMsg(this)) }

            is ResponseStatus.TechnicalError -> { redToast(getString(it.errorMessageCode)) }
        }
    }

    private val observerUpdateProfileStatus = Observer<ResponseStatus<UploadAvatarResponseData>> {
        photoFile?.let { file -> deleteTempFiles(file) }
        when (it) {
            is ResponseStatus.Success -> {
                PlayerInfo.setProfileImage(this, it.response.avatarPath?.removePrefix(
                    PlayerInfo.getPlayerInfo()?.playerLoginInfo?.commonContentPath.toString()
                ) ?: "")
                greenToast(getString(R.string.image_uploaded_successfully))
            }

            is ResponseStatus.Error -> { redToast(it.errorMessage.getMsg(this)) }

            is ResponseStatus.TechnicalError -> { redToast(getString(it.errorMessageCode)) }
        }
    }

    private fun checkPermissions() {
        if (AppPermissions.checkPermissionFoStorage(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)))
            AppPermissions.requestPermissionForStorageAndCamera(this)
        else
            showOptionsDialog()
    }

    private fun showOptionsDialog() {

        val builder = AlertDialog.Builder(this)
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
        if (intent.resolveActivity(packageManager) != null) {
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                redToast(getString(R.string.something_went_wrong))
            }
            val photoURI = photoFile?.let {
                FileProvider.getUriForFile(
                    this, BuildConfig.APPLICATION_ID + ".provider", it
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
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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
                        val cursor = contentResolver?.query(selectedImageNonNull, filePathColumn, null, null, null)

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
            redToast(getString(R.string.something_went_wrong))
        }
    }

    fun onClickProfile(view: View) {
        startActivity(Intent(this, UpdateProfileActivity::class.java))
    }

    private fun openLoginDialog() {
        val fm: FragmentManager = supportFragmentManager
        val loginDialog = LoginDialog(::onSuccessfulLogin)
        loginDialog.show(fm, "LoginDialog")
    }

    private fun onSuccessfulLogin() {
        viewModel.playerBalance.value = "${BuildConfig.CURRENCY_SYMBOL} ${PlayerInfo.getPlayerTotalBalance()}"
        viewModel.playerName.value = if (PlayerInfo.getPlayerFirstName().trim().isEmpty()) {
            PlayerInfo.getPlayerUserName()
        } else {
            PlayerInfo.getPlayerFirstName() + " " + PlayerInfo.getPlayerLastName()
        }

    }

}