package com.skilrock.infinitylotoapp.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.response_data_class.UpdateProfileResponseData
import com.skilrock.infinitylotoapp.databinding.ActivityUpdateProfileBinding
import com.skilrock.infinitylotoapp.dialogs.ErrorDialog
import com.skilrock.infinitylotoapp.dialogs.ProfileUpdateBottomSheetDialog
import com.skilrock.infinitylotoapp.dialogs.SuccessDialog
import com.skilrock.infinitylotoapp.utility.*
import com.skilrock.infinitylotoapp.viewmodels.UpdateProfileViewModel
import kotlinx.android.synthetic.main.activity_update_profile.*
import kotlinx.android.synthetic.main.toolbar_without_login_option.*

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var viewModel: UpdateProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAppTheme()
        setDataBindingAndViewModel()
        setListeners()
        disableEmailIfVerified()
        setGenderAndDob()
    }

    private fun setAppTheme() {
        if (SharedPrefUtils.getAppTheme(this) == THEME_DARK)
            setTheme(R.style.DarkTheme)
        else
            setTheme(R.style.LightTheme)
    }

    private fun setDataBindingAndViewModel() {
        val binding : ActivityUpdateProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_update_profile)
        viewModel = ViewModelProvider(this).get(UpdateProfileViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataLoader.observe(this, observerLoader)
        viewModel.liveDataUpdateProfile.observe(this, observerUpdateProfile)
        viewModel.liveDataUpdatePlayerInfo.observe(this, observerUpdatePlayerInfo)
    }

    private fun setListeners() {
        ivBack.setOnClickListener { finish() }

        etDob.setOnClickListener {
            tilDob.hideKeyboard()
            openDobDialog(this, etDob)
        }

        btnUpdate.setOnClickListener {
            if (validate()) {
                val data =
                    "<b><i>First Name:</i></b> ${viewModel.firstName.value}<br><b><i>Last Name:</i></b> ${viewModel.lastName.value}<br><b><i>Email:</i></b> ${viewModel.email.value}<br><b><i>DOB:</i></b> ${etDob.text.toString().trim()}<br><b><i>Gender:</i></b> ${viewModel.gender}<br><b><i>Address:</i></b> ${viewModel.address.value}"

                ProfileUpdateBottomSheetDialog(data) { viewModel.callProfileUpdateApi(etDob.text.toString().trim()) }.apply {
                    show(supportFragmentManager, ProfileUpdateBottomSheetDialog.TAG)
                }
            }
        }

        radioMale.setOnClickListener { viewModel.gender = "Male" }

        radioFemale.setOnClickListener { viewModel.gender = "Female" }
    }

    private fun validate() : Boolean {
        val email = etEmail.getTrimText()
        if (email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.showError(getString(R.string.invalid_email), null,  tilEmail)
            return false
        }

        if(viewModel.firstName.value.isNullOrBlank() and viewModel.lastName.value.isNullOrBlank() and viewModel.email.value.isNullOrBlank() and etDob.text.toString().isBlank()
            and viewModel.address.value.isNullOrBlank() and viewModel.gender.isBlank()) {
                redToast("There is nothing to update.")
                return false
        }
        return true
    }

    private fun disableEmailIfVerified() {
        if (PlayerInfo.getPlayerEmailId().isNotBlank()) {
            val isEmailVerified = PlayerInfo.isEmailVerified()
            etEmail.isFocusable = !isEmailVerified
            etEmail.isClickable = !isEmailVerified
            etEmail.isFocusableInTouchMode = !isEmailVerified

            if (isEmailVerified) {
                if (SharedPrefUtils.getAppTheme(this) == THEME_DARK)
                    etEmail.setTextColor(Color.parseColor("#6d7ba1"))
                else
                    etEmail.setTextColor(Color.parseColor("#676767"))
            }
        }
    }

    private fun setGenderAndDob() {
        PlayerInfo.getPlayerInfo()?.playerLoginInfo?.gender?.let {
            if (it.trim().equals("M", true) || it.trim().equals("Male", true)) {
                radioMale.isChecked = true
                viewModel.gender    = "Male"
            }
            else if (it.trim().equals("F", true) || it.trim().equals("Female", true)) {
                radioFemale.isChecked   = true
                viewModel.gender        = "Female"
            }
        }

        etDob.setText(viewModel.getDateOfBirth())
    }

    private val observerLoader = Observer<Boolean> {
        if (it) Loader.showLoader(this) else Loader.dismiss()
    }

    private val observerUpdateProfile = Observer<ResponseStatus<UpdateProfileResponseData>> {
        when(it) {
            is ResponseStatus.Success -> {
                it.response.playerInfoBean?.let { plrInfoBean ->
                    plrInfoBean.dob = viewModel.getDob(plrInfoBean.dob ?: "", "yyyy-MM-dd", "MMM d, yyyy")
                    PlayerInfo.updateProfile(this@UpdateProfileActivity, plrInfoBean.firstName ?: PlayerInfo.getPlayerFirstName(),
                        plrInfoBean.lastName ?: PlayerInfo.getPlayerLastName(), plrInfoBean.emailId ?: PlayerInfo.getPlayerEmailId(),
                        plrInfoBean.dob ?: PlayerInfo.getPlayerDob(), plrInfoBean.gender ?: PlayerInfo.getPlayerGender(),
                        plrInfoBean.addressLine1 ?: PlayerInfo.getPlayerAddress())
                }
                SuccessDialog(this, getString(R.string.update_profile),
                    getString(R.string.profile_updated_successfully)) {
                    finish()
                }.showDialog()
            }

            is ResponseStatus.Error -> {
                ErrorDialog(this, getString(R.string.update_profile), it.errorMessage.getMsg(this), it.errorCode, Intent(this, HomeKenyaActivity::class.java)) {}.showDialog()
            }

            is ResponseStatus.TechnicalError -> {
                ErrorDialog(this, getString(R.string.update_profile), getString(it.errorMessageCode)) {}.showDialog()
            }
        }
    }

    private val observerUpdatePlayerInfo = Observer<UpdateProfileResponseData.PlayerInfoBean> { plrInfoBean ->
        plrInfoBean.dob = viewModel.getDob(plrInfoBean.dob ?: "", "yyyy-MM-dd", "MMM d, yyyy")
        PlayerInfo.updateProfile(this@UpdateProfileActivity, plrInfoBean.firstName ?: PlayerInfo.getPlayerFirstName(),
            plrInfoBean.lastName ?: PlayerInfo.getPlayerLastName(), plrInfoBean.emailId ?: PlayerInfo.getPlayerEmailId(),
            plrInfoBean.dob ?: PlayerInfo.getPlayerDob(), plrInfoBean.gender ?: PlayerInfo.getPlayerGender(),
            plrInfoBean.addressLine1 ?: PlayerInfo.getPlayerAddress())
    }

}