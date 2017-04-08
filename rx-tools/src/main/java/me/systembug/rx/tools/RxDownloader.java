package me.systembug.rx.tools;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by systembug on 4/12/16.
 */
public class RxDownloader {

    private String mUrl = "";
    private OkHttpClient mOkHttpClient;
    private String mLocal = "";

    public static class Builder {
        private String mUrl = "";
        private OkHttpClient mOkHttpClient;
        private String mLocal = "";

        public Builder client(OkHttpClient client) {
            mOkHttpClient = Preconditions.checkNotNull(client);
            return this;
        }

        public Builder url(String url) {
            mUrl =  Preconditions.checkNotNull(url);
            return this;
        }

        public Builder local(String local) {
            mLocal =  Preconditions.checkNotNull(local);
            return this;
        }

        public RxDownloader build() {
            return new RxDownloader(mUrl, mOkHttpClient, mLocal);
        }
    }

    private RxDownloader(String url, OkHttpClient client, String local) {
        mLocal = local;
        mOkHttpClient = client;
        mUrl = url;
    }

    public OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient();
        }
        return mOkHttpClient;
    }

    public Observable<Integer> download() {
        return Observable.create(new Observable.OnSubscribe<Response>() {
                    @Override
                    public void call(Subscriber<? super Response> subscriber) {
                        if (Strings.isNullOrEmpty(mUrl)) {
                            subscriber.onError(new InvalidParameterException("url should be set."));
                            return;
                        }
                        if (Strings.isNullOrEmpty(mLocal)) {
                            subscriber.onError(new InvalidParameterException("local should be set."));
                            return;
                        }

                        getOkHttpClient().newCall(new Request.Builder().url(mUrl).build()).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    subscriber.onError(e);
                                }

                                @Override
                                public void onResponse(Call call, final Response response) throws IOException {
                                    if (!response.isSuccessful()) {
                                        subscriber.onError(new IOException("Unexpected code " + response));
                                    } else {
                                        subscriber.onNext(response);
                                        subscriber.onCompleted();
                                    }
                                }
                            });
                    }
                })
                .flatMap(response -> Observable.create(new Observable.OnSubscribe<Integer>() {
                            @Override
                            public void call(Subscriber<? super Integer> subscriber) {
                                BufferedSink sink = null;
                                try {

                                    File file = new File(mLocal);

                                    sink = Okio.buffer(Okio.sink(file));

                                    int DOWNLOAD_CHUNK_SIZE = 2048;
                                    long bytesRead = 0;
                                    ResponseBody body = response.body();
                                    long contentLength = body.contentLength();
                                    BufferedSource source = body.source();

                                    while (source.read(sink.buffer(), DOWNLOAD_CHUNK_SIZE) != -1) {
                                        bytesRead += DOWNLOAD_CHUNK_SIZE;
                                        int progress = (int) ((bytesRead * 100) / contentLength);
                                        subscriber.onNext(new Integer(progress));
                                    }

                                    sink.writeAll(source);
                                    sink.close();
                                    subscriber.onCompleted();
                                } catch (IOException ex) {
                                    try {
                                        if (sink != null) {
                                            sink.close();
                                        }
                                    } catch (Exception ignored) {
                                        // ignore exceptions generated by close()
                                    }
                                }
                            }
                        }))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }
}

