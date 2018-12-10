package cn.cjwddz.knowu.service;

import android.support.annotation.Nullable;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by K.B. on 2018/10/18.
 */

public interface MyInterface {
    void successed(Call call, Response response)throws IOException;
    void failure(IOException e);
    void failed(Call call, Response response)throws IOException;
    void register();
}
