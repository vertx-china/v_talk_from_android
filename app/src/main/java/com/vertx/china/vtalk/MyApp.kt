package com.vertx.china.vtalk

import android.app.Application
import com.easysocket.EasySocket
import com.easysocket.config.DefaultMessageProtocol
import com.easysocket.config.EasySocketOptions
import com.easysocket.entity.SocketAddress
import com.facebook.fresco.helper.Phoenix
import com.vertx.china.vtalk.utilities.TcpInfoConfig
import com.vertx.china.vtalk.utilities.notNullSingle
import kotlin.properties.Delegates

class MyApp : Application() {

    companion object {
        var instance by Delegates.notNullSingle<MyApp>()
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        Phoenix.init(this)

    }
}