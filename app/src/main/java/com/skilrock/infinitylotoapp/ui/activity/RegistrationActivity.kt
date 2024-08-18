package com.skilrock.infinitylotoapp.ui.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.response_data_class.RegistrationOtpResponseData
import com.skilrock.infinitylotoapp.data_class.response_data_class.RegistrationResponseData
import com.skilrock.infinitylotoapp.databinding.ActivityRegistrationBinding
import com.skilrock.infinitylotoapp.dialogs.ErrorDialog
import com.skilrock.infinitylotoapp.dialogs.RegistrationOtpBottomSheetDialog
import com.skilrock.infinitylotoapp.dialogs.SuccessDialog
import com.skilrock.infinitylotoapp.utility.*
import com.skilrock.infinitylotoapp.viewmodels.RegistrationViewModel
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {

    private lateinit var viewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAppTheme()
        setDataBindingAndViewModel()
        toolbarNavigation()
        setMobileNumberValidations()
        keyboardEnterFunctionality()
    }

    private fun setAppTheme() {
        if (SharedPrefUtils.getAppTheme(this) == THEME_DARK)
            setTheme(R.style.DarkTheme)
        else
            setTheme(R.style.LightTheme)
    }

    private fun setDataBindingAndViewModel() {
        val binding : ActivityRegistrationBinding = DataBindingUtil.setContentView(this, R.layout.activity_registration)
        viewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(this, observerVibrator)
        viewModel.liveDataLoader.observe(this, observerLoader)
        viewModel.liveDataHideKeyboard.observe(this, observerHideKeyboard)
        viewModel.liveDataValidation.observe(this, observerValidation)
        viewModel.liveDataRegistrationOtp.observe(this, observerOtp)
        viewModel.liveDataRegistrationStatus.observe(this, observerRegistrationStatus)
    }

    private fun toolbarNavigation() {
        toolbar.navigationIcon?.setColorFilter(getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN)
        toolbar.setNavigationOnClickListener {
            vibrate(this)
            finish()
        }
    }

    private fun setMobileNumberValidations() {
        if (BuildConfig.CLIENT == CLIENT_SABANZURI)
            tilMobileNumber.hint =
                (getString(R.string.mobile_number) + " (" + COUNTRY_CODE_KENYA + ")")

        etMobileNumber.filters = arrayOf(InputFilter.LengthFilter(BuildConfig.MOBILE_NO_LENGTH))
    }


    private fun keyboardEnterFunctionality() {
        etReferralCode.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    viewModel.callRegistrationOtpApi(false)
                    true
                }
                else -> false
            }
        }
    }

    private val observerOtp = Observer<ResponseStatus<RegistrationOtpResponseData>> {
        when(it) {
            is ResponseStatus.Success -> {
                showNotification(this, it.response.respMsg ?: "")
                if (it.response.showResendOtpToast)
                    greenToast(getString(R.string.otp_sent_success))
                else {
                    RegistrationOtpBottomSheetDialog(etMobileNumber.getTrimText(), ::onRegisterClick, ::onResendOtp
                    ).apply {
                        show(supportFragmentManager, RegistrationOtpBottomSheetDialog.TAG)
                    }
                }
            }

            is ResponseStatus.Error ->
                ErrorDialog(this, getString(R.string.registration_error), it.errorMessage.getMsg(this)) {}.showDialog()

            is ResponseStatus.TechnicalError ->
                ErrorDialog(this, getString(R.string.registration_error), getString(it.errorMessageCode)) {}.showDialog()
        }
    }

    private fun onRegisterClick(otp: String) {
        viewModel.onRegisterButtonClick(otp)
    }

    private fun onResendOtp() {
        viewModel.callRegistrationOtpApi(true)
    }

    private val observerRegistrationStatus = Observer<ResponseStatus<RegistrationResponseData>> {
        when(it) {
            is ResponseStatus.Success ->
                SuccessDialog(this, getString(R.string.register), getString(R.string.registered_successfully)) {finish()}.showDialog()

            is ResponseStatus.Error ->
                ErrorDialog(this, getString(R.string.registration_error), it.errorMessage.getMsg(this)) {}.showDialog()

            is ResponseStatus.TechnicalError ->
                ErrorDialog(this, getString(R.string.registration_error), getString(it.errorMessageCode)) {}.showDialog()
        }
    }

    private val observerVibrator = Observer<String> { vibrate(this) }

    private val observerValidation = Observer<RegistrationViewModel.Validation> {
        when(it) {
            is RegistrationViewModel.Validation.FirstName -> etFirstName.showError(getString(it.errorMessageCode), null,  tilFirstName)
            is RegistrationViewModel.Validation.LastName -> etLastName.showError(getString(it.errorMessageCode), null,  tilLastName)
            is RegistrationViewModel.Validation.UserName -> etUserName.showError(getString(it.errorMessageCode), null,  tilUserName)
            is RegistrationViewModel.Validation.MobileNumber -> etMobileNumber.showError(getString(it.errorMessageCode), null,  tilMobileNumber)
            is RegistrationViewModel.Validation.Password -> etPassword.showError(getString(it.errorMessageCode), this,  tilPassword)
            is RegistrationViewModel.Validation.ConfirmPassword -> etConfirmPassword.showError(getString(it.errorMessageCode), this,  tilConfirmPassword)
        }
    }

    private val observerLoader = Observer<Boolean> {
        if (it) Loader.showLoader(this) else Loader.dismiss()
    }

    fun onClickTermsAndConditions(view: View) {
        val intent = Intent(this, ExtrasKenyaActivity::class.java)
        intent.putExtra("webUrl", TNC_URL)
        startActivity(intent)
    }

    private val observerHideKeyboard = Observer<Boolean> {
        if (it) btnRegister.hideKeyboard()
    }

    private fun showNotification(context: Context, body: String) {
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1
        val channelId = "channel-01"
        val channelName = "Channel Name"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(
                channelId, channelName, importance
            )
            notificationManager.createNotificationChannel(mChannel)
        }
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.sabanzuri_launcher_icon)
            .setContentTitle(getString(R.string.app_name_sabanzuri))
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))

        notificationManager.notify(notificationId, mBuilder.build())
    }

}
