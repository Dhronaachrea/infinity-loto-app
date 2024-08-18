package com.skilrock.infinitylotoapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.skilrock.infinitylotoapp.ui.fragment.DepositFragment
import com.skilrock.infinitylotoapp.ui.fragment.WithdrawalFragment


class MyWalletViewPagerAdapter(fragmentActivity: FragmentActivity):
    FragmentStateAdapter(fragmentActivity) {

    private val _itemSize = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            DepositFragment()
        } else {
            WithdrawalFragment()
        }
    }

    override fun getItemCount(): Int {
        return _itemSize
    }
}