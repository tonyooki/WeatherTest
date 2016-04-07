package com.mycompany.weathertest.http;

/**
 * Created by zqs12 on 2016/3/9.
 */
public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
