package com.infusiblecoder.cryptotracker

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.room.Room
import com.facebook.ads.AudienceNetworkAds
import com.infusiblecoder.cryptotracker.data.database.CryptoTrackerDatabase
import com.facebook.stetho.Stetho
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.onesignal.OneSignal
import com.infusiblecoder.cryptotracker.utils.AppOpenManager
import com.onesignal.debug.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.Timber.DebugTree


class MyApplication : Application() {
    var appOpenManager: AppOpenManager? = null

    companion object {

        private const val DATABASE_NAME = "cryptotracker.db"

        private lateinit var appContext: Context
        var database: CryptoTrackerDatabase? = null

        @JvmStatic
        fun getGlobalAppContext(): Context {
            return appContext
        }
    }

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

//        if (BuildConfig.DEBUG) {
//            Timber.plant(DebugTree())
//            Stetho.initializeWithDefaults(this)
//        } else {
            Timber.plant(CrashReportingTree())
//        }

        database = Room.databaseBuilder(this, CryptoTrackerDatabase::class.java, DATABASE_NAME).allowMainThreadQueries().build()
        MobileAds.initialize(
            this
        ) { }


        appOpenManager = AppOpenManager(this)
        AudienceNetworkAds.initialize(this)


        // Verbose Logging set to help debug issues, remove before releasing your app.
        OneSignal.Debug.logLevel = LogLevel.VERBOSE

        // OneSignal Initialization
        OneSignal.initWithContext(this, getString(R.string.onesignalid))

        // requestPermission will show the native Android notification permission prompt.
        // NOTE: It's recommended to use a OneSignal In-App Message to prompt instead.
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(false)
        }

    }

    /** A tree which logs important information for crash reporting.  */
    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
            if (priority == Log.ERROR) {
                FirebaseCrashlytics.getInstance().log("E/$tag:$message")
            } else if (priority == Log.WARN) {
                FirebaseCrashlytics.getInstance().log("W/$tag:$message")
            }
        }
    }
}
