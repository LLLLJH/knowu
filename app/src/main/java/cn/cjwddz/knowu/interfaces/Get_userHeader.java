package cn.cjwddz.knowu.interfaces;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by K.B. on 2018/11/13.
 */

public interface Get_userHeader {
    void successedGetuserHeader(Call call, Response response)throws IOException;
    void failureGetuserHeader(IOException e);
    void failedGetuserHeader(Call call, Response response)throws IOException;
}
