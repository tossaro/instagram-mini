package com.instagram.mini.presenter.artlist

import android.content.Context
import android.inputmethodservice.Keyboard
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instagram.mini.R
import com.instagram.mini.databinding.ArtListFragmentBinding
import com.instagram.mini.presenter.base.BaseFragment


class ArtListFragment : BaseFragment<ArtListFragmentBinding, ArtListViewModel>(
    layoutResId = R.layout.art_list_fragment,
    ArtListViewModel::class
) {
    private var doubleBackToExitPressedOnce = false
    var mAdapter: ArtAdapter? = null
    var page = 1
    var isInfinite = true

    override fun actionBarTitle(): String = getString(R.string.art_list_title)

    override fun initBinding(binding: ArtListFragmentBinding, viewModel: ArtListViewModel) {
        super.initBinding(binding, viewModel)
        binding.viewModel = viewModel.also {
            it.loadingIndicator.observe(this, ::loadingIndicator)
            it.alertMessage.observe(this, ::showAlert)
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
        hideKeyboard()
        getBottomNav()?.visibility = View.VISIBLE
        with (binding) {
            swipeContainer.setOnRefreshListener {
                mAdapter?.arts?.clear()
                etSearch.setText("")
                isInfinite = true
                getArts(1)
            }
            mAdapter = ArtAdapter()
            mAdapter?.onClick = { art ->
                val bundle = Bundle()
                bundle.putString("title", art.title)
                bundle.putInt("id", art.id)
                findNavController().navigate(R.id.actionToDetailArtFragment, bundle)
            }
            val mLayoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
            rvArt.layoutManager = mLayoutManager
            rvArt.adapter = mAdapter
            rvArt.addOnScrollListener (object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (! recyclerView.canScrollVertically(1) && isInfinite){
                        page++
                        getArts(page)
                    }
                }
            })
            etSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mAdapter?.arts?.clear()
                    viewModel?.searchArts(etSearch.text.toString())
                    isInfinite = etSearch.text.toString().isEmpty()
                    hideKeyboard()
                }
                return@setOnEditorActionListener true
            }
        }
        getArts(1)
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

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!
            .isConnected
    }

    fun getArts(p: Int) {
        page = p
        if (viewModel.isFromLocal != !isNetworkAvailable()) {
            page = 1
            mAdapter?.arts?.clear()
        }
        viewModel.isFromLocal = !isNetworkAvailable()
        viewModel.getArts(page)
    }
}