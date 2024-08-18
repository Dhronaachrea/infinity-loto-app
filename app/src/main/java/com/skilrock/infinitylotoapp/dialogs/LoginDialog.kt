package com.skilrock.infinitylotoapp.dialogs

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.response_data_class.LoginResponseData
import com.skilrock.infinitylotoapp.databinding.DialogLoginBinding
import com.skilrock.infinitylotoapp.ui.activity.ForgotPasswordActivity
import com.skilrock.infinitylotoapp.ui.activity.RegistrationActivity
import com.skilrock.infinitylotoapp.utility.*
import com.skilrock.infinitylotoapp.viewmodels.LoginViewModel


class LoginDialog(private val onSuccessfulLogin:() -> Unit) : DialogFragment() {

    private lateinit var btnForgotPassword: MaterialButton
    private lateinit var btnDialogLogin: MaterialButton
    private lateinit var btnRegister: MaterialButton
    private lateinit var etUserName: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilUserName: TextInputLayout
    private lateinit var viewModel: LoginViewModel
    private var lastClickTime: Long = 0
    private var clickGap = 600

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        val binding: DialogLoginBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_login, null, false)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.viewModel = viewModel

        btnForgotPassword   = binding.root.findViewById(R.id.btnDialogForgotPassword)
        btnDialogLogin      = binding.root.findViewById(R.id.btnDialogLogin)
        btnRegister         = binding.root.findViewById(R.id.btnDialogRegister)
        etUserName          = binding.root.findViewById(R.id.etUserName)
        etPassword          = binding.root.findViewById(R.id.etPassword)
        tilPassword         = binding.root.findViewById(R.id.textInputLayoutPassword)
        tilUserName         = binding.root.findViewById(R.id.textInputLayoutUser)

        viewModel.liveDataLoader.observe(this, observerLoader)
        viewModel.liveDataVibrator.observe(this, observerVibrator)
        viewModel.liveDataValidation.observe(this, observerValidation)
        viewModel.liveDataLoginStatus.observe(this, observerLoginStatus)

        dialog.setContentView(binding.root)

        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setClickListeners()
    }

    override fun onStart() {
        super.onStart()

        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT

        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 80)
        dialog?.window?.setBackgroundDrawable(inset)

        dialog?.window?.setLayout(width, height)
    }

    private fun setClickListeners() {
        btnForgotPassword.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < clickGap) {
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()

            activity?.let {  act ->
                vibrate(act)
                act.startActivity(Intent(act, ForgotPasswordActivity::class.java))
            }
        }

        btnRegister.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < clickGap) {
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()

            activity?.let {  act ->
                vibrate(act)
                act.startActivity(Intent(act, RegistrationActivity::class.java))
            }
        }

        etPassword.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    btnDialogLogin.hideKeyboard()
                    context?.let { cxt ->
                        viewModel.onLoginButtonClick(SharedPrefUtils.getFcmToken(cxt))
                    }
                    true
                }
                else -> false
            }
        }

        btnDialogLogin.setOnClickListener {
            context?.let { cxt ->
                viewModel.onLoginButtonClick(SharedPrefUtils.getFcmToken(cxt))
            }
        }

    }

    private val observerVibrator = Observer<String> { context?.let { vibrate(it) }}

    private val observerLoginStatus = Observer<ResponseStatus<LoginResponseData>> {
        when(it) {
            is ResponseStatus.Success -> {
                activity?.let { act ->
                    PlayerInfo.setLoginData(act, it.response)
                    onSuccessfulLogin()
                    dismiss()
                }
            }

            is ResponseStatus.Error -> {
                activity?.let { act ->
                    ErrorDialog(act, getString(R.string.login_error), it.errorMessage.getMsg(act)) {}.showDialog()
                    dismiss()
                }
            }

            is ResponseStatus.TechnicalError -> {
                activity?.let { act ->
                    ErrorDialog(act, getString(R.string.login_error), getString(it.errorMessageCode)) {}.showDialog()
                    dismiss()
                }
            }
        }
    }

    private val observerLoader = Observer<Boolean> {
        btnDialogLogin.hideKeyboard()
        if (it) {
            context?.let { cxt ->
                Loader.showLoader(cxt)
            }
        } else Loader.dismiss()
    }

    private val observerValidation = Observer<LoginViewModel.Validation> {
        when(it) {
            is LoginViewModel.Validation.UserNameValidation -> etUserName.showError(getString(it.errorMessageCode), null,  tilUserName)

            is LoginViewModel.Validation.PasswordValidation -> {
                context?.let { cxt ->
                    etPassword.showError(getString(it.errorMessageCode), cxt,  tilPassword)
                }
            }
        }
    }

}