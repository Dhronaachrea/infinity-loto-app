package com.skilrock.infinitylotoapp.ui.fragment

import android.content.Context
import androidx.fragment.app.Fragment
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.ui.activity.HomeActivity
import kotlinx.android.synthetic.main.activity_home.*

abstract class BaseFragment : Fragment() {

    lateinit var master: HomeActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeActivity)
            master = context
    }

    fun setToolbarElements(title: String, logoVisibility: Int, isHamburgerIcon: Boolean = true) {
        master.tvToolBarTitle.text = title
        master.ivToolBarLogo.visibility = logoVisibility

        if (isHamburgerIcon)
            master.ivDrawer.setImageResource(R.drawable.drawer_icon)
        else
            master.ivDrawer.setImageResource(R.drawable.back_icon)
    }

    fun updateToolbarBalance(balance: String) {
        val bal = "${BuildConfig.CURRENCY_SYMBOL} $balance"
        master.tvPlayerBalance.text = bal
    }

    fun switchToHome() {
        master.goToHomeFromFundTransfer()
    }
}