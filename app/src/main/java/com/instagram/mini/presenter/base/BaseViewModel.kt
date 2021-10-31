package com.instagram.mini.presenter.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel: ViewModel() {
    val loadingIndicator = MutableLiveData<Boolean>().apply { false }
    val alertMessage = MutableLiveData<String>()
    val infoMessage = MutableLiveData<String>()
}