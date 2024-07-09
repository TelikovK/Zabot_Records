package ru.vb.practice.App

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import ru.vb.practice.di.appModule
import ru.vb.practice.di.domainModule
import ru.vb.practice.di.dataModule


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule,domainModule,dataModule)
        }
    }

}