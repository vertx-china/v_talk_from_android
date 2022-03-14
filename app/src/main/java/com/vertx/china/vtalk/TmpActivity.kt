package com.vertx.china.vtalk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.easysocket.EasySocket
import com.easysocket.entity.OriginReadData
import com.easysocket.entity.SocketAddress
import com.easysocket.interfaces.conn.ISocketActionListener
import com.easysocket.utils.LogUtil
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.fresco.helper.Phoenix
import com.facebook.fresco.helper.photoview.PhotoX
import com.squareup.moshi.Moshi
import com.vertx.china.vtalk.databinding.ActivityTmpBinding
import com.vertx.china.vtalk.utilities.TcpInfoConfig


class TmpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTmpBinding


    private val data = mutableListOf<MessageModel>()

    val moshi: Moshi = Moshi.Builder().build()

    val msgAdapter by lazy {
        DelegateMultiAdapter(mutableListOf())
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
                .toJson(SendMessageModel("$tvmsg (From 面筋's 客户端)", nickname = TcpInfoConfig.nickName))


            // 发送
            EasySocket.getInstance().upMessage(
                (sendStr + "\r\n").toByteArray()
            )

            msgAdapter.addData(MessageModel(message = "$tvmsg (From 面筋's 客户端)", nickname = TcpInfoConfig.nickName, isMine = true))
            binding.sendRecycler.smoothScrollToPosition(msgAdapter.data.size)

            binding.etSengMessage.setText("")

        }
    }
}

class DelegateMultiAdapter(items: MutableList<MessageModel>) : BaseDelegateMultiAdapter<MessageModel, BaseViewHolder>(items) {

    init {
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<MessageModel>() {
            override fun getItemType(data: List<MessageModel>, position: Int): Int {
                return when (data[position].isMine) {
                    true -> {
                        0
                    }
                    false -> {
                        1
                    }
                }
            }
        })


        getMultiTypeDelegate()
            ?.addItemType(0, R.layout.simple_recycler_mine_item)
            ?.addItemType(1, R.layout.simple_recycler_other_item)

    }

    override fun convert(holder: BaseViewHolder, item: MessageModel) {
        when (holder.itemViewType) {
            0 -> {
                showMsgUi(
                    holder,
                    R.id.msg_mine_nickname,
                    R.id.msg_mine_time,
                    R.id.msg_mine_content,
                    R.id.msg_mine_content_img,
                    item
                )
            }
            1 -> {
                showMsgUi(
                    holder,
                    R.id.msg_other_nickname,
                    R.id.msg_other_time,
                    R.id.msg_other_content,
                    R.id.msg_other_content_img,
                    item
                )
            }
        }
    }

    private fun showMsgUi(holder: BaseViewHolder, nickname: Int, time: Int, content: Int, pic: Int, item: MessageModel) {
        holder.setText(nickname, item.nickname)
        holder.setText(time, item.time?.substring(11, 16))

        holder.setGone(content, true)
        holder.setGone(pic, true)

        if (item.message.startsWith("http")) {

            val otherImg = holder.getView(pic) as SimpleDraweeView

            otherImg.setOnClickListener {
                PhotoX.with(context)
                    .setOriginalUrl(item.message)
                    .start()
            }

            holder.setGone(pic, false)
            Phoenix.with(otherImg).setWidth(600)
                .setAspectRatio((5..10).random() / 10f).load(item.message)


        } else {
            holder.setGone(content, false)
            holder.setText(content, item.message)
        }
    }

}


