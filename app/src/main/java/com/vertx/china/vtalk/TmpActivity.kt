package com.vertx.china.vtalk

import android.os.Bundle
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseProviderMultiAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.easysocket.EasySocket
import com.easysocket.entity.OriginReadData
import com.easysocket.entity.SocketAddress
import com.easysocket.interfaces.conn.ISocketActionListener
import com.easysocket.utils.LogUtil
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.fresco.helper.Phoenix
import com.squareup.moshi.Moshi
import com.vertx.china.vtalk.databinding.ActivityTmpBinding
import com.vertx.china.vtalk.utilities.TcpInfoConfig

class TmpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTmpBinding


    val data = mutableListOf<MessageModel>()

    val moshi = Moshi.Builder().build()

    val msgAdapter by lazy {
        MsgAdapter(mutableListOf())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTmpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        EasySocket.getInstance().subscribeSocketAction(object : ISocketActionListener {
            override fun onSocketConnSuccess(socketAddress: SocketAddress?) {
                LogUtil.d("---> 连接成功");
            }

            override fun onSocketConnFail(socketAddress: SocketAddress?, isNeedReconnect: Boolean) {
            }

            override fun onSocketDisconnect(socketAddress: SocketAddress?, isNeedReconnect: Boolean) {
            }

            override fun onSocketResponse(socketAddress: SocketAddress?, originReadData: OriginReadData?) {


                LogUtil.d("originReadData收到数据-->" + originReadData?.getBodyString());
            }

            override fun onSocketResponse(socketAddress: SocketAddress?, readData: String) {

                LogUtil.i("xxxxxx", readData)


                val tmp = readData.split("\r\n")

                tmp.forEach {

                    if (it != "") {
                        try {
                            val xx = moshi.adapter(MessageModel::class.java).fromJson(it)
                            msgAdapter.addData(xx!!)
                        } catch (e: Exception) {

                        }

                        binding.sendRecycler.smoothScrollToPosition(msgAdapter.data.size)

                    }
                }


            }

            override fun onSocketResponse(socketAddress: SocketAddress?, readData: ByteArray?) {
            }
        })



        binding.sendRecycler.adapter = msgAdapter

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        binding.sendRecycler.layoutManager = linearLayoutManager
        msgAdapter.setNewInstance(data)


        binding.btnSend.setOnClickListener {


            val tvmsg = binding.etSengMessage.text.toString().trim()

            if (tvmsg == "") return@setOnClickListener


            val sendStr = moshi.adapter(SendMessageModel::class.java)
                .toJson(SendMessageModel(tvmsg, nickname = TcpInfoConfig.nickName))


            // 发送
            EasySocket.getInstance().upMessage(
                (sendStr + "\r\n").toByteArray()
            )

            msgAdapter.addData(MessageModel(message = tvmsg, nickname = TcpInfoConfig.nickName, isMine = true))
            binding.sendRecycler.smoothScrollToPosition(msgAdapter.data.size)

            binding.etSengMessage.setText("")

        }
    }
}


class MsgAdapter(items: MutableList<MessageModel>) : BaseQuickAdapter<MessageModel, BaseViewHolder>(
    R.layout.simple_recycler_item, items
) {
    override fun convert(holder: BaseViewHolder, item: MessageModel) {

        holder.setGone(R.id.other_group, true)
        holder.setGone(R.id.mine_group, true)

        if (item.isMine) {
            holder.setGone(R.id.mine_group, false)

            holder.setText(R.id.msg_mine_nickname, item.nickname)

            holder.setGone(R.id.msg_mine_content, true)
            holder.setGone(R.id.msg_mine_content_img, true)

            if (item.message.startsWith("http")) {
                holder.setGone(R.id.msg_mine_content_img, false)
                Phoenix.with(holder.getView(R.id.msg_mine_content_img) as SimpleDraweeView).load(item.message)
            } else {
                holder.setGone(R.id.msg_mine_content, false)
                holder.setText(R.id.msg_mine_content, item.message)
            }

        } else {

            holder.setGone(R.id.other_group, false)

            holder.setText(R.id.msg_other_nickname, item.nickname)

            holder.setGone(R.id.msg_other_content, true)
            holder.setGone(R.id.msg_other_content_img, true)

            if (item.message.startsWith("http")) {
                holder.setGone(R.id.msg_other_content_img, false)
                Phoenix.with(holder.getView(R.id.msg_other_content_img) as SimpleDraweeView).load(item.message)
            } else {
                holder.setGone(R.id.msg_other_content, false)
                holder.setText(R.id.msg_other_content, item.message)
            }
        }
    }
}



