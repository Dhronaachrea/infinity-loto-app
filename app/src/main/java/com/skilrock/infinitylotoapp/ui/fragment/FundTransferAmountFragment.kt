package com.skilrock.infinitylotoapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.response_data_class.FundTransferFromWalletResponseData
import com.skilrock.infinitylotoapp.databinding.FundTransferAmountFragmentBinding
import com.skilrock.infinitylotoapp.dialogs.ErrorDialog
import com.skilrock.infinitylotoapp.dialogs.SuccessDialog
import com.skilrock.infinitylotoapp.utility.*
import com.skilrock.infinitylotoapp.viewmodels.FundTransferAmountViewModel
import kotlinx.android.synthetic.main.fund_transfer_amount_fragment.*

class FundTransferAmountFragment : BaseFragment() {

    private lateinit var viewModel: FundTransferAmountViewModel
    private lateinit var binding : FundTransferAmountFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fund_transfer_amount_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbarElements(getText(R.string.fund_transfer).toString(), View.GONE, false)
        setUpViewModel()
        setMachineId()
        keyboardEnterFunctionality()
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(this).get(FundTransferAmountViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.liveDataLoader.observe(viewLifecycleOwner, observerLoader)
        viewModel.liveDataVibrator.observe(viewLifecycleOwner, observerVibrator)
        viewModel.liveDataValidateAmount.observe(viewLifecycleOwner, observerValidation)
        viewModel.liveDataFundTransferStatus.observe(viewLifecycleOwner, observerResponseStatus)
    }

    private fun setMachineId() {
        arguments?.let {
            tvMachineId.text        = FundTransferAmountFragmentArgs.fromBundle(it).machineId
            viewModel.machineId     = FundTransferAmountFragmentArgs.fromBundle(it).machineId
            viewModel.merchantId    = FundTransferAmountFragmentArgs.fromBundle(it).merchantId
            viewModel.ipAddress     = FundTransferAmountFragmentArgs.fromBundle(it).ipAddress
        }
    }

    private fun keyboardEnterFunctionality() {
        etAmount.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    btnSubmit.hideKeyboard()
                    viewModel.onPayClick()
                    true
                }
                else -> false
            }
        }
    }

    private val observerVibrator = Observer<String> { context?.let { vibrate(it) } }

    private val observerLoader = Observer<Boolean> {
        context?.let { cxt -> if (it) Loader.showLoader(cxt) else Loader.dismiss() }
    }

    private val observerValidation = Observer<Int> {
        etAmount.showError(getString(it), null,  tilAmount)
    }

    private val observerResponseStatus = Observer<ResponseStatus<FundTransferFromWalletResponseData>> {
        when (it) {
            is ResponseStatus.Success -> { context?.let { cxt ->
                    it.response.totalBal?.let { totalBalance ->
                        updateToolbarBalance(totalBalance.toString())
                        PlayerInfo.setTotalBalance(cxt, totalBalance)
                        val msg = "<b>${BuildConfig.CURRENCY_SYMBOL} ${etAmount.text.toString().trim()}</b> fund transfer initiated successfully, with Transaction Id <b>${it.response.txnId}</b>"
                        SuccessDialog(cxt, getString(R.string.fund_transfer), msg, true) {
                            switchToHome()
                        }.showDialog()
                    }
                }
            }

            is ResponseStatus.Error -> activity?.let { act -> ErrorDialog(
                act, getString(R.string.fund_transfer_error), it.errorMessage.getMsg(act), it.errorCode
            ) {}.showDialog() }

            is ResponseStatus.TechnicalError -> activity?.let { act -> ErrorDialog(
                act, getString(R.string.fund_transfer_error), getString(it.errorMessageCode)
            ) {}.showDialog() }

        }
    }

}
