package com.skilrock.infinitylotoapp.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.gson.Gson
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.data_class.response_data_class.LoginResponseData
import com.skilrock.infinitylotoapp.databinding.ActivityHomeBinding
import com.skilrock.infinitylotoapp.dialogs.LoginDialog
import com.skilrock.infinitylotoapp.utility.PlayerInfo
import com.skilrock.infinitylotoapp.utility.SharedPrefUtils
import com.skilrock.infinitylotoapp.utility.getScreenResolution
import com.skilrock.infinitylotoapp.utility.vibrate
import com.skilrock.infinitylotoapp.viewmodels.HomeViewModel
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private var _currentFragmentLabel = ""
    private lateinit var viewModel: HomeViewModel

    private var lastClickTime: Long = 0
    private var clickGap = 600

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(toolbar)
        checkIfLogoutIsRequired()
        setDataBindingAndViewModel()
        initializeNavigationComponents()
        setDrawerClick()
        setVersion()
        setClickListeners()
        getScreenResolution(this)
    }

    override fun onResume() {
        super.onResume()
        setToolbarLoginOrPlayerInfo()
    }

    private fun setDataBindingAndViewModel() {
        val binding : ActivityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initializeNavigationComponents() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        NavigationUI.setupWithNavController(navigationView, navController)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.e("log", "Destination: ${destination.label}")
            _currentFragmentLabel = destination.label.toString()
        }

        bottomNavigationView.setupWithNavController(navController)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.gameListFragment -> {
                    vibrate(this)
                    navController.navigate(R.id.gameListFragment)
                    true
                }
                R.id.fundTransferFragment -> {
                    vibrate(this)
                    setNavigation(R.id.fundTransferFragment)
                }
                R.id.myProfileFragment -> {
                    vibrate(this)
                    setNavigation(R.id.myProfileFragment)
                }
                else -> false
            }
        }
    }

    private fun setNavigation(id: Int) : Boolean {
        return if (SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN).isEmpty()) {
            openLoginDialog()
            false
        } else {
            navController.navigate(id)
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    private fun setVersion() {
        val version = getString(R.string.version) + ": " + BuildConfig.VERSION_NAME
        tvDrawerVersion.text = version
    }

    private fun setDrawerClick() {
        ivDrawer.setOnClickListener {

            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(drawerLayout.windowToken, 0)

            val isBack: Boolean = ivDrawer.drawable.constantState?.equals(AppCompatResources.getDrawable(this, R.drawable.back_icon)?.constantState) ?: false
            if(isBack)
                navController.navigateUp()
            else
                drawerLayout.openDrawer(GravityCompat.START)
        }
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
        }
    }

    private fun setClickListeners() {
        btnLogin.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime >= clickGap) {
                lastClickTime = SystemClock.elapsedRealtime()
                vibrate(this)
                openLoginDialog()
            }
        }
    }

    fun setToolbarLoginOrPlayerInfo() {
        if (SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN).isEmpty()) {
            toolbarPlayerInfo.visibility = View.GONE
            btnLogin.visibility = View.VISIBLE

            viewModel.playerName.value = ""
            viewModel.playerBalance.value = "${BuildConfig.CURRENCY_SYMBOL} 0"
            setDrawerEnabled(false)
            ivDrawer.visibility = View.GONE
        } else {
            toolbarPlayerInfo.visibility = View.VISIBLE
            btnLogin.visibility = View.GONE

            viewModel.playerBalance.value = "${BuildConfig.CURRENCY_SYMBOL} ${PlayerInfo.getPlayerTotalBalance()}"
            viewModel.playerName.value = if (PlayerInfo.getPlayerFirstName().trim().isEmpty()) {
                PlayerInfo.getPlayerUserName()
            } else {
                PlayerInfo.getPlayerFirstName() + " " + PlayerInfo.getPlayerLastName()
            }
            setDrawerEnabled(true)
            ivDrawer.visibility = View.VISIBLE
        }
    }

    private fun setDrawerEnabled(enabled: Boolean) {
        val lockMode =
            if (enabled) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        drawerLayout.setDrawerLockMode(lockMode)
    }

    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(GravityCompat.START) -> drawerLayout.closeDrawer(GravityCompat.START)
            else -> {
                if ((_currentFragmentLabel == getString(R.string.profile)) || (_currentFragmentLabel == getString(R.string.fund_transfer))) {
                    navController.popBackStack(R.id.gameListFragment, true)
                    navController.navigate(R.id.gameListFragment)
                }
                else
                    super.onBackPressed()
            }
        }
    }

    fun goToHomeFromFundTransfer() {
        navController.navigateUp()
        navController.navigateUp()
    }

    private fun openLoginDialog() {
        val fm: FragmentManager = supportFragmentManager
        val loginDialog = LoginDialog(::onSuccessfulLogin)
        loginDialog.show(fm, "NotesEntryDialog")
    }

    fun navigateToHome() {
        navController.popBackStack(R.id.gameListFragment, true)
        navController.navigate(R.id.gameListFragment)
    }

    private fun onSuccessfulLogin() {
        setToolbarLoginOrPlayerInfo()
    }

}
