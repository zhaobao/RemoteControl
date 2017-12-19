package control.lizimumu.www.remotecontrol;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by zb on 12/12/2017.
 */

public interface ActionService {

    @GET("action/{direction}")
    Call<ResponseBody> doAction(@Path("direction") String direction);
}
