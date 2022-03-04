package com.vertx.china.vtalk

import android.app.Application
import com.easysocket.EasySocket
import com.easysocket.config.DefaultMessageProtocol
import com.easysocket.config.EasySocketOptions
import com.easysocket.entity.SocketAddress
import com.facebook.fresco.helper.Phoenix
import com.vertx.china.vtalk.utilities.TcpInfoConfig

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Phoenix.init(this)

        initEasySocket()
    }

    private fun initEasySocket() {
        val options = EasySocketOptions.Builder()
            .setSocketAddress(
                SocketAddress(
                    TcpInfoConfig.ipAddress, 32167
                )
            ) //主机地址
            // 强烈建议定义一个消息协议，方便解决 socket黏包、分包的问题
//            .setReaderProtocol(DefaultMessageProtocol()) // 默认的消息协议
            .build()

        //初始化EasySocket
        EasySocket.getInstance().createConnection(
            options, this
        )
    }
}