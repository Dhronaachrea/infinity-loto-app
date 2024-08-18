package com.skilrock.infinitylotoapp.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.infinitylotoapp.BuildConfig
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.adapter.InboxAdapter
import com.skilrock.infinitylotoapp.data_class.response_data_class.PlayerInboxReadResponseData
import com.skilrock.infinitylotoapp.data_class.response_data_class.PlayerInboxResponseData
import com.skilrock.infinitylotoapp.databinding.ActivityInboxBinding
import com.skilrock.infinitylotoapp.dialogs.ErrorDialog
import com.skilrock.infinitylotoapp.dialogs.InboxBottomSheetDialog
import com.skilrock.infinitylotoapp.dialogs.InformationDialog
import com.skilrock.infinitylotoapp.dialogs.LoginDialog
import com.skilrock.infinitylotoapp.utility.*
import com.skilrock.infinitylotoapp.viewmodels.InboxViewModel
import kotlinx.android.synthetic.main.activity_inbox.*
import kotlinx.android.synthetic.main.toolbar_without_login_option.*
import kotlinx.android.synthetic.main.view_search.*


class InboxActivity : AppCompatActivity() {

    private lateinit var viewModel: InboxViewModel
    private lateinit var adapter: InboxAdapter
    private val listMultipleDelete = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAppTheme()
        setDataBindingAndViewModel()
        setClickListeners()
        setRecyclerView()
        viewModel.callInboxApi()
    }

    private fun setAppTheme() {
        if (SharedPrefUtils.getAppTheme(this) == THEME_DARK)
            setTheme(R.style.DarkTheme)
        else
            setTheme(R.style.LightTheme)
    }

    private fun setDataBindingAndViewModel() {
        val binding : ActivityInboxBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_inbox)
        viewModel = ViewModelProvider(this).get(InboxViewModel::class.java)
        binding.lifecycleOwner = this
        binding.inboxViewModel = viewModel

        viewModel.liveDataVibrator.observe(this, observerVibrator)
        viewModel.liveDataLoader.observe(this, observerLoader)
        viewModel.liveDataInbox.observe(this, observerInbox)
        viewModel.liveDataDeleteMessage.observe(this, observerDeleteMessage)
        viewModel.liveDataReadMessage.observe(this, observerReadMessage)
    }

    private fun setClickListeners() {
        ivBack.setOnClickListener { finish() }

        tvSelectAll.setOnClickListener {
            if (tvSelectAll.text.toString().equals(getString(R.string.select_all), true)) {
                tvSelectAll.text = getString(R.string.deselect_all)
                listMultipleDelete.clear()
                listMultipleDelete.addAll(adapter.getAllInboxId())
                tvDeleteSelected.text = ("${getString(R.string.delete_selected)} (${listMultipleDelete.size})")
                adapter.selectAll(true)
            } else {
                tvSelectAll.text = getString(R.string.select_all)
                listMultipleDelete.clear()
                tvDeleteSelected.text = ("${getString(R.string.delete_selected)} (${listMultipleDelete.size})")
                adapter.selectAll(false)
            }
        }

        tvDeleteSelected.setOnClickListener {
            if (listMultipleDelete.isNotEmpty())
                deleteMessage(listMultipleDelete)
            else
                redToast(getString(R.string.nothing_to_delete))
        }

        searchInputText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                adapter.searchFilter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //beforeTextChanged
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //onTextChanged
            }
        })
    }

    private fun setRecyclerView() {
        rvInbox.layoutManager = LinearLayoutManager(this)
        rvInbox.setHasFixedSize(true)
        adapter = InboxAdapter(this, ::onClickMail, ::onLongPressMail)
        adapter.setHasStableIds(true)
        rvInbox.adapter = adapter

        rvInbox.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        val swipeHelper: SwipeHelper = object : SwipeHelper(this) {
            override fun instantiateUnderlayButton(
                viewHolder: RecyclerView.ViewHolder,
                underlayButtons: MutableList<UnderlayButton>
            ) {
                underlayButtons.add(UnderlayButton("Delete", Color.parseColor("#FF3C30")) { position ->
                    val inboxId = adapter.getInboxId(position)
                    inboxId?.let { id ->
                        val list = ArrayList<Int>()
                        list.add(id)
                        deleteMessage(list)
                    } ?: redToast(getString(R.string.some_technical_issue))
                })
            }
        }
        swipeHelper.attachToRecyclerView(rvInbox)
    }

    private fun deleteMessage(list: ArrayList<Int>) {
        if (list.isNotEmpty())
            viewModel.callDeleteMessageApi(list)
    }

    private fun onClickMail(mailData: PlayerInboxResponseData.PlrInbox, position: Int) {
        rvInbox.hideKeyboard()
        mailData.status?.let { readStatus ->
            if (!readStatus.equals("READ", true)) {
                mailData.inboxId?.let {
                    viewModel.callReadMessageApi(it, position)
                }
            }
        }

        InboxBottomSheetDialog(mailData, ::deleteMessage).apply {
            show(supportFragmentManager, InboxBottomSheetDialog.TAG)
        }
    }

    private fun onLongPressMail(mailData: PlayerInboxResponseData.PlrInbox) {
        rvInbox.hideKeyboard()
        if (!InboxAdapter.IS_LONG_PRESS_ACTIVATED) {
            vibrate(this)
            showDeleteBar(true)
        }
        InboxAdapter.IS_LONG_PRESS_ACTIVATED = true
        mailData.inboxId?.let { id ->
            if (listMultipleDelete.contains(id))
                listMultipleDelete.remove(id)
            else
                listMultipleDelete.add(id)

            tvDeleteSelected.text = ("${getString(R.string.delete_selected)} (${listMultipleDelete.size})")

            if (listMultipleDelete.isEmpty())
                resetLongPress()
        }
    }

    private val observerInbox = Observer<ResponseStatus<PlayerInboxResponseData>> {
        adapter.clearData()
        when(it) {
            is ResponseStatus.Success -> {
                showDeleteBar(false)
                val inboxList: ArrayList<PlayerInboxResponseData.PlrInbox?>? =
                    it.response.plrInboxList
                val isListEmpty: Boolean = inboxList?.isEmpty() ?: true
                if (isListEmpty) {
                    InformationDialog(
                        this, getString(R.string.inbox), getString(R.string.no_mails_found)
                    ) { finish() }.showDialog()
                } else
                    adapter.setInboxList(inboxList)

                it.response.unreadMsgCount?.let { count ->
                    if (count > 0)
                        tvToolBarTitle.text = (getString(R.string.inbox) + " (${count})")
                    else
                        tvToolBarTitle.text = getString(R.string.inbox)
                } ?: run { tvToolBarTitle.text = getString(R.string.inbox) }

            }

            is ResponseStatus.Error -> {
                if (it.errorCode == SESSION_EXPIRE_CODE) {
                    redToast(it.errorMessage.getMsg(this))
                    openLoginDialog()
                } else {
                    ErrorDialog(
                        this, getString(R.string.inbox_error),
                        it.errorMessage.getMsg(this), it.errorCode,
                        Intent(this, HomeKenyaActivity::class.java)
                    ) {}.showDialog()
                }
            }

            is ResponseStatus.TechnicalError -> {
                ErrorDialog(
                    this, getString(R.string.inbox_error),
                    getString(it.errorMessageCode)
                ) {}.showDialog()
            }
        }
    }

    private val observerDeleteMessage = Observer<ResponseStatus<PlayerInboxResponseData>> {
        when(it) {
            is ResponseStatus.Success -> {
                showDeleteBar(false)
                greenToast(getString(R.string.deleted_successfully))
                val inboxList: ArrayList<PlayerInboxResponseData.PlrInbox?>? =
                    it.response.plrInboxList
                val isListEmpty: Boolean = inboxList?.isEmpty() ?: true
                if (isListEmpty) {
                    adapter.clearData()
                    InformationDialog(
                        this, getString(R.string.inbox), getString(R.string.no_mails_found)
                    ) { finish() }.showDialog()
                } else {
                    adapter.clearData()
                    adapter.setInboxList(inboxList)
                }

                it.response.unreadMsgCount?.let { count ->
                    if (count > 0)
                        tvToolBarTitle.text = (getString(R.string.inbox) + " (${count})")
                    else
                        tvToolBarTitle.text = getString(R.string.inbox)
                } ?: run { tvToolBarTitle.text = getString(R.string.inbox) }

            }

            is ResponseStatus.Error -> {
                if (it.errorCode == SESSION_EXPIRE_CODE) {
                    redToast(it.errorMessage.getMsg(this))
                    openLoginDialog()
                } else {
                    redToast(it.errorMessage.getMsg(this))
                }
            }

            is ResponseStatus.TechnicalError -> { redToast(getString(it.errorMessageCode)) }
        }
    }

    private val observerReadMessage = Observer<ResponseStatus<PlayerInboxReadResponseData>> {
        when(it) {
            is ResponseStatus.Success -> {

                it.response.inboxId?.let { id ->
                    it.response.position?.let { pos ->
                        adapter.readMessage(id, pos)
                    }
                }

                it.response.unreadMsgCount?.let { count ->
                    if (count > 0)
                        tvToolBarTitle.text = (getString(R.string.inbox) + " (${count})")
                    else
                        tvToolBarTitle.text = getString(R.string.inbox)
                } ?: run { tvToolBarTitle.text = getString(R.string.inbox) }
            }

            is ResponseStatus.Error -> {
                if (it.errorCode == SESSION_EXPIRE_CODE) {
                    redToast(it.errorMessage.getMsg(this))
                    openLoginDialog()
                }
            }

            is ResponseStatus.TechnicalError -> { Log.e("log", "Technical Error") }
        }
    }

    private val observerVibrator = Observer<String> { vibrate(this) }

    private val observerLoader = Observer<Boolean> {
        if (it) Loader.showLoader(this) else Loader.dismiss()
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

        viewModel.callInboxApi()
    }

    private fun showDeleteBar(flag: Boolean) {
        listMultipleDelete.clear()
        if (flag) {
            searchBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.flip_in_bottom))
            llDeleteBar.visibility  = View.VISIBLE
            searchBar.visibility    = View.GONE
            llDeleteBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.flip_in_top))
        } else {
            searchBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.flip_in_top))
            llDeleteBar.visibility  = View.GONE
            searchBar.visibility    = View.VISIBLE
            llDeleteBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.flip_in_bottom))
        }
    }

    private fun resetLongPress() {
        showDeleteBar(false)
        adapter.resetLongPressRows()
    }

    override fun onBackPressed() {
        when {
            InboxAdapter.IS_LONG_PRESS_ACTIVATED -> { resetLongPress() }
            searchOpenView.visibility == View.VISIBLE -> { close_search_button.performClick() }
            else -> { super.onBackPressed() }
        }
    }

}