package scut.jlyuan.opendoor;

import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by hero on 15/10/23.
 */
public class HttpUtil {

    public interface LoginService{
        @FormUrlEncoded
        @POST("/userInfo/login.json")
        Call<ResponseBody> login(@Field("tel") String tel, @Field("version") String version, @Field("password") String password);
    }

    public interface OpenDoorService{
        @FormUrlEncoded
        @POST("/door/openDoor.json")
        Call<ResponseBody> open(@Field("communityId") int communityId, @Field("doorIdStr") String doorIdStr, @Header("token") String authorization);
    }

}
