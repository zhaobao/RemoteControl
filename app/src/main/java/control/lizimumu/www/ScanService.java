package control.lizimumu.www;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 *
 * Created by zb on 12/12/2017.
 */

public interface ScanService {

    @GET("action/ping")
    Call<ResponseBody> doScan();
}
