package control.lizimumu.www.remotecontrol;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Action
 * Created by zb on 13/12/2017.
 */

class ActionApi {

    void newScanTask(final String baseUrl) {
        Log.d("request", "scanning " + baseUrl);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(new OkHttpClient.Builder().connectTimeout(200, TimeUnit.MILLISECONDS).build())
                .build();
        ScanService service = retrofit.create(ScanService.class);
        Call<ResponseBody> call = service.doScan();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    DevicesManager.getsInstance().addDevice(baseUrl + "|" + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DevicesManager.getsInstance().scanNext();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                DevicesManager.getsInstance().scanNext();
            }
        });
    }

    static void doAction(final String action, String address, final Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(address)
                .client(new OkHttpClient.Builder().connectTimeout(2000, TimeUnit.MILLISECONDS).build())
                .build();
        ActionService service = retrofit.create(ActionService.class);
        Call<ResponseBody> call = service.doAction(action);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    ResponseBody r = response.body();
                    Log.v("header", response.headers().toString());
                    if (r != null) {
                        String msg = r.string();
                        Log.v("response", msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                t.printStackTrace();
                Toast.makeText(context, R.string.no_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
