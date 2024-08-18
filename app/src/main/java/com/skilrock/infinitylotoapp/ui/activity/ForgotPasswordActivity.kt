package com.skilrock.infinitylotoapp.ui.activity

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.response_data_class.ForgotPasswordResponseData
import com.skilrock.infinitylotoapp.databinding.ActivityForgotPasswordBinding
import com.skilrock.infinitylotoapp.dialogs.ErrorDialog
import com.skilrock.infinitylotoapp.dialogs.ForgotPasswordOtpDialog
import com.skilrock.infinitylotoapp.dialogs.SuccessDialog
import com.skilrock.infinitylotoapp.utility.*
import com.skilrock.infinitylotoapp.viewmodels.ForgotPasswordViewModel
import kotlinx.android.synthetic.main.activity_forgot_password.*
import java.util.regex.Pattern

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var viewModel: ForgotPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAppTheme()
        setDataBindingAndViewModel()
        toolbarNavigation()
        setHintForMobileNumber()
        keyboardEnterFunctionality()
    }

    private fun setAppTheme() {
        if (SharedPrefUtils.getAppTheme(this) == THEME_DARK)
            setTheme(R.style.DarkTheme)
        else
            setTheme(R.style.LightTheme)
    }

    private fun setDataBindingAndViewModel() {
        val binding : ActivityForgotPasswordBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_forgot_password
        )
        viewModel = ViewModelProvider(this).get(ForgotPasswordViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.liveDataLoader.observe(this, observerLoader)
        viewModel.liveDataVibrator.observe(this, observerVibrator)
        viewModel.liveDataValidateMobileNumber.observe(this, observerValidation)
        viewModel.liveDataOtpStatus.observe(this, observerOtpStatus)
        viewModel.liveDataResetPasswordStatus.observe(this, observerResetPasswordStatus)
    }

    private fun toolbarNavigation() {
        toolbar.navigationIcon?.setColorFilter(getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN)
        toolbar.setNavigationOnClickListener {
            vibrate(this)
            finish()
        }
    }

    private fun setHintForMobileNumber() {
        if (BuildConfig.CLIENT == CLIENT_SABANZURI)
            tilMobileNumberForgotPassword.hint =
                (getString(R.string.username_mobile_number) + " (" + COUNTRY_CODE_KENYA + ")")
    }

    private fun keyboardEnterFunctionality() {
        etMobileNumber.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    btnOtp.hideKeyboard()
                    viewModel.sendOtp()
                    true
                }
                else -> false
            }
        }
    }

    private val observerVibrator = Observer<String> { vibrate(this) }

    private val observerLoader = Observer<Boolean> {
        btnOtp.hideKeyboard()
        if (it) Loader.showLoader(this) else Loader.dismiss()
    }

    private val observerValidation = Observer<Int> {
        etMobileNumber.showError(getString(it), null, tilMobileNumberForgotPassword)
    }

    private val observerOtpStatus = Observer<ResponseStatus<ForgotPasswordResponseData>> {
        when(it) {
            is ResponseStatus.Success -> {
                val info = if (isValidMobile(etMobileNumber.getTrimText())) {
                    getString(R.string.otp_success_sent_to) + " " + etMobileNumber.getTrimText()
                } else {
                    getString(R.string.otp_success_sent_to) + " " +
                            etMobileNumber.getTrimText() + "'s " +  getString(R.string.registered_mobile_number)
                }
                //info = getString(R.string.otp_success_sent_to) + " " + etMobileNumber.text
                ForgotPasswordOtpDialog(this, info) { otp, newPassword, confirmPassword ->
                    viewModel.resetPassword(otp, newPassword, confirmPassword, etMobileNumber.getTrimText())
                }.showDialog()

            }

            is ResponseStatus.Error ->
                ErrorDialog(
                    this, getString(R.string.forgot_password_error), it.errorMessage.getMsg(
                        this
                    )
                ) {}.showDialog()


            is ResponseStatus.TechnicalError ->
                ErrorDialog(
                    this,
                    getString(R.string.forgot_password_error),
                    getString(it.errorMessageCode)
                ) {}.showDialog()
        }
    }

    private val observerResetPasswordStatus = Observer<ResponseStatus<ForgotPasswordResponseData>> {
        when(it) {
            is ResponseStatus.Success ->
                SuccessDialog(
                    this,
                    getString(R.string.forgot_password_label),
                    getString(R.string.password_changed_successfully)
                ) { finish() }.showDialog()

            is ResponseStatus.Error ->
                ErrorDialog(
                    this, getString(R.string.forgot_password_error), it.errorMessage.getMsg(
                        this
                    )
                ) {}.showDialog()

            is ResponseStatus.TechnicalError ->
                ErrorDialog(
                    this,
                    getString(R.string.forgot_password_error),
                    getString(it.errorMessageCode)
                ) {}.showDialog()
        }
    }

    private fun isValidMobile(text: String): Boolean {
        return if (!Pattern.matches("[a-zA-Z]+", text)) {
            text.length == BuildConfig.MOBILE_NO_LENGTH
        } else false
    }
}
