package com.vertx.china.vtalk

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.easysocket.EasySocket
import com.easysocket.config.EasySocketOptions
import com.easysocket.entity.SocketAddress
import com.tencent.mmkv.MMKV
import com.vertx.china.vtalk.databinding.ActivityLaunchBinding
import com.vertx.china.vtalk.utilities.TcpInfoConfig
import com.vertx.china.vtalk.utilities.isEmpty

private lateinit var binding: ActivityLaunchBinding

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val address: String = MMKV.defaultMMKV().decodeString("Address", "")!!
        val nickname: String = MMKV.defaultMMKV().decodeString("Nickname", "")!!


        binding.tvLogin.setOnClickListener {


            if (address != "" && nickname != "") {
                TcpInfoConfig.ipAddress = address
                TcpInfoConfig.nickName = nickname

                goToMainPage()

                return@setOnClickListener

            }

            if (binding.etAddress.isEmpty()) {
                Toast.makeText(this, "请输入ip地址", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                TcpInfoConfig.ipAddress = binding.etAddress.text.toString().trim()
                MMKV.defaultMMKV().encode("Address", binding.etAddress.text.toString().trim())
            }

            if (binding.etNickname.isEmpty()) {
                Toast.makeText(this, "请输入用户昵称", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                TcpInfoConfig.nickName = binding.etNickname.text.toString().trim()
                MMKV.defaultMMKV().encode("Nickname", binding.etNickname.text.toString().trim())
            }

            goToMainPage()

        }


    }

    private fun goToMainPage() {
        initEasySocket()
        startActivity(Intent(this, TmpActivity::class.java))
        finish()
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
            options, MyApp.instance
        )
    }

}