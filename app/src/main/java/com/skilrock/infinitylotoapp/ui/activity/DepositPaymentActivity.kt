@file:Suppress("unused")

package com.skilrock.infinitylotoapp.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.response_data_class.BalanceResponseData
import com.skilrock.infinitylotoapp.databinding.ActivityDepositWithdrawalPaymentBinding
import com.skilrock.infinitylotoapp.utility.*
import com.skilrock.infinitylotoapp.viewmodels.DepositWithdrawalPaymentViewModel
import kotlinx.android.synthetic.main.activity_game_play.*


class DepositPaymentActivity : AppCompatActivity() {

    private lateinit var viewModel: DepositWithdrawalPaymentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAppTheme()
        setDataBindingAndViewModel()
        setClickListeners()
        setWebViewElements()
    }

    private fun setAppTheme() {
        if (SharedPrefUtils.getAppTheme(this) == THEME_DARK)
            setTheme(R.style.DarkTheme)
        else
            setTheme(R.style.LightTheme)
    }

    private fun setDataBindingAndViewModel() {
        val binding : ActivityDepositWithdrawalPaymentBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_deposit_withdrawal_payment
        )

        viewModel = ViewModelProvider(this).get(DepositWithdrawalPaymentViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataLoader.observe(this, observerLoader)
        viewModel.liveDataBalanceStatus.observe(this, observerBalanceStatus)
    }

    private fun setWebViewElements() {
        intent.getStringExtra("requestData")?.let { requestData ->
            //Log.w("log", "Deposit Post Request: $requestData")

            initWebView()
            setWebClient()
            webView.loadData(requestData, "text/html", "UTF-8")
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

        WebView.setWebContentsDebuggingEnabled(true)
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
                Loader.showLoader(this@DepositPaymentActivity)
            }

            override fun shouldOverrideUrlLoading(webView: WebView, url: String?): Boolean {
                //Log.i("log", "EVERY URLS: $url")
                webView.loadUrl(url)
                return true
            }

            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                //Log.i("log", "EVERY URLS: $url")
                super.doUpdateVisitedHistory(view, url, isReload)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                Loader.dismiss()

                Log.d("log", "onPageFinished: $url")
                //webView.loadUrl("javascript:window.HtmlViewer.showHTML(document.getElementsByName('responseJson')[0].value);")

                webView.loadUrl("javascript:"
                        + "var len = document.getElementsByName('responseJson').length;"
                        + "if (len > 0) {"
                        + " window.HtmlViewer.showHTML(document.getElementsByName('responseJson')[0].value);"
                        + "} else {"
                        + " window.HtmlViewer.showHTML('');"
                        + " }")

            }

            override fun onLoadResource(view: WebView?, url: String?) {
                //Log.d("log", "onLoadResource: $url")
                super.onLoadResource(view, url)
            }
        }

        //webView.addJavascriptInterface(JavaScriptInterface(), "HtmlViewer")
        webView.addJavascriptInterface(MyJavaScriptInterface(), "HtmlViewer")

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    inner class MyJavaScriptInterface {

        @JavascriptInterface
        fun showHTML(html: String?) {
            Log.d("log", "SHOW HTML: $html")

            html?.let { data ->
                if (data.isNotBlank()) {
                    val intent = Intent()
                    intent.putExtra("responseData", data)
                    setResult(222, intent)
                    finish()
                }

            }
        }
    }

    private fun setClickListeners() {
        ivBack.setOnClickListener {
            vibrate(this)
            finish()
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
