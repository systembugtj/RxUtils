package cn.systembug.rx.download;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by systembug on 4/12/16.
 */
public class Downloader implements Observable.OnSubscribe<Response>{

    private OkHttpClient mOkHttpClient;

    public void setOkHttpClient(OkHttpClient client) {
        mOkHttpClient = client;
    }

    public OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient();
        }
        return mOkHttpClient;
    }

    @Override
    public void call(Subscriber<? super Response> subscriber) {

    }
}

