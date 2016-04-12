package cn.systembug.rx.download;

import android.util.Log;

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
public class Downloader {

    public interface OnDownloadChangeListener {
        void startDownload(String url);
        void downloadProgress(int step);
        void downloadComplete();
    }

    private OnDownloadChangeListener mListener;

    private String mUrl = "";
    private OkHttpClient mOkHttpClient;
    private String mLocal = "";

    public Downloader client(OkHttpClient client) {
        mOkHttpClient = client;
        return this;
    }

    public Downloader listener (OnDownloadChangeListener listener) {
        if (listener != null) {
            mListener = listener;
        }
        return this;
    }


    public Downloader url(String url) {
        mUrl = url;
        return this;
    }

    public Downloader local(String local) {
        mLocal = local;
        return this;
    }

    protected OnDownloadChangeListener getListener() {
        if (mListener == null) {
            mListener = new OnDownloadChangeListener() {
                @Override
                public void startDownload(String url) {

                }

                @Override
                public void downloadProgress(int step) {

                }

                @Override
                public void downloadComplete() {

                }
            };
        }
        return mListener;
    }

    public OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient();
        }
        return mOkHttpClient;
    }

    public Observable<String> download() {
        return Observable.create(new Observable.OnSubscribe<Response>() {
                    @Override
                    public void call(Subscriber<? super Response> subscriber) {

                        getListener().startDownload(mUrl);

                        if (mUrl == null || mUrl.length() <= 0) {
                            subscriber.onError(new InvalidParameterException("url should be set."));
                            return;
                        }
                        if (mLocal == null || mLocal.length() <= 0) {
                            subscriber.onError(new InvalidParameterException("local should be set."));
                            return;
                        }

                        mOkHttpClient.newCall(new Request.Builder().url(mUrl).build()).enqueue(new Callback() {
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
                .map(response -> {
                    if (response.isSuccessful()) {
                        BufferedSink sink = null;
                        try {

                            File file = new File(mLocal);

                            sink = Okio.buffer(Okio.sink(file));

                            int DOWNLOAD_CHUNK_SIZE = 2048;
                            long bytesRead = 0;
                            ResponseBody body = response.body();
                            long contentLength = body.contentLength();
                            BufferedSource source = body.source();

                            if (mListener != null) {

                            }
                            getListener().downloadProgress(2);
                            int previous = 0;
                            while (source.read(sink.buffer(), DOWNLOAD_CHUNK_SIZE) != -1) {
                                bytesRead += DOWNLOAD_CHUNK_SIZE;
                                int progress = (int) ((bytesRead * 100) / contentLength);
                                getListener().downloadProgress(progress);
                            }

                            sink.writeAll(source);
                            sink.close();
                            return mLocal;
                        } catch (IOException ex) {
                            try{
                                if(sink != null) {
                                    sink.close();
                                }
                            }catch (Exception ignored) {
                                // ignore exceptions generated by close()
                            }
                        }
                        getListener().downloadComplete();
                        return mLocal;
                    }
                    return ""; // not downloaded, return empty string to ignore it.
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}

