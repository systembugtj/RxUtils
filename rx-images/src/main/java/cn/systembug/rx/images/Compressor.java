package cn.systembug.rx.images;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import cn.systembug.rx.images.util.ImageUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by systembug on 4/17/16.
 */
public class Compressor {
    private boolean mInPlaceConvert = true;
    private String mPath;
    private String mNewPath;
    private int mWidth;
    private int mHeight;
    private int mQuality = 1;

    public Compressor path(String path) {
        mPath = Preconditions.checkNotNull(path);
        return this;
    }

    public Compressor newFile(String path) {
        mNewPath = Preconditions.checkNotNull(path);
        mInPlaceConvert = false;
        return this;
    }

    public Compressor width(int width) {
        Preconditions.checkArgument(width > 0);
        mWidth = width;
        return this;
    }

    public Compressor height (int height) {
        Preconditions.checkArgument(height > 0);
        mHeight = height;
        return this;
    }

    public Compressor quality(int quality) {
        Preconditions.checkArgument(0 <= quality && quality <= 100);
        mQuality = quality;
        return this;
    }

    public Observable<String> compress() {
        return Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        if (Strings.isNullOrEmpty(mPath)) {
                            subscriber.onError(new InvalidParameterException("path should be set."));
                            return;
                        }

                        if (!mInPlaceConvert && Strings.isNullOrEmpty(mNewPath)) {
                            subscriber.onError(new InvalidParameterException("output path should be set."));
                            return;
                        }

                        if (mInPlaceConvert){
                            ImageUtil.compress(mPath, mWidth, mHeight, mQuality);
                        } else {
                            ImageUtil.compress(mPath, mNewPath, mWidth, mHeight, mQuality);
                        }
                        subscriber.onNext(mInPlaceConvert ? mPath : mPath);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
