package scut.jlyuan.opendoor;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by hero on 15/10/23.
 */
public class OpenDoorWidget extends AppWidgetProvider {

    private Context mContext;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);


        for (int i = 0; i < appWidgetIds.length; i ++){
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
            remoteViews.setOnClickPendingIntent(R.id.front, newPendingIntent(context, 0, "front"));
            remoteViews.setOnClickPendingIntent(R.id.back, newPendingIntent(context, 1, "back"));
            remoteViews.setOnClickPendingIntent(R.id.hall, newPendingIntent(context, 2, "hall"));
            remoteViews.setOnClickPendingIntent(R.id.underground, newPendingIntent(context, 3, "underground"));
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }



    }


    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        mContext = context;

        switch (action) {
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
            case "underground":
                login("0551");
                Log.e(getClass().getSimpleName(), "underground");
                break;
            default:
                Log.e(getClass().getSimpleName(), action);
        }


        super.onReceive(context, intent);
    }

    private Intent newIntent(Context context, String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.setClass(context, OpenDoorWidget.class);
        return intent;
    }

    public PendingIntent newPendingIntent(Context context, int request_code, String action) {
        return PendingIntent.getBroadcast(context, request_code, newIntent(context, action), PendingIntent.FLAG_UPDATE_CURRENT);
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

                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

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
                    Log.e("test2", jsonObject.toJSONString());

                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

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
