package com.instagram.mini.presenter

import com.instagram.mini.presenter.artdetail.ArtDetailViewModel
import com.instagram.mini.presenter.artlist.ArtListViewModel
import com.instagram.mini.presenter.favorite.FavoriteListViewModel
import com.instagram.mini.presenter.signin.SignInViewModel
import com.instagram.mini.presenter.splash.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val ViewModelModule = module {
    viewModel { SplashViewModel(get()) }
    viewModel { SignInViewModel(get()) }
    viewModel { ArtListViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { ArtDetailViewModel(get(), get()) }
    viewModel { FavoriteListViewModel(get(), get(), get(), get()) }
}