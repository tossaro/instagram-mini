package com.instagram.mini

import android.app.Application
import com.instagram.mini.data.storage.DatabaseModule
import com.instagram.mini.data.network.NetworkModule
import com.instagram.mini.domain.interactors.InteractorModule
import com.instagram.mini.domain.repositories.RepositoryModule
import com.instagram.mini.presenter.ViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.EmptyLogger
import java.util.*

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            if (BuildConfig.DEBUG) androidLogger() else EmptyLogger()
            androidContext(applicationContext)
            modules(listOf(
                DatabaseModule,
                NetworkModule,
                RepositoryModule,
                InteractorModule,
                ViewModelModule
            ))
        }
        setLocale()
    }

    private fun setLocale() {
        val locale = Locale("id")
        Locale.setDefault(locale)
        val config = resources.configuration
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)
    }

}