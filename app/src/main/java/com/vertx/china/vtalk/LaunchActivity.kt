package com.vertx.china.vtalk

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vertx.china.vtalk.databinding.ActivityLaunchBinding
import com.vertx.china.vtalk.databinding.ActivityTmpBinding
import com.vertx.china.vtalk.utilities.TcpInfoConfig

private lateinit var binding: ActivityLaunchBinding

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.tvLogin.setOnClickListener {


            if (!BuildConfig.DEBUG) {
                if (binding.etAddress.text.toString().trim() == "") {
                    Toast.makeText(this, "请输入ip地址", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (binding.etNickname.text.toString().trim() == "") {
                    Toast.makeText(this, "请输入用户昵称", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            if (binding.etAddress.text.toString().trim() != "") {
                TcpInfoConfig.ipAddress = binding.etAddress.text.toString().trim()
            }

            if (binding.etNickname.text.toString().trim() != "") {
                TcpInfoConfig.nickName = binding.etNickname.text.toString().trim()
            }


            startActivity(Intent(this, TmpActivity::class.java))

            finish()

        }


    }

}