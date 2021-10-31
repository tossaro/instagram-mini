package com.instagram.mini.external.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import com.instagram.mini.BuildConfig
import com.instagram.mini.external.constants.HttpHeaderConstant.APP_VERSION
import com.instagram.mini.external.constants.HttpHeaderConstant.DEVICE_ID
import com.instagram.mini.external.constants.TimeoutConstant.OKHTTP_CONNECTION_TIMEOUT
import com.instagram.mini.external.constants.TimeoutConstant.OKHTTP_READ_TIMEOUT
import com.instagram.mini.external.constants.TimeoutConstant.OKHTTP_WRITE_TIMEOUT
import com.readystatesoftware.chuck.ChuckInterceptor
import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object NetworkUtil {
    fun buildClient(applicationContext: Context): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        val builder = OkHttpClient.Builder()
        try {
            val pInfo: PackageInfo =
                applicationContext.packageManager.getPackageInfo(applicationContext.packageName, 0)
            val version = pInfo.versionName
            val requestInterceptor = Interceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header(APP_VERSION, version)
                    .header(DEVICE_ID, DeviceUtil(applicationContext).getDeviceId())
                    .method(original.method, original.body)
                    .build()
                chain.proceed(request)
            }

            builder.connectTimeout(OKHTTP_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            builder.readTimeout(OKHTTP_READ_TIMEOUT, TimeUnit.SECONDS)
            builder.writeTimeout(OKHTTP_WRITE_TIMEOUT, TimeUnit.SECONDS)
            builder.addInterceptor(httpLoggingInterceptor)
            if (BuildConfig.DEBUG) builder.addNetworkInterceptor(StethoInterceptor())
            builder.addInterceptor(ChuckInterceptor(applicationContext))
            builder.addNetworkInterceptor(requestInterceptor)
            builder.connectionSpecs(listOf(ConnectionSpec.COMPATIBLE_TLS))
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return builder.build()
    }

    inline fun <reified T> buildService(baseUrl: String, okHttpClient: OkHttpClient): T {
        val gson = GsonBuilder()
            .enableComplexMapKeySerialization()
            .serializeNulls()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .setPrettyPrinting()
            .setVersion(1.0)
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .build()

        return retrofit.create(T::class.java)
    }
}