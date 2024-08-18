package com.skilrock.infinitylotoapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.adapter.MyTransactionAdapter
import com.skilrock.infinitylotoapp.data_class.response_data_class.MyTransactionResponseData
import com.skilrock.infinitylotoapp.databinding.FragmentMyTransationBinding
import com.skilrock.infinitylotoapp.dialogs.ErrorDialog
import com.skilrock.infinitylotoapp.utility.*
import com.skilrock.infinitylotoapp.viewmodels.MyTransactionsViewModel
import kotlinx.android.synthetic.main.fragment_my_transation.*

class MyTransactionFragment : BaseFragment() {

    private lateinit var viewModel: MyTransactionsViewModel
    private lateinit var binding : FragmentMyTransationBinding
    private lateinit var adapter: MyTransactionAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_transation, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setToolbarElements(getText(R.string.my_transactions).toString(), View.GONE, false)
        setUpViewModel()
        setOnClickListeners()
        setUpRecyclerView()
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(this).get(MyTransactionsViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.liveDataLoader.observe(viewLifecycleOwner, observerLoader)
        viewModel.liveDataVibrator.observe(viewLifecycleOwner, observerVibrator)
        viewModel.liveDataTxnType.observe(viewLifecycleOwner, observerTxnType)
        viewModel.liveDataResponse.observe(viewLifecycleOwner, observerLoginStatus)
    }

    private fun setUpRecyclerView() {
        rvReport.layoutManager = LinearLayoutManager(context)
        rvReport.setHasFixedSize(true)
        adapter = MyTransactionAdapter()
        rvReport.adapter = adapter
    }

    private val observerVibrator = Observer<String> { context?.let { vibrate(it) } }

    private val observerLoader = Observer<Boolean> {
        if (it) Loader.showLoader(context) else Loader.dismiss()
    }

    private fun setOnClickListeners() {
        containerFromDate.setOnClickListener { context?.let { openStartDateDialog(it, tvStartDate, tvEndDate) } }

        containerEndDate.setOnClickListener { context?.let { openEndDateDialog(it, tvStartDate, tvEndDate) } }
    }

    private val observerLoginStatus = Observer<ResponseStatus<MyTransactionResponseData>> {
        adapter.clearData()
        when(it) {
            is ResponseStatus.Success -> {
                val txnList: ArrayList<MyTransactionResponseData.Txn?>? = it.response.txnList
                val isListEmpty: Boolean = it.response.txnList?.isEmpty() ?: true
                if (isListEmpty) {
                    activity?.let { act ->
                        ErrorDialog(act, getString(R.string.my_transaction_error), getString(R.string.no_transactions_found)) {}.showDialog()
                    }
                }
                else
                    adapter.setTxnList(txnList)
            }

            is ResponseStatus.Error -> {
                activity?.let { act ->
                    ErrorDialog(act, getString(R.string.my_transaction_error), it.errorMessage.getMsg(act), it.errorCode) {}.showDialog()
                }
            }

            is ResponseStatus.TechnicalError -> {
                activity?.let { act ->
                    ErrorDialog(act, getString(R.string.my_transaction_error), getString(it.errorMessageCode)) {}.showDialog()
                }
            }
        }
    }

    private val observerTxnType = Observer<String> { txnType ->
        resetAllTxnTyeTextView(txnType)
    }

    private fun resetAllTxnTyeTextView(txnType: String) {
        context?.let { cxt ->
            tvAll.setTextColor(ContextCompat.getColor(cxt, R.color.colorPrimary))
            tvWager.setTextColor(ContextCompat.getColor(cxt, R.color.colorPrimary))
            tvDeposit.setTextColor(ContextCompat.getColor(cxt, R.color.colorPrimary))
            tvWithdrawal.setTextColor(ContextCompat.getColor(cxt, R.color.colorPrimary))
            tvWinning.setTextColor(ContextCompat.getColor(cxt, R.color.colorPrimary))
            tvCashIn.setTextColor(ContextCompat.getColor(cxt, R.color.colorPrimary))
            tvCashOut.setTextColor(ContextCompat.getColor(cxt, R.color.colorPrimary))

            tvAll.setBackgroundResource(R.drawable.grey_rounded_bg)
            tvWager.setBackgroundResource(R.drawable.grey_rounded_bg)
            tvDeposit.setBackgroundResource(R.drawable.grey_rounded_bg)
            tvWithdrawal.setBackgroundResource(R.drawable.grey_rounded_bg)
            tvWinning.setBackgroundResource(R.drawable.grey_rounded_bg)
            tvCashIn.setBackgroundResource(R.drawable.grey_rounded_bg)
            tvCashOut.setBackgroundResource(R.drawable.grey_rounded_bg)

            when(txnType) {
                viewModel.txnAll -> {
                    tvAll.setTextColor(ContextCompat.getColor(cxt, R.color.colorWhite))
                    tvAll.setBackgroundResource(R.drawable.blue_rounded_bg)
                }

                viewModel.txnWager -> {
                    tvWager.setTextColor(ContextCompat.getColor(cxt, R.color.colorWhite))
                    tvWager.setBackgroundResource(R.drawable.blue_rounded_bg)
                }

                viewModel.txnDeposit -> {
                    tvDeposit.setTextColor(ContextCompat.getColor(cxt, R.color.colorWhite))
                    tvDeposit.setBackgroundResource(R.drawable.blue_rounded_bg)
                }

                viewModel.txnWithdrawal -> {
                    tvWithdrawal.setTextColor(ContextCompat.getColor(cxt, R.color.colorWhite))
                    tvWithdrawal.setBackgroundResource(R.drawable.blue_rounded_bg)
                }

                viewModel.txnWinning -> {
                    tvWinning.setTextColor(ContextCompat.getColor(cxt, R.color.colorWhite))
                    tvWinning.setBackgroundResource(R.drawable.blue_rounded_bg)
                }

                viewModel.txnTransferIn -> {
                    tvCashIn.setTextColor(ContextCompat.getColor(cxt, R.color.colorWhite))
                    tvCashIn.setBackgroundResource(R.drawable.blue_rounded_bg)
                }

                viewModel.txnTransferOut -> {
                    tvCashOut.setTextColor(ContextCompat.getColor(cxt, R.color.colorWhite))
                    tvCashOut.setBackgroundResource(R.drawable.blue_rounded_bg)
                }
            }
        }
    }

}
