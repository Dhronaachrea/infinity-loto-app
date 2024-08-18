package com.skilrock.infinitylotoapp.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dcastalia.localappupdate.DownloadApk
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.response_data_class.LoginResponseData
import com.skilrock.infinitylotoapp.data_class.response_data_class.VersionResponseData
import com.skilrock.infinitylotoapp.databinding.ActivitySplashBinding
import com.skilrock.infinitylotoapp.dialogs.AppVersionDialog
import com.skilrock.infinitylotoapp.dialogs.ErrorDialog
import com.skilrock.infinitylotoapp.utility.*
import com.skilrock.infinitylotoapp.viewmodels.SplashViewModel

class SplashActivity : AppCompatActivity() {

    private lateinit var mResponse: VersionResponseData
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        setAppTheme()
        setDataBindingAndViewModel()
        getFirebaseToken()
    }

    private fun setAppTheme() {
        if (SharedPrefUtils.getAppTheme(this) == THEME_DARK)
            setTheme(R.style.DarkTheme)
        else
            setTheme(R.style.LightTheme)
    }

    private fun setDataBindingAndViewModel() {
        val binding : ActivitySplashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.liveDataVersionStatus.observe(this, observerResponse)
    }

    private fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("log", "Fetching FCM registration token failed", task.exception)
                callVersionApi()
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result ?: ""

            // Log and toast
            Log.d("log", "Firebase Token: $token")
            SharedPrefUtils.setFcmToken(this@SplashActivity, token)
            callVersionApi()
        })
    }

    private fun callVersionApi() {
        viewModel.callVersionApi(BuildConfig.VERSION_NAME, SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN))
    }

    private val observerResponse = Observer<ResponseStatus<VersionResponseData>> {
        when(it) {
            is ResponseStatus.Success -> {
                mResponse = it.response
                setGameEngineData(it.response)
                if (it.response.appDetails?.isUpdateAvailable == true) {
                    performVersioningOperation(it.response)
                }
                else {
                    Handler().postDelayed({
                        proceedToLogin()
                    }, 2000)
                }
            }

            is ResponseStatus.Error -> {
                ErrorDialog(this, getString(R.string.version_error), it.errorMessage.getMsg(this)) {finish()}.showDialog()
            }

            is ResponseStatus.TechnicalError -> {
                ErrorDialog(this, getString(R.string.version_error), getString(it.errorMessageCode)) {finish()}.showDialog()
            }
        }
    }

    private fun performVersioningOperation(resp: VersionResponseData) {
        resp.appDetails?.mandatory.let { isMandatory ->
            isMandatory?.let {
                showUpdateDialog(it, resp.appDetails?.url, resp.appDetails?.message)
            }
        } ?: ErrorDialog(this, getString(R.string.version_error), getString(R.string.some_technical_issue)) {finish()}.showDialog()
    }

    private fun showUpdateDialog(isUpdateForceFull: Boolean, url: String?, message: String?) {
        val updateMsg = message ?: getString(R.string.new_update_available)
        url?.let {
            AppVersionDialog(isUpdateForceFull, it, updateMsg, this, ::onUpdateSelected).showDialog()
        } ?: ErrorDialog(this, getString(R.string.version_error), getString(R.string.some_internal_error)) {finish()}.showDialog()
    }

    private fun onUpdateSelected(isNow: Boolean, url: String) {
        if (isNow) {
            if (isWriteStoragePermissionGranted()) downloadApk(url)
        } else {
            Handler().postDelayed({
                proceedToLogin()
            }, 2000)
        }
    }

    private fun downloadApk(url: String) {
        val downloadApk = DownloadApk(this)
        downloadApk.startDownloadingApk(url)
    }

    private fun proceedToLogin() {
        if (SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN).isEmpty()) {
            PlayerInfo.destroy()
            if(BuildConfig.IS_LOGIN_MANDATORY) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = when (BuildConfig.CLIENT) {
                    CLIENT_SABANZURI -> Intent(this, HomeKenyaActivity::class.java)
                    else -> Intent(this, HomeActivity::class.java)
                }
                startActivity(intent)
                finish()
            }

        } else {
            PlayerInfo.setLoginData(Gson().fromJson(SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_DATA), LoginResponseData::class.java))
            val intent = when (BuildConfig.CLIENT) {
                CLIENT_SABANZURI -> Intent(this, HomeKenyaActivity::class.java)
                else -> Intent(this, HomeActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun setGameEngineData(resp: VersionResponseData) {
        SharedPrefUtils.clearGameEngineSharedPref(this)

        resp.gameEngineInfo?.dGE?.let { engine ->
            val gameEngine = GameEnginesDataClass(getString(R.string.game_draw_games),
                engine.serverUrl ?: "", R.drawable.icon_draw_game, TAG_DGE)
            val jsonString = GsonBuilder().create().toJson(gameEngine)
            SharedPrefUtils.setGameData(this, SharedPrefUtils.GAME_DGE, jsonString)
        }

        resp.gameEngineInfo?.sBS?.let { engine ->
            val gameEngine = GameEnginesDataClass(getString(R.string.game_sports_betting),
                engine.serverUrl ?: "", R.drawable.icon_sports_betting, TAG_SBS)
            val jsonString = GsonBuilder().create().toJson(gameEngine)
            SharedPrefUtils.setGameData(this, SharedPrefUtils.GAME_SBS, jsonString)
        }

        resp.gameEngineInfo?.iGE?.let { engine ->
            val gameEngine = GameEnginesDataClass(getString(R.string.game_instant_games),
                engine.serverUrl ?: "", R.drawable.icon_slot_games, TAG_IGE)
            val jsonString = GsonBuilder().create().toJson(gameEngine)
            SharedPrefUtils.setGameData(this, SharedPrefUtils.GAME_IGE, jsonString)
        }

        resp.gameEngineInfo?.sLE?.let { engine ->
            val gameEngine = GameEnginesDataClass(getString(R.string.game_sports_lottery),
                engine.serverUrl ?: "", R.drawable.icon_sports_lottery, TAG_SLE)
            val jsonString = GsonBuilder().create().toJson(gameEngine)
            SharedPrefUtils.setGameData(this, SharedPrefUtils.GAME_SLE, jsonString)
        }

        resp.gameEngineInfo?.bETGAMES?.let { engine ->
            val gameEngine = GameEnginesDataClass(getString(R.string.game_bet_games),
                engine.serverUrl ?: "", R.drawable.icon_live_dealer, TAG_BET_GAMES)
            val jsonString = GsonBuilder().create().toJson(gameEngine)
            SharedPrefUtils.setGameData(this, SharedPrefUtils.GAME_BET_GAMES, jsonString)
        }

        resp.gameEngineInfo?.vIRTUALGAMES?.let { engine ->
            val gameEngine = GameEnginesDataClass(getString(R.string.game_virtual_games),
                engine.serverUrl ?: "", R.drawable.icon_virtual_games, TAG_VIRTUAL_GAMES)
            val jsonString = GsonBuilder().create().toJson(gameEngine)
            SharedPrefUtils.setGameData(this, SharedPrefUtils.GAME_VIRTUAL_GAMES, jsonString)
        }
    }

    private fun isWriteStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.i("log", "Permission is granted2")
                true
            } else {
                Log.v("log", "Permission is revoked2")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
                false
            }
        } else {
            //permission is automatically granted on sdk<23 upon installation
            Log.v("log", "Permission is granted2")
            true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            2 -> {
                Log.d("log", "External storage2")
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("log", "Permission: " + permissions[0] + " was " + grantResults[0])
                    mResponse.appDetails?.url.let { it?.let { apkPath -> downloadApk(apkPath) } }
                }
            }
            3 -> {
                Log.d("log", "External storage1")
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("log", "Permission: " + permissions[0] + " was " + grantResults[0])
                }
            }
        }
    }

}
