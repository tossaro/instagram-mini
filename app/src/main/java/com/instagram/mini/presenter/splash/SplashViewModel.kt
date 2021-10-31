package com.instagram.mini.presenter.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.instagram.mini.domain.entities.User
import com.instagram.mini.domain.interactors.GetUserLocalIteractor
import com.instagram.mini.presenter.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(private val getUserLocal: GetUserLocalIteractor) : BaseViewModel() {
    var user = MutableLiveData<User>()

    init {
        viewModelScope.launch {
            delay(200)
            getUserLocal().let { user.value = it }
        }
    }
}