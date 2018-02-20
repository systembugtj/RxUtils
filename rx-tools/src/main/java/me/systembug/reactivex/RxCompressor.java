package me.systembug.reactivex;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.File;
import java.security.InvalidParameterException;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;
import me.systembug.utils.Images;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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

    public Single<String> compress() {
        return Single.create(new SingleOnSubscribe<String>() {
                    @Override
                    public void subscribe(@NonNull SingleEmitter<String> e) throws Exception {
                        if (Strings.isNullOrEmpty(mPath)) {
                            e.onError(new InvalidParameterException("path should be set."));
                            return;
                        }

                        if (!mInPlaceConvert && Strings.isNullOrEmpty(mNewPath)) {
                            e.onError(new InvalidParameterException("output path should be set."));
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
                        e.onSuccess(mInPlaceConvert ? mPath : mPath);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
