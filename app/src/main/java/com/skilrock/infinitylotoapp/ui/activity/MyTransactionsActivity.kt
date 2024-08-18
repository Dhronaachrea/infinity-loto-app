package com.skilrock.infinitylotoapp.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.adapter.MyTransactionAdapter
import com.skilrock.infinitylotoapp.data_class.response_data_class.MyTransactionResponseData
import com.skilrock.infinitylotoapp.databinding.ActivityMyTransactionsBinding
import com.skilrock.infinitylotoapp.dialogs.ErrorDialog
import com.skilrock.infinitylotoapp.dialogs.LoginDialog
import com.skilrock.infinitylotoapp.utility.*
import com.skilrock.infinitylotoapp.viewmodels.MyTransactionsViewModel
import kotlinx.android.synthetic.main.activity_home.toolbar
import kotlinx.android.synthetic.main.activity_my_transactions.*
import kotlinx.android.synthetic.main.toolbar_without_login_option.*

class MyTransactionsActivity : AppCompatActivity() {

    private lateinit var viewModel: MyTransactionsViewModel
    private lateinit var adapter: MyTransactionAdapter
    private var isFirstTimeResponse = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAppTheme()
        setSupportActionBar(toolbar)
        setDataBindingAndViewModel()
        setOnClickListeners()
        setUpRecyclerView()
        viewModel.onTxnTypeClick(viewModel.txnAll)

    }

    private fun setAppTheme() {
        if (SharedPrefUtils.getAppTheme(this) == THEME_DARK)
            setTheme(R.style.DarkTheme)
        else
            setTheme(R.style.LightTheme)
    }

    private fun setDataBindingAndViewModel() {
        val binding : ActivityMyTransactionsBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_my_transactions)

        viewModel = ViewModelProvider(this).get(MyTransactionsViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataLoader.observe(this, observerLoader)
        viewModel.liveDataVibrator.observe(this, observerVibrator)
        viewModel.liveDataTxnType.observe(this, observerTxnType)
        viewModel.liveDataResponse.observe(this, observerMyTransactions)
    }

    private fun setUpRecyclerView() {
        rvReport.layoutManager = LinearLayoutManager(this)
        rvReport.setHasFixedSize(true)
        adapter = MyTransactionAdapter()
        rvReport.adapter = adapter
    }

    private val observerVibrator = Observer<String> { vibrate(this) }

    private val observerLoader = Observer<Boolean> {
        if (it) Loader.showLoader(this) else Loader.dismiss()
    }

    private fun setOnClickListeners() {
        ivBack.setOnClickListener { finish() }

        containerFromDate.setOnClickListener { openStartDateDialog(this, tvStartDate, tvEndDate) }

        containerEndDate.setOnClickListener { openEndDateDialog(this, tvStartDate, tvEndDate) }
    }

    private val observerMyTransactions = Observer<ResponseStatus<MyTransactionResponseData>> {
        adapter.clearData()
        when(it) {
            is ResponseStatus.Success -> {
                val txnList: ArrayList<MyTransactionResponseData.Txn?>? = it.response.txnList
                val isListEmpty: Boolean = it.response.txnList?.isEmpty() ?: true
                if (isListEmpty) {
                    if (!isFirstTimeResponse) {
                        ErrorDialog(
                            this, getString(R.string.my_transaction_error),
                            getString(R.string.no_transactions_found)
                        ) {}.showDialog()
                    }
                }
                else
                    adapter.setTxnList(txnList)

                isFirstTimeResponse = false
            }

            is ResponseStatus.Error -> {
                if (!isFirstTimeResponse) {
                    if (it.errorCode == SESSION_EXPIRE_CODE) {
                        redToast(it.errorMessage.getMsg(this))
                        openLoginDialog()
                    } else {
                        ErrorDialog(
                            this, getString(R.string.my_transaction_error),
                            it.errorMessage.getMsg(this), it.errorCode,
                            Intent(this, HomeKenyaActivity::class.java)
                        ) {}.showDialog()
                    }
                }

                isFirstTimeResponse = false
            }

            is ResponseStatus.TechnicalError -> {
                if (!isFirstTimeResponse) {
                    ErrorDialog(
                        this, getString(R.string.my_transaction_error),
                        getString(it.errorMessageCode)
                    ) {}.showDialog()
                }

                isFirstTimeResponse = false
            }
        }
    }

    private val observerTxnType = Observer<String> { txnType ->
        resetAllTxnTyeTextView(txnType)
    }

    private fun resetAllTxnTyeTextView(txnType: String) {
        tvAll.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        tvWager.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        tvDeposit.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        tvWithdrawal.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        tvWinning.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        tvCashIn.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        tvCashOut.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))

        tvAll.setBackgroundResource(R.drawable.grey_rounded_bg)
        tvWager.setBackgroundResource(R.drawable.grey_rounded_bg)
        tvDeposit.setBackgroundResource(R.drawable.grey_rounded_bg)
        tvWithdrawal.setBackgroundResource(R.drawable.grey_rounded_bg)
        tvWinning.setBackgroundResource(R.drawable.grey_rounded_bg)
        tvCashIn.setBackgroundResource(R.drawable.grey_rounded_bg)
        tvCashOut.setBackgroundResource(R.drawable.grey_rounded_bg)

        when(txnType) {
            viewModel.txnAll -> {
                tvAll.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                tvAll.setBackgroundResource(R.drawable.blue_rounded_bg)
            }

            viewModel.txnWager -> {
                tvWager.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                tvWager.setBackgroundResource(R.drawable.blue_rounded_bg)
            }

            viewModel.txnDeposit -> {
                tvDeposit.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                tvDeposit.setBackgroundResource(R.drawable.blue_rounded_bg)
            }

            viewModel.txnWithdrawal -> {
                tvWithdrawal.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                tvWithdrawal.setBackgroundResource(R.drawable.blue_rounded_bg)
            }

            viewModel.txnWinning -> {
                tvWinning.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                tvWinning.setBackgroundResource(R.drawable.blue_rounded_bg)
            }

            viewModel.txnTransferIn -> {
                tvCashIn.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                tvCashIn.setBackgroundResource(R.drawable.blue_rounded_bg)
            }

            viewModel.txnTransferOut -> {
                tvCashOut.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
                tvCashOut.setBackgroundResource(R.drawable.blue_rounded_bg)
            }
        }
    }

    private fun openLoginDialog() {
        val fm: FragmentManager = supportFragmentManager
        val loginDialog = LoginDialog(::onSuccessfulLogin)
        loginDialog.show(fm, "LoginDialog")
    }

    private fun onSuccessfulLogin() {
        viewModel.playerBalance.value = "${BuildConfig.CURRENCY_SYMBOL} ${PlayerInfo.getPlayerTotalBalance()}"
        viewModel.playerName.value = if (PlayerInfo.getPlayerFirstName().trim().isEmpty()) {
            PlayerInfo.getPlayerUserName()
        } else {
            PlayerInfo.getPlayerFirstName() + " " + PlayerInfo.getPlayerLastName()
        }

        viewModel.onProceedClick()
    }
}