package scut.jlyuan.opendoor

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.NotificationCompat
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import com.alibaba.fastjson.JSONObject
import com.squareup.okhttp.ResponseBody
import retrofit.Callback
import retrofit.Response
import retrofit.Retrofit

import java.util.HashMap

class MainActivity : AppCompatActivity() , View.OnClickListener{
    override fun onClick(p0: View?) {

        when(p0?.id){
            R.id.front -> {login("01")}
            R.id.back -> {login("02")}
            R.id.hall -> {login("0550")}
            R.id.underground -> {login("0551")}
            R.id.show -> {sendBroadCast("notification_show")}
            R.id.hide -> {sendBroadCast("notification_hide")}
        }

    }

    fun sendBroadCast(action: String){
        var intent = Intent()
        intent.setAction(action)
        sendBroadcast(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        findViewById(R.id.front).setOnClickListener(this)
        findViewById(R.id.back).setOnClickListener(this)
        findViewById(R.id.hall).setOnClickListener(this)
        findViewById(R.id.underground).setOnClickListener(this)
        findViewById(R.id.show).setOnClickListener(this)
        findViewById(R.id.hide).setOnClickListener(this)


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

                    Toast.makeText(this@MainActivity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

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

                    Toast.makeText(this@MainActivity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

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
