package com.instagram.mini.presenter.splash

import com.instagram.mini.R
import com.instagram.mini.databinding.SplashFragmentBinding
import com.instagram.mini.presenter.base.BaseFragment

class SplashFragment: BaseFragment<SplashFragmentBinding, SplashViewModel>(
    layoutResId = R.layout.splash_fragment,
    SplashViewModel::class
) {

    override fun isActionBarShown(): Boolean = false

    override fun initBinding(binding: SplashFragmentBinding, viewModel: SplashViewModel) {
        super.initBinding(binding, viewModel)
        binding.viewModel = viewModel.also {
            it.loadingIndicator.observe(this, ::loadingIndicator)
            it.alertMessage.observe(this, ::showAlert)
            it.user.observe(this, ::isSignedIn)
        }
    }

}