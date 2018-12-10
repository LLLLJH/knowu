package cn.cjwddz.knowu.interfaces;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by K.B. on 2018/11/13.
 */

public interface Get_M_callback {
    void successedGetM(Call call, Response response)throws IOException;
    void failureGetM(IOException e);
    void failedGetM(Call call, Response response)throws IOException;
}
