package com.instagram.mini.presenter.favorite

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instagram.mini.R
import com.instagram.mini.databinding.FavoriteListFragmentBinding
import com.instagram.mini.presenter.base.BaseFragment


class FavoriteListFragment : BaseFragment<FavoriteListFragmentBinding, FavoriteListViewModel>(
    layoutResId = R.layout.favorite_list_fragment,
    FavoriteListViewModel::class
) {
    private var doubleBackToExitPressedOnce = false
    var mAdapter: FavoriteAdapter? = null
    var page = 1

    override fun actionBarTitle(): String = getString(R.string.favorite_list_title)

    override fun initBinding(binding: FavoriteListFragmentBinding, viewModel: FavoriteListViewModel) {
        super.initBinding(binding, viewModel)
        binding.viewModel = viewModel.also {
            it.loadingIndicator.observe(this, ::loadingIndicator)
            it.alertMessage.observe(this, ::showAlert)
            it.infoMessage.observe(this, ::showInfo)
            it.user.observe(this, ::isSignedIn)
            it.arts.observe(this, { arts ->
                arts?.let { ss ->
                    mAdapter?.arts?.addAll(ss)
                    mAdapter?.notifyDataSetChanged()
                    with (binding) {
                        if (page == 1) rvArt.smoothScrollToPosition(0)
                        swipeContainer.isRefreshing = false
                    }
                }
            })
        }
    }

    override fun initViews() {
        super.initViews()
        getBottomNav()?.visibility = View.VISIBLE
        with (binding) {
            swipeContainer.setOnRefreshListener {
                mAdapter?.arts?.clear()
                viewModel?.getFavoritesFromLocal(1)
            }
            mAdapter = FavoriteAdapter()
            mAdapter?.onClick = { art ->
                val bundle = Bundle()
                bundle.putString("title", art.title)
                bundle.putInt("id", art.id)
                findNavController().navigate(R.id.actionToDetailArtFragment, bundle)
            }
            mAdapter?.onDelete = { i ->
                mAdapter?.arts?.get(i)?.let { a ->
                    viewModel?.toggleFavorite(a)
                    mAdapter?.arts?.remove(a)
                    mAdapter?.notifyItemRemoved(i)
                    if (mAdapter?.arts?.isEmpty() == true) {
                        page = 0
                        showInfo("Empty")
                    }
                }
            }
            val mLayoutManager = LinearLayoutManager(activity)
            rvArt.layoutManager = mLayoutManager
            rvArt.adapter = mAdapter
            rvArt.addOnScrollListener (object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (! recyclerView.canScrollVertically(1)){
                        page++
                        viewModel?.getFavoritesFromLocal(page)
                    }
                }
            })
        }
        viewModel.getFavoritesFromLocal(1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favorite_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == R.id.action_sign_out) {
            AlertDialog.Builder(context)
                .setMessage(getString(R.string.favorite_list_sign_out_sure))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.favorite_list_sign_out_yes) { _, _ ->
                    viewModel.signOut()
                }
                .setNegativeButton(R.string.favorite_list_sign_out_no, null).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            activity?.finish()
            return
        }

        doubleBackToExitPressedOnce = true
        Toast.makeText(context, getString(R.string.sign_in_tap_to_minimize), Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

    private fun showInfo(messageString: String) {
        with (binding) {
            tvInfo.text = if (messageString == "Empty") getString(R.string.favorite_list_empty) else ""
            tvInfo.visibility = if (messageString.isEmpty()) View.GONE else View.VISIBLE
        }
    }
}