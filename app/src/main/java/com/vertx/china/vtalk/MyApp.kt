package com.vertx.china.vtalk

import android.app.Application
import com.facebook.fresco.helper.Phoenix
import com.tencent.mmkv.MMKV
import com.vertx.china.vtalk.utilities.notNullSingle
import dagger.hilt.android.HiltAndroidApp
import kotlin.properties.Delegates


@HiltAndroidApp
class MyApp : Application() {

    companion object {
        var instance by Delegates.notNullSingle<MyApp>()
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        Phoenix.init(this)
        MMKV.initialize(this)
    }
}