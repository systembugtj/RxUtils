package me.systembug.rx.tools;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.File;
import java.security.InvalidParameterException;

import me.systembug.utils.Images;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by systembug on 4/17/16.
 */
public class RxCompressor {

    // if less than this length, will not be converted.
    public static long DEFAULT_MIN_LENGTH = 2097152;

    private boolean mInPlaceConvert = true;
    private String mPath;
    private String mNewPath;
    private int mWidth;
    private int mHeight;
    private int mQuality = 1;
    private long mMinLength = DEFAULT_MIN_LENGTH;

    private RxCompressor() {

    }
    public final static RxCompressor newInstance() {
        return new RxCompressor();
    }

    public RxCompressor path(String path) {
        mPath = Preconditions.checkNotNull(path);
        return this;
    }

    public RxCompressor newFile(String path) {
        mNewPath = Preconditions.checkNotNull(path);
        mInPlaceConvert = false;
        return this;
    }

    public RxCompressor width(int width) {
        Preconditions.checkArgument(width > 0);
        mWidth = width;
        return this;
    }

    public RxCompressor minLength(Long min) {
        Preconditions.checkArgument(min > 0);
        mMinLength = min;
        return this;
    }

    public RxCompressor height (int height) {
        Preconditions.checkArgument(height > 0);
        mHeight = height;
        return this;
    }

    public RxCompressor quality(int quality) {
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

                        File file = new File(mPath);

                        if (file.length() > mMinLength) {
                            if (mInPlaceConvert) {
                                Images.compress(mPath, mWidth, mHeight, mQuality);
                            } else {
                                Images.compress(mPath, mNewPath, mWidth, mHeight, mQuality);
                            }
                        }
                        subscriber.onNext(mInPlaceConvert ? mPath : mPath);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
