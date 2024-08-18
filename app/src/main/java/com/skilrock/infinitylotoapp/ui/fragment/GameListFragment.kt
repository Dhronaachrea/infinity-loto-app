package com.skilrock.infinitylotoapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.skilrock.infinitylotoapp.R
import com.skilrock.infinitylotoapp.adapter.GameListAdapter
import com.skilrock.infinitylotoapp.databinding.GameListFragmentBinding
import com.skilrock.infinitylotoapp.ui.activity.GamePlayActivity
import com.skilrock.infinitylotoapp.utility.getGameList
import com.skilrock.infinitylotoapp.utility.vibrate
import com.skilrock.infinitylotoapp.viewmodels.GameListViewModel
import kotlinx.android.synthetic.main.game_list_fragment.*

class GameListFragment : BaseFragment() {

    private lateinit var viewModel: GameListViewModel
    private lateinit var binding : GameListFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.game_list_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GameListViewModel::class.java)
        binding.gameListViewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarElements("", View.VISIBLE)
        setGameUI()
    }

    private fun setGameUI() {
        context?.let { cxt ->
            rvGames.post {
                Log.w("log", "Game List: " + getGameList(cxt).toString())
                Log.i("log", "RecyclerView Height: " + rvGames.height)
                Log.i("log", "Screen Density: " + resources.displayMetrics.density)

                val gameList = getGameList(cxt)
                Log.i("log", "Game List: $gameList")
                val spanCount = if (gameList.size < 5) 2 else 3

                rvGames.layoutManager = GridLayoutManager(cxt, spanCount)
                rvGames.setHasFixedSize(true)
                val adapter = GameListAdapter(gameList, cxt, (rvGames.height / 2) - 40 ) { gameData ->
                    vibrate(cxt)
                    loadWebView(gameData.gameUrl, gameData.tag)
                }
                rvGames.adapter = adapter
            }
        }
    }

    private fun loadWebView(url: String, tag: String) {
        if (url.trim().isNotBlank()) {
            context?.let {
                vibrate(it)
                Log.i("log", "Game Tag: $tag")
                val intent = Intent(it, GamePlayActivity::class.java)
                intent.putExtra("gameTag", tag)
                intent.putExtra("gameUrl", url)
                it.startActivity(intent)
            }
        }
    }

}
