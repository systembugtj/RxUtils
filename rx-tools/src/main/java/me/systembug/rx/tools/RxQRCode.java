package me.systembug.rx.tools;

import android.content.Context;
import android.graphics.Bitmap;

import cn.bingoogolapple.qrcode.core.DisplayUtils;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import io.reactivex.Single;

/**
 * Created by albert on 9/21/16.
 */
public class RxQRCode {
    public static Single<Bitmap> createQRCode(String content, Context context, int size) {
        return Single.create( emitter -> {
                QRCodeEncoder.encodeQRCode(content,
                        DisplayUtils.dp2px(context, size),
                        new QRCodeEncoder.Delegate() {
                            @Override
                            public void onEncodeQRCodeSuccess(Bitmap bitmap) {
                                emitter.onSuccess(bitmap);
                            }

                            @Override
                            public void onEncodeQRCodeFailure() {
                                emitter.onError(new Exception("Failed to encode QRCode."));
                            }
                        });
        });
    }
}
