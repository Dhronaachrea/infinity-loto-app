package com.skilrock.infinitylotoapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.response_data_class.LoginResponseData
import com.skilrock.infinitylotoapp.databinding.ActivityLoginBinding
import com.skilrock.infinitylotoapp.dialogs.ErrorDialog
import com.skilrock.infinitylotoapp.utility.*
import com.skilrock.infinitylotoapp.viewmodels.LoginViewModel
import com.skilrock.infinitylotoapp.viewmodels.LoginViewModel.Validation
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private var lastClickTime: Long = 0
    private var clickGap = 600

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setDataBindingAndViewModel()
        setClickListeners()
    }

    private fun setDataBindingAndViewModel() {
        val binding : ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.liveDataLoader.observe(this, observerLoader)
        viewModel.liveDataVibrator.observe(this, observerVibrator)
        viewModel.liveDataValidation.observe(this, observerValidation)
        viewModel.liveDataLoginStatus.observe(this, observerLoginStatus)
    }

    private fun setClickListeners() {
        btnForgotPassword.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < clickGap) {
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()

            vibrate(this)
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        btnRegister.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < clickGap) {
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()

            vibrate(this)
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        etPassword.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    btnLogin.hideKeyboard()
                    viewModel.onLoginButtonClick(SharedPrefUtils.getFcmToken(this@LoginActivity))
                    true
                }
                else -> false
            }
        }

        btnLogin.setOnClickListener { viewModel.onLoginButtonClick(SharedPrefUtils.getFcmToken(this@LoginActivity)) }
    }

    private val observerVibrator = Observer<String> { vibrate(this) }

    private val observerLoginStatus = Observer<ResponseStatus<LoginResponseData>> {
        when(it) {
            is ResponseStatus.Success -> {
                PlayerInfo.setLoginData(this, it.response)
                val intent = when (BuildConfig.CLIENT) {
                    CLIENT_SABANZURI -> Intent(this, HomeKenyaActivity::class.java)
                    else -> Intent(this, HomeActivity::class.java)
                }
                startActivity(intent)
                finish()
            }

            is ResponseStatus.Error -> {
                ErrorDialog(this, getString(R.string.login_error), it.errorMessage.getMsg(this)) {}.showDialog()
            }

            is ResponseStatus.TechnicalError -> {
                ErrorDialog(this, getString(R.string.login_error), getString(it.errorMessageCode)) {}.showDialog()
            }
        }
    }

    private val observerLoader = Observer<Boolean> {
        btnLogin.hideKeyboard()
        if (it) Loader.showLoader(this) else Loader.dismiss()
    }

    private val observerValidation = Observer<Validation> {
        when(it) {
            is Validation.UserNameValidation -> etUserName.showError(getString(it.errorMessageCode), null,  textInputLayoutUser)

            is Validation.PasswordValidation -> etPassword.showError(getString(it.errorMessageCode), this,  textInputLayoutPassword)
        }
    }
}
