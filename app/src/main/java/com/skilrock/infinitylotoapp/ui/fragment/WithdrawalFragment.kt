package com.skilrock.infinitylotoapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.adapter.DepositPendingAdapter
import com.skilrock.infinitylotoapp.adapter.DepositWithdrawalAdapter
import com.skilrock.infinitylotoapp.data_class.request_data_class.WithdrawalRequestData
import com.skilrock.infinitylotoapp.data_class.response_data_class.BalanceResponseData
import com.skilrock.infinitylotoapp.data_class.response_data_class.DepositPendingResponseData
import com.skilrock.infinitylotoapp.data_class.response_data_class.DepositUiResponseData
import com.skilrock.infinitylotoapp.data_class.response_data_class.WithdrawResponseData
import com.skilrock.infinitylotoapp.databinding.FragmentWithdrawalBinding
import com.skilrock.infinitylotoapp.dialogs.ErrorDialog
import com.skilrock.infinitylotoapp.dialogs.SuccessDialog
import com.skilrock.infinitylotoapp.ui.activity.MyWalletActivity
import com.skilrock.infinitylotoapp.utility.*
import com.skilrock.infinitylotoapp.viewmodels.WithdrawalViewModel
import kotlinx.android.synthetic.main.fragment_withdrawal.*
import org.json.JSONObject

class WithdrawalFragment : Fragment() {

    private lateinit var viewModel: WithdrawalViewModel
    private lateinit var binding: FragmentWithdrawalBinding
    private lateinit var adapterUI: DepositWithdrawalAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_withdrawal, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setViewModel()
        viewModel.callUiApi()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this).get(WithdrawalViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(viewLifecycleOwner, observerVibrator)
        viewModel.liveDataLoader.observe(viewLifecycleOwner, observerLoader)
        viewModel.liveDataWithdrawalUi.observe(viewLifecycleOwner, observerWithdrawalUi)
        viewModel.liveDataWithdrawalPendingResponse.observe(viewLifecycleOwner, observerWithdrawalPendingResponse)
        viewModel.liveDataWithdrawResponse.observe(viewLifecycleOwner, observerWithdrawResponse)
        viewModel.liveDataBalanceStatus.observe(viewLifecycleOwner, observerBalanceStatus)
    }

    private val observerVibrator = Observer<String> { context?.let { vibrate(it) } }

    private val observerLoader = Observer<Boolean> {
        context?.let { cxt ->
            if (it) Loader.showLoader(cxt) else Loader.dismiss()
        }
    }

    private val observerWithdrawalUi = Observer<ResponseStatus<JSONObject>> { responseStatus ->

        viewModel.callWithdrawalPendingApi()

        when (responseStatus) {
            is ResponseStatus.Success -> {
                prepareDataForRecyclerView(responseStatus.response)
            }

            is ResponseStatus.Error -> activity?.let { act ->
                ErrorDialog(
                    act, getString(R.string.withdrawal_error),
                    responseStatus.errorMessage.getMsg(act), responseStatus.errorCode
                ) {}.showDialog()
            }

            is ResponseStatus.TechnicalError -> activity?.let { act ->
                ErrorDialog(
                    act,
                    getString(R.string.withdrawal_error),
                    getString(responseStatus.errorMessageCode)
                ) {}.showDialog()
            }

        }
    }

    private val observerWithdrawalPendingResponse = Observer<ResponseStatus<DepositPendingResponseData>> {
        when (it) {
            is ResponseStatus.Success -> {
                val response: DepositPendingResponseData = it.response
                response.txnList?.let { list ->

                    if (list.size > 0) {
                        llPendingWithdrawal.visibility = View.VISIBLE
                        rvWithdrawalPending.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = DepositPendingAdapter(list)
                        }
                    }
                }
            }

            is ResponseStatus.Error -> activity?.let { act ->
                if (it.errorCode != 111)
                    act.redToast(it.errorMessage.getMsg(act))
            }

            is ResponseStatus.TechnicalError -> activity?.redToast(getString(it.errorMessageCode))

        }
    }

    private fun prepareDataForRecyclerView(jsonResponse: JSONObject) {
        val uiData = DepositUiResponseData()

        val payTypeMapKeys = jsonResponse.optJSONObject("payTypeMap")?.keys()
        val payTypeMapObject = jsonResponse.optJSONObject("payTypeMap")

        payTypeMapKeys?.let { payTypeMapKeysNonNull ->
            payTypeMapObject?.let { payTypeMapObjectNonNull ->
                val listPaymentTypes = ArrayList<DepositUiResponseData.PayTypeMap>()

                for (key in payTypeMapKeysNonNull) {
                    val payTypeObject = payTypeMapObjectNonNull.optJSONObject(key.toString())

                    payTypeObject?.let { payTypeObjectNonNull ->
                        val subTypeMapObject = payTypeObjectNonNull.optJSONObject("subTypeMap")

                        subTypeMapObject?.let { subTypeMapObjectNonNull ->
                            val subTypeKeys = subTypeMapObjectNonNull.keys()

                            while (subTypeKeys.hasNext()) {
                                val subTypeKey = subTypeKeys.next()

                                val depositUiData = DepositUiResponseData().PayTypeMap()
                                depositUiData.arrayId = key
                                depositUiData.payTypeId = payTypeObjectNonNull.optInt("payTypeId")
                                depositUiData.payTypeCode = payTypeObjectNonNull.optString("payTypeCode")
                                depositUiData.payTypeDispCode = payTypeObjectNonNull.optString("payTypeDispCode")

                                depositUiData.uiOrder = payTypeObjectNonNull.optInt("uiOrder")
                                depositUiData.minValue = payTypeObjectNonNull.optDouble("minValue")
                                depositUiData.maxValue = payTypeObjectNonNull.optDouble("maxValue")

                                depositUiData.subTypeCode = subTypeKey
                                depositUiData.subTypeValue = subTypeMapObjectNonNull.optString(subTypeKey)

                                val currencyMapObject = payTypeObjectNonNull.optJSONObject("currencyMap")
                                val listCurrency = ArrayList<DepositUiResponseData.PayTypeMap.CurrencyMap>()
                                currencyMapObject?.let { currencyMapObjectNonNull ->
                                    val currencyKeys = currencyMapObjectNonNull.keys()
                                    setCurrencyDetails(currencyKeys, currencyMapObjectNonNull, listCurrency)
                                }

                                depositUiData.currencyMap = listCurrency

                                val paymentAccountsKeys = jsonResponse.optJSONObject("paymentAccounts")?.keys()
                                val paymentAccountsObject = jsonResponse.optJSONObject("paymentAccounts")
                                val listAccounts = ArrayList<DepositUiResponseData.PayTypeMap.AccountDetail>()
                                paymentAccountsKeys?.let { paymentAccountsKeysNonNull ->
                                    paymentAccountsObject?.let { paymentAccountsObjectNonNull ->
                                        setAccountDetails(paymentAccountsKeysNonNull, paymentAccountsObjectNonNull, listAccounts, depositUiData)
                                    }
                                }

                                val payAccReqMap = payTypeObjectNonNull.optJSONObject("payAccReqMap")

                                payAccReqMap?.let { payAccReqMapNonNull ->
                                    val status = payAccReqMapNonNull.optString(subTypeKey)
                                    depositUiData.showAddNewAccBtn = status.equals("YES", true)
                                } ?: run {
                                    depositUiData.showAddNewAccBtn = false
                                }

                                val payAccKycType = payTypeObjectNonNull.optJSONObject("payAccKycType")

                                payAccKycType?.let { payAccKycTypeNonNull ->
                                    val type = payAccKycTypeNonNull.optString(subTypeKey)
                                    depositUiData.kycType = type
                                } ?: run {
                                    depositUiData.kycType = ""
                                }

                                depositUiData.accountDetail = listAccounts
                                listPaymentTypes.add(depositUiData)
                            }
                        }
                    }
                }

                setRecyclerView(uiData, listPaymentTypes)
            }
        } ?: run {
            showNoDataAvailable()
        }
    }

    private fun setCurrencyDetails(currencyKeys: MutableIterator<String>, currencyMapObjectNonNull: JSONObject, listCurrency: ArrayList<DepositUiResponseData.PayTypeMap.CurrencyMap>) {
        while (currencyKeys.hasNext()) {
            val currencyKey = currencyKeys.next()
            val depositUiDataCurrency = DepositUiResponseData().PayTypeMap().CurrencyMap()
            depositUiDataCurrency.code = currencyKey
            depositUiDataCurrency.value = currencyMapObjectNonNull.optString(currencyKey)
            listCurrency.add(depositUiDataCurrency)
        }
    }

    private fun setAccountDetails(paymentAccountsKeysNonNull: MutableIterator<String>, paymentAccountsObjectNonNull: JSONObject,
                                  listAccounts: ArrayList<DepositUiResponseData.PayTypeMap.AccountDetail>,
                                  depositUiData: DepositUiResponseData.PayTypeMap) {
        while (paymentAccountsKeysNonNull.hasNext()) {
            val paymentAccountsKey = paymentAccountsKeysNonNull.next()
            val paymentAccountObject = paymentAccountsObjectNonNull.optJSONObject(paymentAccountsKey)
            paymentAccountObject?.let { paymentAccountObjectNonNull ->
                try {
                    val subTypeId = paymentAccountObjectNonNull.optInt("subTypeId")
                    val subTypeCode = depositUiData.subTypeCode.toInt()

                    if (subTypeId == subTypeCode) {
                        val account = DepositUiResponseData().PayTypeMap().AccountDetail()
                        account.accNum = paymentAccountObjectNonNull.optString("accNum")
                        account.paymentAccId = paymentAccountObjectNonNull.optInt("paymentAccId")
                        listAccounts.add(account)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    private fun setRecyclerView(uiData: DepositUiResponseData, listPaymentTypes: java.util.ArrayList<DepositUiResponseData.PayTypeMap>) {
        if (listPaymentTypes.size > 0) {
            uiData.listPayTypeMap = listPaymentTypes

            Log.w("log", "Withdrawal UI Data Class: ${uiData.listPayTypeMap}")
            uiData.listPayTypeMap?.let { list ->
                context?.let { cxt ->
                    adapterUI = DepositWithdrawalAdapter(cxt, requireActivity(), list,
                        getString(R.string.withdraw_caps), "WITHDRAWAL",
                        ::onAccountAdded, {}, ::onWithdrawClick)

                    rvWithdrawal.layoutManager = LinearLayoutManager(context)
                    rvWithdrawal.adapter = adapterUI
                }

            }
        }
        else
            showNoDataAvailable()
    }

    private fun onAccountAdded() {
        viewModel.callUiApi()
    }

    private fun onWithdrawClick(withdrawalRequestData: WithdrawalRequestData) {
        viewModel.callWithdrawApi(withdrawalRequestData)
    }

    private fun showNoDataAvailable() {
        activity?.let { act -> ErrorDialog(
            act, getString(R.string.withdrawal), getString(R.string.no_data_available)
        ) { }.showDialog() }
    }

    private val observerWithdrawResponse = Observer<ResponseStatus<WithdrawResponseData>> {
        when (it) {
            is ResponseStatus.Success -> { context?.let { cxt ->
                    SuccessDialog(cxt, getString(R.string.withdrawal),
                        it.response.respMsg ?: getString(R.string.withdrawn_successfully))
                    { viewModel.callBalanceApi() }.showDialog()
                }
            }

            is ResponseStatus.Error -> activity?.let { act -> ErrorDialog(
                act, getString(R.string.withdrawal_error), it.errorMessage.getMsg(act), it.errorCode
            ) {}.showDialog() }

            is ResponseStatus.TechnicalError -> activity?.let { act -> ErrorDialog(
                act, getString(R.string.withdrawal_error), getString(it.errorMessageCode)
            ) {}.showDialog() }

        }
    }

    private val observerBalanceStatus = Observer<ResponseStatus<BalanceResponseData>> {
        when (it) {
            is ResponseStatus.Success -> {
                activity?.let { act ->
                    //cxt.greenToast(getString(R.string.balance_refreshed))
                    it.response.wallet?.let { wallet ->
                        (act as MyWalletActivity).setToolbarBalance(wallet.totalBalance.toString())
                        PlayerInfo.setBalance(act, wallet)

                    }
                    viewModel.callUiApi()
                }
            }

            is ResponseStatus.Error -> {
                viewModel.callUiApi()
            }

            is ResponseStatus.TechnicalError -> {
                viewModel.callUiApi()
            }
        }
    }

}
