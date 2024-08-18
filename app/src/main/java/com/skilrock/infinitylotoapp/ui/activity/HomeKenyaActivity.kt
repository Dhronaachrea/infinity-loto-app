package com.skilrock.infinitylotoapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.response_data_class.LoginResponseData
import com.skilrock.infinitylotoapp.data_class.response_data_class.LogoutResponseData
import com.skilrock.infinitylotoapp.databinding.ActivityHomeSabanzuriBinding
import com.skilrock.infinitylotoapp.dialogs.ConfirmationDialog
import com.skilrock.infinitylotoapp.dialogs.LoginDialog
import com.skilrock.infinitylotoapp.utility.*
import com.skilrock.infinitylotoapp.viewmodels.HomeViewModel
import kotlinx.android.synthetic.main.activity_home.tvDrawerVersion
import kotlinx.android.synthetic.main.activity_home_sabanzuri.*
import kotlinx.android.synthetic.main.drawer.*
import kotlinx.android.synthetic.main.toolbar.*


class HomeKenyaActivity : AppCompatActivity() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var _gameList: ArrayList<GameEnginesDataClass>

    private var lastClickTime: Long = 0
    private var clickGap = 600

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAppTheme()
        checkIfLogoutIsRequired()
        setDataBindingAndViewModel()
        wasSessionExpired()
        setThemeToggle()
        setVersion()
        setClickListeners()
        setGamesClickListeners()
        getScreenResolution(this)
        _gameList = getGameList(this)
        viewModel.callJackpotAndTimerApi()
    }

    private fun setAppTheme() {
        if (SharedPrefUtils.getAppTheme(this) == THEME_DARK)
            setTheme(R.style.DarkTheme)
        else
            setTheme(R.style.LightTheme)
    }

    override fun onResume() {
        super.onResume()
        setToolbarLoginOrPlayerInfo()
        setDrawerElements()
        reflectDrawerImage()
    }

    private fun setDataBindingAndViewModel() {
        val binding : ActivityHomeSabanzuriBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_home_sabanzuri)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataLoader.observe(this, observerLoader)
        viewModel.liveDataLogoutStatus.observe(this, observerLogoutStatus)
    }

    private fun wasSessionExpired() {
        val wasSessionExpired = intent.getBooleanExtra("wasSessionExpired", false)
        if (wasSessionExpired)
            openLoginDialog()
    }

    private fun setDrawerElements() {
        if (SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN).isEmpty()) {
            tvDrawerPlayerName.text = getString(R.string.hi_guest)
            tvDrawerPlayerBalance.text = ("${PlayerInfo.getCurrencyDisplayCode()} 0.0")
            llLogoutBar.visibility = View.GONE
        } else {
            llLogoutBar.visibility = View.VISIBLE
        }
        reflectDrawerImage()
    }

    private fun reflectDrawerImage() {
        Glide.with(applicationContext)
            .load(PlayerInfo.getProfilePicPath())
            .apply(
                RequestOptions()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
            )
            .into(ivProfilePic)
    }

    private fun setVersion() {
        val version = getString(R.string.version) + ": " + BuildConfig.VERSION_NAME
        tvDrawerVersion.text = version
    }

    private fun setThemeToggle() {
        switchDayNight.isOn = SharedPrefUtils.getAppTheme(this) == THEME_DARK
        if (SharedPrefUtils.getAppTheme(this) == THEME_DARK)
            tvThemeCaption.text = getString(R.string.switch_light_theme)
        else
            tvThemeCaption.text = getString(R.string.switch_dark_theme)
    }

    private fun checkIfLogoutIsRequired() {
        if (BuildConfig.IS_LOGIN_MANDATORY) {
            if (SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN).isEmpty()) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                PlayerInfo.setLoginData(
                    Gson().fromJson(
                        SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_DATA),
                        LoginResponseData::class.java
                    )
                )
            }
        } else {
            if (SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN).isEmpty()) {
                PlayerInfo.destroy()
            } else {
                PlayerInfo.setLoginData(Gson().fromJson(SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_DATA), LoginResponseData::class.java))
            }
        }
    }

    private fun loadWebView(url: String, tag: String, gameCode: String, title: String,
                            textView: MaterialTextView?) {
        vibrate(this)
        Log.i("log", "Game Url: $url")
        Log.i("log", "Game Tag: $tag")
        Log.i("log", "Game Code: $gameCode")
        Log.i("log", "Game Title: $title")
        val intent = Intent(this, GamePlayKenyaActivity::class.java)
        intent.putExtra("gameTag", tag)
        intent.putExtra("gameUrl", url)
        intent.putExtra("gameCode", gameCode)
        intent.putExtra("gameTitle", title)

        textView?.let { tv ->
            ViewCompat.getTransitionName(tv)?.let {
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this, tv, it)

                startActivity(intent, options.toBundle())
            } ?: run { startActivity(intent) }
        } ?: run { startActivity(intent) }
    }

    private fun loadExtrasWebView(url: String) {
        Log.i("log", "Url: $url")
        drawerLayout.close()
        val intent = Intent(this, ExtrasKenyaActivity::class.java)
        intent.putExtra("webUrl", url)
        startActivity(intent)
    }

    private fun setGamesClickListeners() {
        llSabanzuriLotto.setOnClickListener {
            val gameCategory: GameEnginesDataClass? = _gameList.find { it.gameName == getString(R.string.game_draw_games) }
            gameCategory?.let { drawGame ->
                onGamesClickCommonMethod(drawGame, GAME_ID_SABANZURI, "", null)
            } ?: redToast(getString(R.string.some_internal_error))
        }

        btnSlPlay.setOnClickListener {
            val gameCategory: GameEnginesDataClass? = _gameList.find { it.gameName == getString(R.string.game_draw_games) }
            gameCategory?.let { drawGame ->
                onGamesClickCommonMethod(drawGame, GAME_ID_SABANZURI, "", null)
            } ?: redToast(getString(R.string.some_internal_error))
        }

        llLuckyTwelve.setOnClickListener {
            val gameCategory: GameEnginesDataClass? = _gameList.find { it.gameName == getString(R.string.game_draw_games) }
            gameCategory?.let { drawGame ->
                onGamesClickCommonMethod(drawGame, GAME_ID_LUCKY_TWELVE, "", null)
            } ?: redToast(getString(R.string.some_internal_error))
        }

        btnLtPlay.setOnClickListener {
            val gameCategory: GameEnginesDataClass? = _gameList.find { it.gameName == getString(R.string.game_draw_games) }
            gameCategory?.let { drawGame ->
                onGamesClickCommonMethod(drawGame, GAME_ID_LUCKY_TWELVE, "", null)
            } ?: redToast(getString(R.string.some_internal_error))
        }

        llTreasureHunt.setOnClickListener {
            val gameCategory: GameEnginesDataClass? = _gameList.find { it.gameName == getString(R.string.game_instant_games) }
            gameCategory?.let { instantGame ->
                onGamesClickCommonMethod(instantGame, GAME_CODE_TREASURE_HUNT, getString(R.string.treasure_hunt_small), tvTreasureHunt)
            } ?: redToast(getString(R.string.some_internal_error))
        }

        llTicTacToe.setOnClickListener {
            val gameCategory: GameEnginesDataClass? = _gameList.find { it.gameName == getString(R.string.game_instant_games) }
            gameCategory?.let { instantGame ->
                onGamesClickCommonMethod(instantGame, GAME_CODE_TIC_TAC_TOE, getString(R.string.tic_tac_toe_small), tvTicTacToe)
            } ?: redToast(getString(R.string.some_internal_error))
        }

        llMoneyBee.setOnClickListener {
            val gameCategory: GameEnginesDataClass? = _gameList.find { it.gameName == getString(R.string.game_instant_games) }
            gameCategory?.let { instantGame ->
                onGamesClickCommonMethod(instantGame, GAME_CODE_MONEY_BEE, getString(R.string.money_bee_small), tvMoneyBee)
            } ?: redToast(getString(R.string.some_internal_error))
        }

        llBigFive.setOnClickListener {
            val gameCategory: GameEnginesDataClass? = _gameList.find { it.gameName == getString(R.string.game_instant_games) }
            gameCategory?.let { instantGame ->
                onGamesClickCommonMethod(instantGame, GAME_CODE_BIG_FIVE, getString(R.string.big_five_small), tvBigFive)
            } ?: redToast(getString(R.string.some_internal_error))
        }

        llFunTime.setOnClickListener {
            val gameCategory: GameEnginesDataClass? = _gameList.find { it.gameName == getString(R.string.game_instant_games) }
            gameCategory?.let { instantGame ->
                onGamesClickCommonMethod(instantGame, GAME_CODE_FUN_TIME, getString(R.string.fun_time_small), tvFunTime)
            } ?: redToast(getString(R.string.some_internal_error))
        }
    }

    private fun onGamesClickCommonMethod(gameCategory: GameEnginesDataClass, gameCode: String, title: String, textView: MaterialTextView?) {
        if (gameCategory.gameUrl.isNotBlank()) {
            loadWebView(gameCategory.gameUrl, gameCategory.tag, gameCode, title, textView)
        } else
            redToast(getString(R.string.some_technical_issue))
    }

    private fun setClickListeners() {
        ivDrawer.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

        btnLogin.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime >= clickGap) {
                lastClickTime = SystemClock.elapsedRealtime()
                vibrate(this)
                openLoginDialog()
            }
        }

        switchDayNight.setOnToggledListener { _, isOn ->
            if (SystemClock.elapsedRealtime() - lastClickTime >= clickGap) {
                if (isOn) {
                    SharedPrefUtils.setAppTheme(this, THEME_DARK)
                    tvThemeCaption.text = getString(R.string.switch_light_theme)
                } else {
                    SharedPrefUtils.setAppTheme(this, THEME_LIGHT)
                    tvThemeCaption.text = getString(R.string.switch_dark_theme)
                }

                recreate()
            }
        }

        tvDrawerMyProfile.setOnClickListener {
            onClickDrawerLoginMandatoryItem(Intent(this, ProfileActivity::class.java))
        }

        tvDrawerMyTickets.setOnClickListener {
            onClickDrawerLoginMandatoryItem(Intent(this, MyTicketsActivity::class.java))
        }

        tvDrawerMyWallet.setOnClickListener {
            onClickDrawerLoginMandatoryItem(Intent(this, MyWalletActivity::class.java))
        }

        tvDrawerMyTransactions.setOnClickListener {
            onClickDrawerLoginMandatoryItem(Intent(this, MyTransactionsActivity::class.java))
        }

        tvDrawerInbox.setOnClickListener {
            onClickDrawerLoginMandatoryItem(Intent(this, InboxActivity::class.java))
        }

        tvDrawerDrawGameResults.setOnClickListener {
            onClickDrawerLoginMandatoryItem(Intent(this, DrawGamesResultKenyaActivity::class.java))
        }

        tvDrawerHtpSabanzuriLotto.setOnClickListener {
            loadExtrasWebView(HOW_PLAY_SABANZURI_URL)
        }

        tvDrawerHtpLuckyTwelve.setOnClickListener {
            loadExtrasWebView(HOW_PLAY_LUCKY_TWELVE_URL)
        }

        tvDrawerHtpTreasureHunt.setOnClickListener {
            loadExtrasWebView(HOW_PLAY_TREASURE_URL)
        }

        tvDrawerHtpTicTacToe.setOnClickListener {
            loadExtrasWebView(HOW_PLAY_TIC_TAC_TOE_URL)
        }

        tvDrawerHtpMoneyBee.setOnClickListener {
            loadExtrasWebView(HOW_PLAY_MONEY_BEE_URL)
        }

        tvDrawerHtpBigFive.setOnClickListener {
            loadExtrasWebView(HOW_PLAY_BIG_FIVE_URL)
        }

        tvDrawerHtpFunTime.setOnClickListener {
            loadExtrasWebView(HOW_PLAY_FUN_TIME_URL)
        }

        tvDrawerFaq.setOnClickListener {
            loadExtrasWebView(FAQ_URL)
        }

        tvDrawerResponsibleGaming.setOnClickListener {
            loadExtrasWebView(RESPONSIBLE_GAMING_URL)
        }

        tvDrawerTnc.setOnClickListener {
            loadExtrasWebView(TNC_URL)
        }

        tvDrawerPrivacyPolicy.setOnClickListener {
            loadExtrasWebView(PRIVACY_POLICY_URL)
        }

        tvDrawerContactUs.setOnClickListener {
            loadExtrasWebView(CONTACT_US_URL)
        }

        llDrawerChangePassword.setOnClickListener {
            drawerLayout.close()
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        llDrawerLogout.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime >= clickGap) {
                ConfirmationDialog(
                    this, getString(R.string.logout),
                    getString(R.string.logout_confirmation)
                ) { viewModel.performLogout() }.showDialog()
            }
        }
    }

    private fun setToolbarLoginOrPlayerInfo() {
        setDrawerElements()
        if (SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN).isEmpty()) {
            toolbarPlayerInfo.visibility = View.GONE
            btnLogin.visibility = View.VISIBLE

            viewModel.playerName.value = ""
            viewModel.playerBalance.value = "${PlayerInfo.getCurrencyDisplayCode()} 0"
        } else {
            toolbarPlayerInfo.visibility = View.VISIBLE
            btnLogin.visibility = View.GONE

            viewModel.playerBalance.value = "${PlayerInfo.getCurrencyDisplayCode()} ${PlayerInfo.getPlayerTotalBalance()}"
            viewModel.playerName.value = if (PlayerInfo.getPlayerFirstName().trim().isEmpty()) {
                PlayerInfo.getPlayerUserName()
            } else {
                PlayerInfo.getPlayerFirstName() + " " + PlayerInfo.getPlayerLastName()
            }
        }
    }

    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(GravityCompat.START) -> drawerLayout.closeDrawer(GravityCompat.START)
            else -> super.onBackPressed()
        }
    }

    private val observerLogoutStatus = Observer<ResponseStatus<LogoutResponseData>> {
        if (isNetworkConnected(this)) {
            if (BuildConfig.IS_LOGIN_MANDATORY)
                performLogoutCleanUp(this)
            else {
                performLogoutCleanUp(this, Intent(this, HomeKenyaActivity::class.java))
            }
        }
    }

    private val observerLoader = Observer<Boolean> {
        if (it) Loader.showLoader(this) else Loader.dismiss()
    }

    private fun onClickDrawerLoginMandatoryItem(intent: Intent) {
        drawerLayout.close()
        if (SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN).isEmpty()) {
            openLoginDialog {
                setToolbarLoginOrPlayerInfo()
                startActivity(intent)
            }
        } else {
            startActivity(intent)
        }
    }

    private fun openLoginDialog() {
        val fm: FragmentManager = supportFragmentManager
        val loginDialog = LoginDialog(::onSuccessfulLogin)
        loginDialog.show(fm, "LoginDialog")
    }

    private fun onSuccessfulLogin() {
        setToolbarLoginOrPlayerInfo()
    }

    private fun openLoginDialog(callBack: () -> Unit) {
        val fm: FragmentManager = supportFragmentManager
        val loginDialog = LoginDialog(callBack)
        loginDialog.show(fm, "LoginDialog")
    }

}
