package com.skilrock.infinitylotoapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.response_data_class.ChangePasswordResponseData
import com.skilrock.infinitylotoapp.databinding.ChangePasswordFragmentBinding
import com.skilrock.infinitylotoapp.dialogs.ErrorDialog
import com.skilrock.infinitylotoapp.dialogs.SuccessDialog
import com.skilrock.infinitylotoapp.utility.*
import com.skilrock.infinitylotoapp.viewmodels.ChangePasswordViewModel
import kotlinx.android.synthetic.main.change_password_fragment.*

class ChangePasswordFragment : BaseFragment() {

    private lateinit var viewModel: ChangePasswordViewModel
    private lateinit var binding : ChangePasswordFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.change_password_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbarElements(getText(R.string.change_password).toString(), View.GONE, false)
        setUpViewModel()
        keyboardEnterFunctionality()
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(this).get(ChangePasswordViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.liveDataLoader.observe(viewLifecycleOwner, observerLoader)
        viewModel.liveDataVibrator.observe(viewLifecycleOwner, observerVibrator)
        viewModel.liveDataValidation.observe(viewLifecycleOwner, observerValidation)
        viewModel.liveDataResponseStatus.observe(viewLifecycleOwner, observerResponseStatus)
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
            is ResponseStatus.Success -> context?.let { cxt -> SuccessDialog(
                cxt, getString(R.string.change_password), getString(R.string.password_changed_successfully)) {
                activity?.let { act -> performLogoutCleanUp(act) }
            }.showDialog() }

            is ResponseStatus.Error -> activity?.let { act ->
                ErrorDialog(act, getString(R.string.change_password_error), it.errorMessage.getMsg(act), it.errorCode) {}.showDialog()
            }

            is ResponseStatus.TechnicalError -> activity?.let { act -> ErrorDialog(
                act, getString(R.string.change_password_error), getString(it.errorMessageCode)
            ) {}.showDialog() }

        }
    }

    private val observerVibrator = Observer<String> { context?.let { vibrate(it) } }

    private val observerLoader = Observer<Boolean> {
        context?.let { cxt -> if (it) Loader.showLoader(cxt) else Loader.dismiss() }
    }

    private val observerValidation = Observer<ChangePasswordValidation> {
        when (it) {
            is ChangePasswordValidation.OldPasswordValidation -> context?.let { cxt ->
                etOldPassword.showError(getString(it.errorMessageCode), cxt, tilOldPassword)
            }
            is ChangePasswordValidation.NewPasswordValidation -> context?.let { cxt ->
                etNewPassword.showError(getString(it.errorMessageCode), cxt, tilNewPassword)
            }
            is ChangePasswordValidation.ConfirmPasswordValidation -> context?.let { cxt ->
                etConfirmPassword.showError(getString(it.errorMessageCode), cxt, tilNewPassword)
            }
        }
    }

}
