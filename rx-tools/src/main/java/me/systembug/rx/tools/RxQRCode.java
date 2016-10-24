package me.systembug.rx.tools;

import android.content.Context;
import android.graphics.Bitmap;

import cn.bingoogolapple.qrcode.core.DisplayUtils;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by albert on 9/21/16.
 */
public class RxQRCode {
    public static Observable<Bitmap> createQRCode(String content, Context context,  int size) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                QRCodeEncoder.encodeQRCode(content,
                        DisplayUtils.dp2px(context, size),
                        new QRCodeEncoder.Delegate() {
                            @Override
                            public void onEncodeQRCodeSuccess(Bitmap bitmap) {
                                subscriber.onNext(bitmap);
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onEncodeQRCodeFailure() {
                                subscriber.onError(new Exception("Failed to encode QRCode."));
                            }
                        });
            }
        });
    }
}
