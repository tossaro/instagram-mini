package com.instagram.mini.external.utils

import android.content.Context
import android.provider.Settings

class DeviceUtil(val context: Context) {
    fun getDeviceId(): String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}