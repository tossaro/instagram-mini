package com.instagram.mini.data.network

import com.instagram.mini.BuildConfig
import com.instagram.mini.data.network.services.ArtServiceV1
import com.instagram.mini.external.utils.NetworkUtil
import org.koin.dsl.module

val NetworkModule = module {
    single { NetworkUtil.buildClient(get()) }
    single { NetworkUtil.buildService<ArtServiceV1>(BuildConfig.SERVER_V1, get()) }
}