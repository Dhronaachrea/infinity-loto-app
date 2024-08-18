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
import com.skilrock.infinitylotoapp.databinding.ActivityGamePlayBinding
import com.skilrock.infinitylotoapp.dialogs.LoginDialog
import com.skilrock.infinitylotoapp.utility.*
import com.skilrock.infinitylotoapp.viewmodels.GamePlayViewModel
import kotlinx.android.synthetic.main.activity_game_play.*
import kotlinx.android.synthetic.main.activity_home.tvToolBarTitle


class GamePlayActivity : AppCompatActivity() {

    private lateinit var viewModel: GamePlayViewModel
    private var _gameType = ""
    private var _url = ""
    private var _tag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setDataBindingAndViewModel()
        setToolbarElements()
        setWebViewElements()
        setClickListeners()
    }

    private fun setDataBindingAndViewModel() {
        val binding : ActivityGamePlayBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_game_play
        )
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
        _tag = intent.getStringExtra("gameTag")
        _tag?.let {
            tvToolBarTitle.text = when (it) {
                TAG_DGE -> {
                    _gameType = "dge"
                    getString(R.string.game_draw_games_small)
                }
                TAG_IGE -> {
                    //_gameType = "InstantGameEngineNode"
                    _gameType = "ige"
                    getString(R.string.game_instant_games_small)
                }
                TAG_SBS -> {
                    _gameType = "sbs"
                    getString(R.string.game_sports_betting_small)
                }
                TAG_SLE -> {
                    _gameType = "sle"
                    getString(R.string.game_sports_lottery_small)
                }
                TAG_BET_GAMES -> {
                    _gameType = "-"
                    getString(R.string.game_bet_games_small)
                }
                TAG_VIRTUAL_GAMES -> {
                    _gameType = "sbs"
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
    }

    private fun setWebViewElements() {
        intent.getStringExtra("gameUrl")?.let {
            _url = it
            initWebView()
            setWebClient()
            //webView.loadUrl(getCompleteUrl())
            val url = getCompleteUrl()
            Log.w("log", "Complete Url: $url")
            webView.loadUrl(url)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView.settings.javaScriptEnabled      = true
        webView.settings.loadWithOverviewMode   = true
        webView.settings.useWideViewPort        = true
        webView.settings.domStorageEnabled      = true
        webView.settings.userAgentString        = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36"

        webView.settings.setSupportMultipleWindows(true)
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
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Loader.showLoader(this@GamePlayActivity)
            }

            override fun shouldOverrideUrlLoading(webView: WebView, url: String?): Boolean {
                webView.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                Loader.dismiss()
                webView.loadUrl("javascript:callJS()")
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
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
        fun showAndroidToolbar(makeVisible: Boolean) {
           //showAndroidToolbar
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
        //<DOMAIN>/:service/:playerid/:username/:sessid/:bal/:lang/:curr/:alias
        //val urlData = "-/-/-/0/${BuildConfig.LANGUAGE}/${BuildConfig.CURRENCY}/${BuildConfig.DOMAIN_NAME}/${MOBILE_PLATFORM}"
        //val urlData = "${PlayerInfo.getPlayerId()}/${PlayerInfo.getPlayerUserName()}/${PlayerInfo.getPlayerToken()}/${PlayerInfo.getPlayerTotalBalance()}/${BuildConfig.LANGUAGE}/${BuildConfig.CURRENCY}/${BuildConfig.DOMAIN_NAME}/${MOBILE_PLATFORM}"

        //http://localhost:4200/sbs/:type/:playerid/:playername/:token/:balance/:locale/:currency/:alias/:family

        return if (SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN).isEmpty()) {
            when {
                _tag.equals(TAG_VIRTUAL_GAMES) -> "${_url}${_gameType}/player/-/-/-/0/${BuildConfig.LANGUAGE}/${PlayerInfo.getCurrency()}/${BuildConfig.DOMAIN_NAME}/VIRTUAL_SPORTS"
                _tag.equals(TAG_SBS) -> "${_url}${_gameType}/player/-/-/-/0/${BuildConfig.LANGUAGE}/${PlayerInfo.getCurrency()}/${BuildConfig.DOMAIN_NAME}/SPORTS_BOOK"
                else -> "${_url}${_gameType}/-/-/-/0/${BuildConfig.LANGUAGE}/${PlayerInfo.getCurrency()}/${BuildConfig.DOMAIN_NAME}/${MOBILE_PLATFORM}"
            }
        } else {
            when {
                _tag.equals(TAG_VIRTUAL_GAMES) -> "${_url}${_gameType}/player/${PlayerInfo.getPlayerId()}/${PlayerInfo.getPlayerUserName()}/${PlayerInfo.getPlayerToken()}/${PlayerInfo.getPlayerTotalBalance()}/${BuildConfig.LANGUAGE}/${PlayerInfo.getCurrency()}/${BuildConfig.DOMAIN_NAME}/VIRTUAL_SPORTS"
                _tag.equals(TAG_SBS) -> "${_url}${_gameType}/player/${PlayerInfo.getPlayerId()}/${PlayerInfo.getPlayerUserName()}/${PlayerInfo.getPlayerToken()}/${PlayerInfo.getPlayerTotalBalance()}/${BuildConfig.LANGUAGE}/${PlayerInfo.getCurrency()}/${BuildConfig.DOMAIN_NAME}/SPORTS_BOOK"
                else -> "${_url}${_gameType}/${PlayerInfo.getPlayerId()}/${PlayerInfo.getPlayerUserName()}/${PlayerInfo.getPlayerToken()}/${PlayerInfo.getPlayerTotalBalance()}/${BuildConfig.LANGUAGE}/${PlayerInfo.getCurrency()}/${BuildConfig.DOMAIN_NAME}/${MOBILE_PLATFORM}"
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
