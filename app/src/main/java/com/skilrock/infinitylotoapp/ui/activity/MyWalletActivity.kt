package com.skilrock.infinitylotoapp.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.adapter.MyWalletViewPagerAdapter
import com.skilrock.infinitylotoapp.databinding.ActivityMyWalletBinding
import com.skilrock.infinitylotoapp.utility.SharedPrefUtils
import com.skilrock.infinitylotoapp.utility.THEME_DARK
import com.skilrock.infinitylotoapp.viewmodels.MyWalletViewModel
import kotlinx.android.synthetic.main.activity_my_wallet.*
import kotlinx.android.synthetic.main.toolbar_without_login_option.*

class MyWalletActivity : AppCompatActivity() {

    private lateinit var viewModel: MyWalletViewModel
    private lateinit var viewPagerAdapter: MyWalletViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAppTheme()
        setDataBindingAndViewModel()
        setViewPagerAndTabs()
        setClickListeners()
    }

    private fun setAppTheme() {
        if (SharedPrefUtils.getAppTheme(this) == THEME_DARK)
            setTheme(R.style.DarkTheme)
        else
            setTheme(R.style.LightTheme)
    }

    private fun setDataBindingAndViewModel() {
        val binding : ActivityMyWalletBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_my_wallet)

        viewModel = ViewModelProvider(this).get(MyWalletViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun setViewPagerAndTabs() {
        viewPagerAdapter    = MyWalletViewPagerAdapter(this)
        viewPagerWallet.adapter = viewPagerAdapter

        TabLayoutMediator(tabLayoutWallet, viewPagerWallet) { tab, position ->
            if (position == 0)
                tab.text = getString(R.string.deposit)
            else if (position == 1)
                tab.text = getString(R.string.withdrawal)
        }.attach()
    }

    private fun setClickListeners() {
        ivBack.setOnClickListener { finish() }
    }

    fun setToolbarBalance(balance: String) {
        tvPlayerBalance.text = (BuildConfig.CURRENCY_SYMBOL + " " + balance)
    }
}