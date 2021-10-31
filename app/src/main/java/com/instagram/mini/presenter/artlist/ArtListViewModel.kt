package com.instagram.mini.presenter.artlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import com.instagram.mini.domain.entities.Art
import com.instagram.mini.domain.entities.User
import com.instagram.mini.domain.interactors.*
import com.instagram.mini.presenter.base.BaseViewModel
import kotlinx.coroutines.launch

class ArtListViewModel(
    private val getArtsRemote: GetArtsRemoteInteractor,
    private val getArtsLocal: GetArtsLocalInteractor,
    private val setArtsLocal: SetArtsLocalInteractor,
    private val searchArtsRemote: SearchArtsRemoteInteractor,
    private val searchArtsLocal: SearchArtsLocalInteractor,
    private val getArtRemote: GetArtRemoteInteractor
) : BaseViewModel() {
    var arts = MutableLiveData<MutableList<Art>?>()
    var user = MutableLiveData<User>()
    var isFromLocal = true
    var limit = 15

    init {
        loadingIndicator.value = true
    }

    fun getArts(page: Int) {
        if (isFromLocal) getArtsFromLocal(page)
        else getArtsFromRemote(page)
    }

    private fun getArtsFromLocal(page: Int) = viewModelScope.launch {
        val artsTemp = mutableListOf<Art>()
        val artsLocal = getArtsLocal(limit * (page - 1), limit).toMutableList()
        artsLocal.forEach { art -> artsTemp.add(art) }
        arts.value = artsTemp
        loadingIndicator.value = false
    }

    private fun getArtsFromRemote(page: Int) = viewModelScope.launch {
        loadingIndicator.value = true
        when (val response = getArtsRemote(limit, page)) {
            is NetworkResponse.Success -> {
                response.body.let { r ->
                    if (isFromLocal) {
                        arts.value = null
                        isFromLocal = false
                    }
                    val artsTemp = mutableListOf<Art>()
                    r.data.forEach { art ->
                        artsTemp.add(art)
                    }
                    arts.value = artsTemp
                    setArtsLocal(artsTemp)
                    loadingIndicator.value = false
                }
            }
            is NetworkResponse.ServerError -> {
                isFromLocal = true
                arts.value = null
                getArtsFromLocal(1)
                loadingIndicator.value = false
                alertMessage.value = response.body?.message.orEmpty()
            }
            is NetworkResponse.NetworkError -> {
                isFromLocal = true
                arts.value = null
                getArtsFromLocal(1)
                loadingIndicator.value = false
                alertMessage.value = response.error.message.orEmpty()
            }
        }
    }

    fun searchArts(search: String) {
        if (isFromLocal) searchArtsFromLocal(search)
        else searchArtsFromRemote(search)
    }

    private fun searchArtsFromLocal(search: String) = viewModelScope.launch {
        val artsTemp = mutableListOf<Art>()
        val artsLocal = searchArtsLocal(search)
        artsLocal.forEach { art -> artsTemp.add(art) }
        arts.value = artsTemp
        loadingIndicator.value = false
    }

    private fun searchArtsFromRemote(search: String) = viewModelScope.launch {
        loadingIndicator.value = true
        when (val response = searchArtsRemote(search)) {
            is NetworkResponse.Success -> {
                response.body.let { r ->
                    if (isFromLocal) {
                        arts.value = null
                        isFromLocal = false
                    }
                    val artsTemp = mutableListOf<Art>()
                    r.data.forEach { art ->
                        when (val response = getArtRemote(art.id)) {
                            is NetworkResponse.Success -> {
                                response.body.let { r2 ->
                                    artsTemp.add(r2.data)
                                }
                            }
                        }
                    }
                    arts.value = artsTemp
                    setArtsLocal(artsTemp)
                    loadingIndicator.value = false
                }
            }
            is NetworkResponse.ServerError -> {
                isFromLocal = true
                arts.value = null
                getArtsFromLocal(1)
                loadingIndicator.value = false
                alertMessage.value = response.body?.message.orEmpty()
            }
            is NetworkResponse.NetworkError -> {
                isFromLocal = true
                arts.value = null
                getArtsFromLocal(1)
                loadingIndicator.value = false
                alertMessage.value = response.error.message.orEmpty()
            }
        }
    }
}