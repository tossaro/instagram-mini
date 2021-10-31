package com.instagram.mini.data.network.request

import android.os.Build
import com.google.gson.annotations.SerializedName

abstract class DeviceAuthRequest(
    @SerializedName("uuid") private val uuid: String,
    @SerializedName("device_token") private val deviceToken: String,
    @SerializedName("manufacturer") private val manufacturer: String = Build.MANUFACTURER,
    @SerializedName("operating_system") private val operatingSystem: String = "Android",
    @SerializedName("os_version") private val osVersion: String = Build.VERSION.RELEASE
)