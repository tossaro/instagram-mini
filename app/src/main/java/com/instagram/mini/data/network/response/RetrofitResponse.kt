package com.instagram.mini.data.network.response

import com.google.gson.annotations.SerializedName

data class RetrofitResponse<T>(@SerializedName("data") var data: T)