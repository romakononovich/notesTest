package xyz.romakononovich.notes

import android.support.multidex.MultiDexApplication
import io.realm.Realm
import io.realm.RealmConfiguration


/**
 * Created by romank on 27.01.18.
 */
class App: MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder().build()
        Realm.setDefaultConfiguration(config)
    }
}