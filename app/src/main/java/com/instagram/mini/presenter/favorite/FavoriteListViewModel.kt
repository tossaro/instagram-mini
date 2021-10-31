package com.instagram.mini.presenter.favorite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import com.instagram.mini.domain.entities.Art
import com.instagram.mini.domain.entities.User
import com.instagram.mini.domain.interactors.*
import com.instagram.mini.presenter.base.BaseViewModel
import kotlinx.coroutines.launch

class FavoriteListViewModel(
    private val getFavoritesLocal: GetFavoritesLocalInteractor,
    private val deleteUserLocal: DeleteUserLocalIteractor,
    private val getUserLocal: GetUserLocalIteractor,
    private val updateArtsLocal: UpdateArtsLocalInteractor
) : BaseViewModel() {
    var arts = MutableLiveData<MutableList<Art>?>()
    var user = MutableLiveData<User>()
    var limit = 15
    var page = 1

    init {
        loadingIndicator.value = true
    }

    fun getFavoritesFromLocal(mPage: Int) {
        page = mPage
        viewModelScope.launch {
            val artsTemp = mutableListOf<Art>()
            getFavoritesLocal(limit * (page - 1), limit).let {
                val artsLocal = it
                if (artsLocal.isEmpty() && page == 1) infoMessage.value = "Empty"
                else {
                    infoMessage.value = ""
                    artsLocal.forEach { art -> artsTemp.add(art) }
                }
            }

            arts.value = artsTemp
            loadingIndicator.value = false
        }
    }

    fun toggleFavorite(art: Art) = viewModelScope.launch {
        art.let {
            it.favorite = if (it.favorite == 1) 0 else 1
            updateArtsLocal(listOf(it) as MutableList<Art>)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            deleteUserLocal()
            getUserLocal().let { user.value = it }
        }
    }
}