package com.skilrock.infinitylotoapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.response_data_class.ChangePasswordResponseData
import com.skilrock.infinitylotoapp.databinding.ActivityChangePasswordBinding
import com.skilrock.infinitylotoapp.dialogs.ErrorDialog
import com.skilrock.infinitylotoapp.dialogs.SuccessDialog
import com.skilrock.infinitylotoapp.utility.*
import com.skilrock.infinitylotoapp.viewmodels.ChangePasswordViewModel
import kotlinx.android.synthetic.main.activity_home.toolbar
import kotlinx.android.synthetic.main.change_password_fragment.*
import kotlinx.android.synthetic.main.toolbar_without_login_option.*

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var viewModel: ChangePasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAppTheme()
        setSupportActionBar(toolbar)
        setDataBindingAndViewModel()
        keyboardEnterFunctionality()
        ivBack.setOnClickListener { finish() }
    }

    private fun setAppTheme() {
        if (SharedPrefUtils.getAppTheme(this) == THEME_DARK)
            setTheme(R.style.DarkTheme)
        else
            setTheme(R.style.LightTheme)
    }

    private fun setDataBindingAndViewModel() {
        val binding : ActivityChangePasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)

        viewModel = ViewModelProvider(this).get(ChangePasswordViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataLoader.observe(this, observerLoader)
        viewModel.liveDataVibrator.observe(this, observerVibrator)
        viewModel.liveDataValidation.observe(this, observerValidation)
        viewModel.liveDataResponseStatus.observe(this, observerResponseStatus)
    }

    private fun keyboardEnterFunctionality() {
        etConfirmPassword.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    btnSubmit.hideKeyboard()
                    viewModel.onSubmitChangePassword()
                    true
                }
                else -> false
            }
        }
    }

    private val observerResponseStatus = Observer<ResponseStatus<ChangePasswordResponseData>> {
        when (it) {
            is ResponseStatus.Success -> SuccessDialog(this,
                    getString(R.string.change_password),
                    getString(R.string.password_changed_successfully)) {
                performLogoutCleanUp(this, Intent(this, HomeKenyaActivity::class.java))
                }.showDialog()

            is ResponseStatus.Error -> ErrorDialog(this,
                getString(R.string.change_password_error),
                it.errorMessage.getMsg(this), it.errorCode,
                Intent(this, HomeKenyaActivity::class.java)) {}.showDialog()

            is ResponseStatus.TechnicalError -> ErrorDialog(
                this, getString(R.string.change_password_error), getString(it.errorMessageCode)
            ) {}.showDialog()

        }
    }

    private val observerVibrator = Observer<String> { vibrate(this) }

    private val observerLoader = Observer<Boolean> {
        if (it) Loader.showLoader(this) else Loader.dismiss()
    }

    private val observerValidation = Observer<ChangePasswordValidation> {
        when (it) {
            is ChangePasswordValidation.OldPasswordValidation ->
                etOldPassword.showError(getString(it.errorMessageCode),
                    this, tilOldPassword)

            is ChangePasswordValidation.NewPasswordValidation ->
                etNewPassword.showError(getString(it.errorMessageCode),
                    this, tilNewPassword)

            is ChangePasswordValidation.ConfirmPasswordValidation ->
                etConfirmPassword.showError(getString(it.errorMessageCode),
                    this, tilNewPassword)
        }
    }

}