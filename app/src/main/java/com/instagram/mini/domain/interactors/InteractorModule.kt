package com.instagram.mini.domain.interactors

import org.koin.dsl.module

val InteractorModule = module {
    single { GetUserLocalIteractor() }
    single { SetUserLocalIteractor() }
    single { DeleteUserLocalIteractor() }
    single { GetArtsLocalInteractor() }
    single { GetArtsRemoteInteractor() }
    single { SetArtsLocalInteractor() }
    single { GetFavoritesLocalInteractor() }
    single { GetArtLocalInteractor() }
    single { UpdateArtsLocalInteractor() }
    single { SearchArtsRemoteInteractor() }
    single { SearchArtsLocalInteractor() }
    single { GetArtRemoteInteractor() }
}