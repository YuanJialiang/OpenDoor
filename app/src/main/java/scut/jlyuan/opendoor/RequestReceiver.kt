package scut.jlyuan.opendoor

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.app.NotificationCompat
import android.util.Log

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.squareup.okhttp.ResponseBody

import java.io.IOException

import retrofit.Call
import retrofit.Callback
import retrofit.Response
import retrofit.Retrofit

/**
 * Created by hero on 15/10/23.
 */

class RequestReceiver : BroadcastReceiver() {

    private var mNotification: Notification? = null
    private var mNotificationManagerCompat: NotificationManagerCompat? = null
    private var mContext: Context? = null


    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        mContext = context

        when (action) {
            "notification_show" -> {
                showNotification()
                Log.e(javaClass.simpleName, "show")
            }
            "notification_hide" -> {
                hideNotification()
                Log.e(javaClass.simpleName, "hide")
            }
            "notification_front" -> {
                login("01")
                Log.e(javaClass.simpleName, "front")
            }
            "notification_back" -> {
                login("02")
                Log.e(javaClass.simpleName, "back")
            }
            "notification_hall" -> {
                login("0550")
                Log.e(javaClass.simpleName, "hall")
            }
            else -> Log.e(javaClass.simpleName, "default")
        }
    }

    private val notificationManagerCompat: NotificationManagerCompat?
        get() {
            if (mNotificationManagerCompat == null) {
                mNotificationManagerCompat = NotificationManagerCompat.from(mContext)
            }
            return mNotificationManagerCompat
        }

    private fun newIntent(action: String): Intent {
        val intent = Intent()
        intent.setAction(action)
        return intent
    }

    fun newPendingIntent(request_code: Int, action: String): PendingIntent {
        return PendingIntent.getBroadcast(mContext, request_code, newIntent(action), PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private val builder: NotificationCompat.Builder
        get() {
            val builder = NotificationCompat.Builder(mContext)
            builder.setContentTitle("Open the door")
            builder.setContentText("Choose a door")
            builder.setSmallIcon(android.R.drawable.ic_menu_share)
            builder.setOngoing(true)
            builder.setStyle(android.support.v4.app.NotificationCompat.BigTextStyle())
            builder.addAction(android.R.drawable.ic_menu_share, "正门", newPendingIntent(0, "notification_front"))
            builder.addAction(android.R.drawable.ic_menu_share, "后门", newPendingIntent(1, "notification_back"))
            builder.addAction(android.R.drawable.ic_menu_share, "大堂", newPendingIntent(2, "notification_hall"))

            return builder
        }

    private fun createNotification() {

        mNotification = builder.build()

    }

    private fun showNotification() {
        if (mNotification == null) {
            createNotification()
        }
        notificationManagerCompat?.notify(ID, mNotification)
    }

    private fun hideNotification() {
        notificationManagerCompat?.cancel(ID)
        mNotification = null
    }

    private fun login(id: String) {
        val retrofit = Retrofit.Builder().baseUrl("http://www.uhomecp.com").build()


        val service = retrofit.create(HttpUtil.LoginService::class.java)
        val call = service.login("18520200580", "4.0", "38213521")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(response: Response<ResponseBody>, retrofit: Retrofit) {
                try {
                    val body = response.body().string()
                    val jsonObject = JSONObject.parseObject(body)
                    Log.e("test2", jsonObject.toJSONString())

                    updateNotification(jsonObject.getString("message"))
                    open(jsonObject.getJSONObject("data").getString("accessToken"), id)


                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(javaClass.simpleName, "something wrong")
                }

            }

            override fun onFailure(t: Throwable) {
                Log.e("test", "failure")
            }
        })
    }

    private fun updateNotification(content: String) {

        mNotification = builder.setContentText(content).build()
        notificationManagerCompat?.notify(ID, mNotification)

    }

    private fun open(token: String, id: String) {
        val retrofit = Retrofit.Builder().baseUrl("http://www.uhomecp.com").build()


        val service = retrofit.create(HttpUtil.OpenDoorService::class.java)
        val call = service.open(385, id, token)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(response: Response<ResponseBody>, retrofit: Retrofit) {
                try {
                    val body = response.body().string()
                    val jsonObject = JSONObject.parseObject(body)
                    updateNotification(jsonObject.getString("message"))
                    Log.e("test2", jsonObject.toJSONString())

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(javaClass.simpleName, "something wrong")
                }

            }

            override fun onFailure(t: Throwable) {
                Log.e("test", "failure")
            }
        })
    }

    companion object {
        private val ID = 1024
    }


}
