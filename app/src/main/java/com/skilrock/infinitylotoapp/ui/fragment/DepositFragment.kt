package com.skilrock.infinitylotoapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.adapter.DepositPendingAdapter
import com.skilrock.infinitylotoapp.adapter.DepositWithdrawalAdapter
import com.skilrock.infinitylotoapp.data_class.response_data_class.BalanceResponseData
import com.skilrock.infinitylotoapp.data_class.response_data_class.DepositPendingResponseData
import com.skilrock.infinitylotoapp.data_class.response_data_class.DepositUiResponseData
import com.skilrock.infinitylotoapp.databinding.FragmentDepositBinding
import com.skilrock.infinitylotoapp.dialogs.ErrorDialog
import com.skilrock.infinitylotoapp.dialogs.SuccessDialog
import com.skilrock.infinitylotoapp.ui.activity.DepositPaymentActivity
import com.skilrock.infinitylotoapp.ui.activity.MyWalletActivity
import com.skilrock.infinitylotoapp.utility.*
import com.skilrock.infinitylotoapp.viewmodels.DepositViewModel
import kotlinx.android.synthetic.main.fragment_deposit.*
import org.json.JSONObject


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class DepositFragment : Fragment() {

    private lateinit var viewModel: DepositViewModel
    private lateinit var binding : FragmentDepositBinding
    private val _requestCode = 111
    private val _resultCode = 222

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_deposit, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setViewModel()
        viewModel.callUiApi()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this).get(DepositViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(viewLifecycleOwner, observerVibrator)
        viewModel.liveDataLoader.observe(viewLifecycleOwner, observerLoader)
        viewModel.liveDataDepositUi.observe(viewLifecycleOwner, observerDepositUi)
        viewModel.liveDataBalanceStatus.observe(viewLifecycleOwner, observerBalanceStatus)
        viewModel.liveDataPendingResponse.observe(viewLifecycleOwner, observerDepositPendingResponse)
    }

    private val observerVibrator = Observer<String> { context?.let { vibrate(it) } }

    private val observerLoader = Observer<Boolean> {
        context?.let { cxt ->
            if (it) Loader.showLoader(cxt) else Loader.dismiss()
        }
    }

    private val observerDepositUi = Observer<ResponseStatus<JSONObject>> { responseStatus ->

        viewModel.callDepositPendingApi()

        when (responseStatus) {
            is ResponseStatus.Success -> {
                prepareDataForRecyclerView(responseStatus.response)
            }

            is ResponseStatus.Error -> activity?.let { act ->
                ErrorDialog(
                    act, getString(R.string.deposit_error),
                    responseStatus.errorMessage.getMsg(act), responseStatus.errorCode
                ) {}.showDialog()
            }

            is ResponseStatus.TechnicalError -> activity?.let { act ->
                ErrorDialog(
                    act,
                    getString(R.string.deposit_error),
                    getString(responseStatus.errorMessageCode)
                ) {}.showDialog()
            }

        }
    }

    private val observerDepositPendingResponse = Observer<ResponseStatus<DepositPendingResponseData>> {
        when (it) {
            is ResponseStatus.Success -> {
                val response: DepositPendingResponseData = it.response
                response.txnList?.let { list ->

                    if (list.size > 0) {
                        llPendingDeposit.visibility = View.VISIBLE
                        rvDepositPending.apply {
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
                                    setCurrencyDetails(currencyMapObjectNonNull, listCurrency)
                                }

                                depositUiData.currencyMap = listCurrency

                                val paymentAccountsKeys = jsonResponse.optJSONObject("paymentAccounts")?.keys()
                                val paymentAccountsObject = jsonResponse.optJSONObject("paymentAccounts")
                                val listAccounts = ArrayList<DepositUiResponseData.PayTypeMap.AccountDetail>()
                                paymentAccountsKeys?.let { paymentAccountsKeysNonNull ->
                                    paymentAccountsObject?.let { paymentAccountsObjectNonNull ->
                                        setPaymentAccounts(paymentAccountsKeysNonNull, paymentAccountsObjectNonNull,
                                            depositUiData, listAccounts)
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

                setupRecyclerView(uiData, listPaymentTypes)
            }
        } ?: run {
            showNoDataAvailable()
        }
    }

    private fun setCurrencyDetails(currencyMapObjectNonNull: JSONObject, listCurrency: ArrayList<DepositUiResponseData.PayTypeMap.CurrencyMap>) {
        val currencyKeys = currencyMapObjectNonNull.keys()
        while (currencyKeys.hasNext()) {
            val currencyKey = currencyKeys.next()
            val depositUiDataCurrency = DepositUiResponseData().PayTypeMap().CurrencyMap()
            depositUiDataCurrency.code = currencyKey
            depositUiDataCurrency.value = currencyMapObjectNonNull.optString(currencyKey)
            listCurrency.add(depositUiDataCurrency)
        }
    }

    private fun setPaymentAccounts(paymentAccountsKeysNonNull: MutableIterator<String>, paymentAccountsObjectNonNull: JSONObject,
                                   depositUiData: DepositUiResponseData.PayTypeMap, listAccounts: ArrayList<DepositUiResponseData.PayTypeMap.AccountDetail>) {
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
                    } else {
                        Log.e("log", "subTypeId != subTypeCode")
                    }
                } catch (e: Exception) {
                    Log.e("log", e.message ?: "ERROR")
                }
            }
        }
    }

    private fun setupRecyclerView(uiData: DepositUiResponseData, listPaymentTypes: ArrayList<DepositUiResponseData.PayTypeMap>) {
        if (listPaymentTypes.size > 0) {
            uiData.listPayTypeMap = listPaymentTypes

            Log.w("log", "Deposit UI Data Class: ${uiData.listPayTypeMap}")
            uiData.listPayTypeMap?.let { list ->
                rvDeposit.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = DepositWithdrawalAdapter(context,
                        requireActivity(),
                        list,
                        getString(R.string.deposit_caps),
                        "DEPOSIT",
                        ::onAccountAdded,
                        ::onDepositClick
                    ) { }
                }
            }
        }
        else
            showNoDataAvailable()
    }

    private fun onAccountAdded() {
        viewModel.callUiApi()
    }

    private fun onDepositClick(requestData: String) {
        activity?.let { act ->
            Log.d("log", "REQUEST DATA: $requestData")
            val intent = Intent(act, DepositPaymentActivity::class.java)
            intent.putExtra("requestData", requestData)
            startActivityForResult(intent, _requestCode)
        }
    }

    private fun showNoDataAvailable() {
        activity?.let { act -> ErrorDialog(
            act, getString(R.string.deposit), getString(R.string.no_data_available)
        ) { }.showDialog() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == _requestCode && resultCode == _resultCode) {
            intent?.let {
                val responseData: String? = it.getStringExtra("responseData")
                responseData?.let { response ->
                    activity?.let { act -> handleResponse(response, act) }
                }
            }
        }
    }

    private fun handleResponse(response: String, act: FragmentActivity) {
        try {
            val jsonObject = JSONObject(response)
            val errorCode: Int = jsonObject.getInt("errorCode")
            if (errorCode == 0) {
                val respMsg     = jsonObject.optString("respMsg")
                val txnId       = jsonObject.optString("txnId")

                var successMsg = respMsg
                if (txnId.trim().isNotBlank())
                    successMsg = "$successMsg\n\nTxn Id: $txnId"

                SuccessDialog(act, getString(R.string.deposit),
                    successMsg ?: getString(R.string.withdrawn_successfully))
                { viewModel.callBalanceApi() }.showDialog()
            } else {
                ErrorDialog(act, getString(R.string.deposit_error),
                    jsonObject.optString("errorMsg"), errorCode
                ) { viewModel.callBalanceApi() }.showDialog()
            }
        } catch (e: Exception) {
            Log.e("log", e.message ?: "ERROR")
            ErrorDialog(act, getString(R.string.deposit_error), getString(R.string.some_internal_error)
            ) { viewModel.callBalanceApi() }.showDialog()
        }
    }

    private val observerBalanceStatus = Observer<ResponseStatus<BalanceResponseData>> {
        when (it) {
            is ResponseStatus.Success -> {
                activity?.let { act ->
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
