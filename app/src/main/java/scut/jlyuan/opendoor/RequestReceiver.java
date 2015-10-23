package scut.jlyuan.opendoor;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by hero on 15/10/23.
 */

public class RequestReceiver extends BroadcastReceiver {

    private Notification mNotification;
    private NotificationManagerCompat mNotificationManagerCompat;
    private Context mContext;
    private static int ID = 1024;


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        mContext = context;

        switch (action) {
            case "show":
                showNotification();
                Log.e(getClass().getSimpleName(), "show");
                break;
            case "hide":
                hideNotification();
                Log.e(getClass().getSimpleName(), "hide");
                break;
            case "front":
                login("01");
                Log.e(getClass().getSimpleName(), "front");
                break;
            case "back":
                login("02");
                Log.e(getClass().getSimpleName(), "back");
                break;
            case "hall":
                login("0550");
                Log.e(getClass().getSimpleName(), "hall");
                break;
            default:
                Log.e(getClass().getSimpleName(), "default");
        }
    }

    private NotificationManagerCompat getNotificationManagerCompat() {
        if (mNotificationManagerCompat == null) {
            mNotificationManagerCompat = NotificationManagerCompat.from(mContext);
        }
        return mNotificationManagerCompat;
    }

    private Intent newIntent(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        return intent;
    }

    public PendingIntent newPendingIntent(int request_code, String action) {
        return PendingIntent.getBroadcast(mContext, request_code, newIntent(action), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private NotificationCompat.Builder getBuilder(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setContentTitle("Open the door");
        builder.setContentText("Choose a door");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setOngoing(true);
        builder.setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle());
        builder.addAction(R.mipmap.ic_launcher, "正门", newPendingIntent(0, "front"));
        builder.addAction(R.mipmap.ic_launcher, "后门", newPendingIntent(1, "back"));
        builder.addAction(R.mipmap.ic_launcher, "大堂", newPendingIntent(2, "hall"));

        return builder;
    }

    private void createNotification() {

        mNotification = getBuilder().build();

    }

    private void showNotification() {
        if (mNotification == null) {
            createNotification();
        }
//        if (mNotificationManagerCompat != null) {
        getNotificationManagerCompat().notify(ID, mNotification);
//            getPreferences(MODE_PRIVATE).edit().putBoolean("isShowing", true).commit();
//        }
    }

    private void hideNotification() {
        getNotificationManagerCompat().cancel(ID);
    }

    private void login(final String id){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.uhomecp.com")
                .build();


        HttpUtil.LoginService service = retrofit.create(HttpUtil.LoginService.class);
        Call<ResponseBody> call = service.login("18520200580", "4.0", "38213521");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                try {
                    String body = response.body().string();
                    JSONObject jsonObject = JSONObject.parseObject(body);
                    Log.e("test2", jsonObject.toJSONString());

                    updateNotification(jsonObject.getString("message"));
                    open(jsonObject.getJSONObject("data").getString("accessToken"), id);


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(getClass().getSimpleName(), "something wrong");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("test", "failure");
            }
        });
    }

    private void updateNotification(String content){

        mNotification = getBuilder().setContentText(content).build();
        getNotificationManagerCompat().notify(ID, mNotification);

    }

    private void open(String token, String id){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.uhomecp.com")
                .build();


        HttpUtil.OpenDoorService service = retrofit.create(HttpUtil.OpenDoorService.class);
        Call<ResponseBody> call = service.open(385, id, token);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                try {
                    String body = response.body().string();
                    JSONObject jsonObject = JSONObject.parseObject(body);
                    updateNotification(jsonObject.getString("message"));

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(getClass().getSimpleName(), "something wrong");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("test", "failure");
            }
        });
    }



}
