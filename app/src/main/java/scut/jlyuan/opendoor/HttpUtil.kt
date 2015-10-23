package scut.jlyuan.opendoor

import com.alibaba.fastjson.JSONObject
import com.squareup.okhttp.ResponseBody

import retrofit.Call
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.Header
import retrofit.http.POST

/**
 * Created by hero on 15/10/23.
 */
class HttpUtil {

    interface LoginService {
        @FormUrlEncoded
        @POST("/userInfo/login.json")
        fun login(@Field("tel") tel: String, @Field("version") version: String, @Field("password") password: String): Call<ResponseBody>
    }

    interface OpenDoorService {
        @FormUrlEncoded
        @POST("/door/openDoor.json")
        fun open(@Field("communityId") communityId: Int, @Field("doorIdStr") doorIdStr: String, @Header("token") authorization: String): Call<ResponseBody>
    }

}
