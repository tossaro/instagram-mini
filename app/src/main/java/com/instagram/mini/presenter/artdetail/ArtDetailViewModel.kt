package com.instagram.mini.presenter.artdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.instagram.mini.domain.entities.Art
import com.instagram.mini.domain.interactors.GetArtLocalInteractor
import com.instagram.mini.domain.interactors.UpdateArtsLocalInteractor
import com.instagram.mini.presenter.base.BaseViewModel
import kotlinx.coroutines.launch

class ArtDetailViewModel(
    private val getArtLocal: GetArtLocalInteractor,
    private val updateArtsLocal: UpdateArtsLocalInteractor
): BaseViewModel() {
    var art = MutableLiveData<Art>()

    fun getArtFromLocal(id: Int) = viewModelScope.launch {
        getArtLocal(id).let {
            art.value = it
        }
    }

    fun toggleFavorite() = viewModelScope.launch {
        art.value?.let {
            it.favorite = if (it.favorite == 1) 0 else 1
            updateArtsLocal(listOf(it) as MutableList<Art>)
            art.value = it
        }
    }
}