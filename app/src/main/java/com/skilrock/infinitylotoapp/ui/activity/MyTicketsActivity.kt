package com.skilrock.infinitylotoapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.adapter.MyTicketsAdapter
import com.skilrock.infinitylotoapp.data_class.response_data_class.MyTicketsResponseData
import com.skilrock.infinitylotoapp.databinding.ActivityMyTicketsBinding
import com.skilrock.infinitylotoapp.dialogs.ErrorDialog
import com.skilrock.infinitylotoapp.dialogs.LoginDialog
import com.skilrock.infinitylotoapp.utility.*
import com.skilrock.infinitylotoapp.viewmodels.MyTicketsViewModel
import kotlinx.android.synthetic.main.activity_my_transactions.*
import kotlinx.android.synthetic.main.toolbar_without_login_option.*

class MyTicketsActivity : AppCompatActivity() {

    private lateinit var viewModel: MyTicketsViewModel
    private lateinit var adapter: MyTicketsAdapter
    private var isFirstTimeResponse = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAppTheme()
        setDataBindingAndViewModel()
        setUpRecyclerView()
        setOnClickListeners()
        viewModel.onProceedClick()
    }

    private fun setAppTheme() {
        if (SharedPrefUtils.getAppTheme(this) == THEME_DARK)
            setTheme(R.style.DarkTheme)
        else
            setTheme(R.style.LightTheme)
    }

    private fun setDataBindingAndViewModel() {
        val binding : ActivityMyTicketsBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_my_tickets)

        viewModel = ViewModelProvider(this).get(MyTicketsViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataLoader.observe(this, observerLoader)
        viewModel.liveDataVibrator.observe(this, observerVibrator)
        viewModel.liveDataResponse.observe(this, observerMyTickets)
    }

    private fun setUpRecyclerView() {
        rvReport.layoutManager = LinearLayoutManager(this)
        rvReport.setHasFixedSize(true)
        adapter = MyTicketsAdapter(this, ::onClickTicket)
        rvReport.adapter = adapter
    }

    private val observerVibrator = Observer<String> { vibrate(this) }

    private val observerLoader = Observer<Boolean> {
        if (it) Loader.showLoader(this) else Loader.dismiss()
    }

    private val observerMyTickets = Observer<ResponseStatus<MyTicketsResponseData>> {
        adapter.clearData()
        when(it) {
            is ResponseStatus.Success -> {
                val ticketList: ArrayList<MyTicketsResponseData.Ticket?>? = it.response.ticketList
                val isListEmpty: Boolean = it.response.ticketList?.isEmpty() ?: true
                if (isListEmpty) {
                    if (!isFirstTimeResponse) {
                        ErrorDialog(
                            this, getString(R.string.my_tickets), getString(R.string.no_tickets_found)
                        ) {}.showDialog()
                    }
                }
                else
                    adapter.setTxnList(ticketList)

                isFirstTimeResponse = false
            }

            is ResponseStatus.Error -> {
                if (!isFirstTimeResponse) {
                    if (it.errorCode == SESSION_EXPIRE_CODE) {
                        redToast(it.errorMessage.getMsg(this))
                        openLoginDialog()
                    } else {
                        ErrorDialog(
                            this, getString(R.string.my_tickets_error),
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
                        this, getString(R.string.my_tickets_error),
                        getString(it.errorMessageCode)
                    ) {}.showDialog()
                }
                isFirstTimeResponse = false
            }
        }
    }

    private fun setOnClickListeners() {
        ivBack.setOnClickListener { finish() }

        containerFromDate.setOnClickListener { openStartDateDialog(this, tvStartDate, tvEndDate) }

        containerEndDate.setOnClickListener { openEndDateDialog(this, tvStartDate, tvEndDate) }
    }

    private fun onClickTicket(transactionId: String) {
        vibrate(this)

        val drawGamesData: GameEnginesDataClass? = getGameList(this).find { it.gameName == getString(R.string.game_draw_games) }
        drawGamesData?.let { drawGame ->
            if (drawGame.gameUrl.isNotBlank()) {
                //http://games.sabanzuri.com/dge/view-ticket/0/2670/53/umesh1234/361ea03211a6d28238a307020f6ae258/998509/en/KSH/www.sabanzurilotto.com/0
                val url = drawGame.gameUrl + GAME_TYPE_DGE + "/view-ticket/0/" +
                        transactionId + "/${PlayerInfo.getPlayerId()}/${PlayerInfo.getPlayerUserName()}/${PlayerInfo.getPlayerToken()}/${PlayerInfo.getPlayerTotalBalance()}/${BuildConfig.LANGUAGE}/${PlayerInfo.getCurrency()}/${PlayerInfo.getCurrencyDisplayCode()}/${BuildConfig.DOMAIN_NAME}/${MOBILE_PLATFORM}"


                Log.i("log", "URL: $url")
                val intent = Intent(this, ExtrasKenyaActivity::class.java)
                intent.putExtra("webUrl", url)
                startActivity(intent)
            } else
                redToast(getString(R.string.some_technical_issue))
        } ?: redToast(getString(R.string.some_internal_error))
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