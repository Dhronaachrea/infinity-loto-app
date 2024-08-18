package com.skilrock.infinitylotoapp.ui.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.response_data_class.BalanceResponseData
import com.skilrock.infinitylotoapp.databinding.ActivityGamePlayKenyaBinding
import com.skilrock.infinitylotoapp.dialogs.LoginDialog
import com.skilrock.infinitylotoapp.utility.*
import com.skilrock.infinitylotoapp.viewmodels.GamePlayViewModel
import kotlinx.android.synthetic.main.activity_game_play.*
import kotlinx.android.synthetic.main.activity_home.tvToolBarTitle


class GamePlayKenyaActivity : AppCompatActivity() {

    private lateinit var viewModel: GamePlayViewModel
    private var _gameType = ""
    private var _url = ""
    private var _gameCode = ""
    private var _tag: String? = null
    private var clearHistory = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAppTheme()
        setDataBindingAndViewModel()
        setToolbarElements()
        setWebViewElements()
        setClickListeners()
    }

    private fun setAppTheme() {
        if (SharedPrefUtils.getAppTheme(this) == THEME_DARK)
            setTheme(R.style.DarkTheme)
        else
            setTheme(R.style.LightTheme)
    }

    private fun setDataBindingAndViewModel() {
        val binding : ActivityGamePlayKenyaBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_game_play_kenya)
        viewModel = ViewModelProvider(this).get(GamePlayViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataLoader.observe(this, observerLoader)
        viewModel.liveDataBalanceStatus.observe(this, observerBalanceStatus)
    }

    private fun onSuccessfulLogin() {
        setToolbarLoginOrPlayerInfo()
        val completeUrl = getCompleteUrl()
        Log.w("log", "Complete Url: $completeUrl")
        webView.loadUrl(completeUrl)
    }

    private fun setToolbarLoginOrPlayerInfo() {
        if (SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN).isEmpty()) {
            toolbarGamePlayPlayerInfo.visibility = View.GONE
            btnGamePlayLogin.visibility = View.VISIBLE

            viewModel.playerName.value = ""
            viewModel.playerBalance.value = "${BuildConfig.CURRENCY_SYMBOL} 0"
        } else {
            toolbarGamePlayPlayerInfo.visibility = View.VISIBLE
            btnGamePlayLogin.visibility = View.GONE

            viewModel.playerBalance.value = "${BuildConfig.CURRENCY_SYMBOL} ${PlayerInfo.getPlayerTotalBalance()}"
            viewModel.playerName.value = if (PlayerInfo.getPlayerFirstName().trim().isEmpty()) {
                PlayerInfo.getPlayerUserName()
            } else {
                PlayerInfo.getPlayerFirstName() + " " + PlayerInfo.getPlayerLastName()
            }
        }
    }

    private fun setToolbarElements() {
        setToolbarLoginOrPlayerInfo()
        _gameCode = intent.getStringExtra("gameCode") ?: ""
        _tag = intent.getStringExtra("gameTag")

        _tag?.let {
            tvToolBarTitle.text = when (it) {
                TAG_DGE -> {
                    _gameType = GAME_TYPE_DGE
                    getString(R.string.game_draw_games_small)
                }
                TAG_IGE -> {
                    _gameType = GAME_TYPE_IGE
                    getString(R.string.game_instant_games_small)
                }
                TAG_SBS -> {
                    _gameType = GAME_TYPE_SBS
                    getString(R.string.game_sports_betting_small)
                }
                TAG_SLE -> {
                    _gameType = GAME_TYPE_SLE
                    getString(R.string.game_sports_lottery_small)
                }
                TAG_BET_GAMES -> {
                    _gameType = "-"
                    getString(R.string.game_bet_games_small)
                }
                TAG_VIRTUAL_GAMES -> {
                    _gameType = GAME_TYPE_VIRTUAL_GAMES
                    getString(R.string.game_virtual_games_small)
                }
                else -> {
                    _gameType = ""
                    ""
                }
            }
        }

        when {
            _tag.equals(TAG_VIRTUAL_GAMES) -> toolbarLayout.visibility = View.VISIBLE
            _tag.equals(TAG_SBS) -> toolbarLayout.visibility = View.VISIBLE
            _tag.equals(TAG_IGE) -> toolbarLayout.visibility = View.VISIBLE
            else -> toolbarLayout.visibility = View.GONE
        }

        val title = intent.getStringExtra("gameTitle")
        title?.let {
            tvToolBarTitle.text = it
        }
    }

    private fun setWebViewElements() {
        intent.getStringExtra("gameUrl")?.let {
            _url = it
            initWebView()
            setWebClient()
            val url = getCompleteUrl()
            Log.w("log", "Complete Url: $url")
            webView.loadUrl(url)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView.settings.setSupportMultipleWindows(true)
        webView.settings.javaScriptEnabled      = true
        webView.settings.loadWithOverviewMode   = true
        webView.settings.useWideViewPort        = true
        webView.settings.domStorageEnabled      = true
        webView.settings.userAgentString        = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36"

        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.setInitialScale(1)

        webView.webViewClient = object : WebViewClient() {
            override
            fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.proceed()
            }
        }

        WebView.setWebContentsDebuggingEnabled(true)
        webView.addJavascriptInterface(
            JavaScriptInterface(), when {
                _tag.equals(TAG_VIRTUAL_GAMES) -> "Android"
                _tag.equals(TAG_SBS) -> "Android"
                _tag.equals(TAG_IGE) -> "JSInterface"
                else -> "Mobile"
            }
        )
    }

    private fun setWebClient() {
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressBar.progress = newProgress
                if (newProgress < 100 && progressBar.visibility == ProgressBar.GONE) {
                    progressBar.visibility = ProgressBar.VISIBLE
                }

                if (newProgress == 100) {
                    progressBar.visibility = ProgressBar.GONE
                }
            }

            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?
            ): Boolean {
                Log.w("onJsAlert", "URL: $url")
                Log.w("onJsAlert", "Message: $message")
                return false
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun shouldOverrideUrlLoading(webView: WebView, url: String?): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d("log", "onPageFinished: $url")
                if (clearHistory) {
                    clearHistory = false
                    webView.clearHistory()
                }
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                //Log.d("log", "logonLoadResource: $url")
                super.onLoadResource(view, url)
            }

        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            if (_tag.equals(TAG_IGE)) {
                finish()
            }
            else
                webView.goBack()
            return true
        } else if (keyCode == KeyEvent.KEYCODE_BACK && !webView.canGoBack()) {
            finish()

            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    inner class JavaScriptInterface {

        @JavascriptInterface
        fun showLoginDialog() {
            Log.e("log", "Open Login Dialog")
            openLoginDialog()
        }

        @JavascriptInterface
        fun onBalanceUpdate() {
            Log.e("log", "onBalanceUpdate()")
            viewModel.onBalanceRefreshClick()
        }

        @JavascriptInterface
        fun updateBalance() {
            Log.e("log", "updateBalance()")
            viewModel.onBalanceRefreshClick()
        }

        @JavascriptInterface
        fun informSessionExpiry() {
            Log.e("log", "Session Expired")
            redToast(getString(R.string.session_expired))
            openLoginDialog()
        }

        @JavascriptInterface
        fun setToolbarTitle(title: String) {
            tvToolBarTitle.text = title
        }

        @JavascriptInterface
        fun goToHome() {
            finish()
        }

        @JavascriptInterface
        fun loadInstantGameUrl(igeUrl: String) {
            val finalUrl = "$igeUrl&isNative=android"
            Log.i("log", "Instant Game URL: $finalUrl")
            webView.post {
                webView.loadUrl(finalUrl)
            }
        }

        @JavascriptInterface
        fun makeWebViewVisible() {
            //makeWebViewVisible
        }

        @JavascriptInterface
        fun loginWindow() {
            Log.e("log", "loginWindow(): Open Login Dialog")
            openLoginDialog()
        }

        @JavascriptInterface
        fun backToLobby() {
            finish()
        }

        @JavascriptInterface
        fun updateBal() {
            Log.e("log", "updateBal()")
            viewModel.onBalanceRefreshClick()
        }

        @JavascriptInterface
        fun reloadGame(tag: String) {
            if (tag == "appGameLoad") {
                webView.post {
                    val url = getCompleteUrl()
                    Log.w("log", "Reload Complete Url: $url")
                    webView.loadUrl(url)
                    clearHistory = true
                }
            }
        }

    }

    private fun setClickListeners() {
        ivBack.setOnClickListener {
            vibrate(this)
            finish()
        }

        btnGamePlayLogin.setOnClickListener {
            openLoginDialog()
        }
    }

    private fun openLoginDialog() {
        val fm: FragmentManager = supportFragmentManager
        val loginDialog = LoginDialog(::onSuccessfulLogin)
        loginDialog.show(fm, "NotesEntryDialog")
    }

    private fun getCompleteUrl() : String {
        //http://games.sabanzuri.com/dge/twelvebytwentyfour/-/-/-/0/en/KSH/www.sabanzurilotto.com/0
        //http://games.sabanzuri.com/dge/powerball/-/-/-/0/en/KSH/www.sabanzurilotto.com/0

        //http://games.sabanzuri.com/ige/KENYA/105/buy/41/tarun015/0d251222a5db313d67865cb578298512/952059/en/KSH/www.sabanzurilotto.com/0#http://www.sabanzuri.com/instant-win

        return if (SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN).isEmpty()) {
            when {
                _tag.equals(TAG_IGE) ->
                    "${_url}${_gameType}/KENYA/${_gameCode}/buy/-/-/-/0/${BuildConfig.LANGUAGE}/${PlayerInfo.getCurrency()}/${PlayerInfo.getCurrencyDisplayCode()}/${BuildConfig.DOMAIN_NAME}/${MOBILE_PLATFORM}"

                else ->
                    "${_url}${_gameType}/${_gameCode}/-/-/-/0/${BuildConfig.LANGUAGE}/${PlayerInfo.getCurrency()}/${PlayerInfo.getCurrencyDisplayCode()}/${BuildConfig.DOMAIN_NAME}/${MOBILE_PLATFORM}"
            }
        } else {
            when {
                _tag.equals(TAG_IGE) ->
                    "${_url}${_gameType}/KENYA/${_gameCode}/buy/${PlayerInfo.getPlayerId()}/${PlayerInfo.getPlayerUserName()}/${PlayerInfo.getPlayerToken()}/${PlayerInfo.getPlayerTotalBalance()}/${BuildConfig.LANGUAGE}/${PlayerInfo.getCurrency()}/${PlayerInfo.getCurrencyDisplayCode()}/${BuildConfig.DOMAIN_NAME}/${MOBILE_PLATFORM}"

                else ->
                    "${_url}${_gameType}/${_gameCode}/${PlayerInfo.getPlayerId()}/${PlayerInfo.getPlayerUserName()}/${PlayerInfo.getPlayerToken()}/${PlayerInfo.getPlayerTotalBalance()}/${BuildConfig.LANGUAGE}/${PlayerInfo.getCurrency()}/${PlayerInfo.getCurrencyDisplayCode()}/${BuildConfig.DOMAIN_NAME}/${MOBILE_PLATFORM}"
            }
        }
    }

    private val observerBalanceStatus = Observer<ResponseStatus<BalanceResponseData>> {
        when (it) {
            is ResponseStatus.Success -> {
                it.response.wallet?.let { wallet ->
                    PlayerInfo.setBalance(this, wallet)
                }
            }

            is ResponseStatus.Error -> redToast(it.errorMessage.getMsg(this))

            is ResponseStatus.TechnicalError -> redToast(getString(it.errorMessageCode))
        }
    }

    private val observerLoader = Observer<Boolean> {
        if (it) Loader.showLoader(this) else Loader.dismiss()
    }

}
