package scut.jlyuan.opendoor

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast

import com.alibaba.fastjson.JSONObject
import com.squareup.okhttp.ResponseBody

import retrofit.Call
import retrofit.Callback
import retrofit.Response
import retrofit.Retrofit

/**
 * Created by hero on 15/10/23.
 */
class OpenDoorWidget : AppWidgetProvider() {

    private var mContext: Context? = null

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)


        for (i in appWidgetIds) {
            val remoteViews = RemoteViews(context.packageName, R.layout.layout_widget)
            remoteViews.setOnClickPendingIntent(R.id.front, newPendingIntent(context, 0, "front"))
            remoteViews.setOnClickPendingIntent(R.id.back, newPendingIntent(context, 1, "back"))
            remoteViews.setOnClickPendingIntent(R.id.hall, newPendingIntent(context, 2, "hall"))
            remoteViews.setOnClickPendingIntent(R.id.underground, newPendingIntent(context, 3, "underground"))
            appWidgetManager.updateAppWidget(i, remoteViews)
        }


    }


    override fun onReceive(context: Context, intent: Intent) {

        val action = intent.action
        mContext = context

        when (action) {
            "front" -> {
                login("01")
                Log.e(javaClass.getSimpleName(), "front")
            }
            "back" -> {
                login("02")
                Log.e(javaClass.getSimpleName(), "back")
            }
            "hall" -> {
                login("0550")
                Log.e(javaClass.getSimpleName(), "hall")
            }
            "underground" -> {
                login("0551")
                Log.e(javaClass.getSimpleName(), "underground")
            }
            else -> Log.e(javaClass.getSimpleName(), action)
        }


        super.onReceive(context, intent)
    }

    private fun newIntent(context: Context, action: String): Intent {
        val intent = Intent()
        intent.setAction(action)
        intent.setClass(context, OpenDoorWidget::class.java)
        return intent
    }

    fun newPendingIntent(context: Context, request_code: Int, action: String): PendingIntent {
        return PendingIntent.getBroadcast(context, request_code, newIntent(context, action), PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun login(id: String) {
        val retrofit = Retrofit.Builder().baseUrl("http://www.uhomecp.com").build()


        val service = retrofit.create<HttpUtil.LoginService>(HttpUtil.LoginService::class.java)
        val call = service.login("18520200580", "4.0", "38213521")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(response: Response<ResponseBody>, retrofit: Retrofit) {
                try {
                    val body = response.body().string()
                    val jsonObject = JSONObject.parseObject(body)
                    Log.e("test2", jsonObject.toJSONString())

                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                    open(jsonObject.getJSONObject("data").getString("accessToken"), id)


                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(javaClass.getSimpleName(), "something wrong")
                }

            }

            override fun onFailure(t: Throwable) {
                Log.e("test", "failure")
            }
        })
    }

    fun open(token: String, id: String) {
        val retrofit = Retrofit.Builder().baseUrl("http://www.uhomecp.com").build()


        val service = retrofit.create<HttpUtil.OpenDoorService>(HttpUtil.OpenDoorService::class.java)
        val call = service.open(385, id, token)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(response: Response<ResponseBody>, retrofit: Retrofit) {
                try {
                    val body = response.body().string()
                    val jsonObject = JSONObject.parseObject(body)
                    Log.e("test2", jsonObject.toJSONString())

                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(javaClass.getSimpleName(), "something wrong")
                }

            }

            override fun onFailure(t: Throwable) {
                Log.e("test", "failure")
            }
        })
    }
}
