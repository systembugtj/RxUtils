package me.systembug.reactivex;

import android.content.Context;
import android.graphics.Bitmap;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import static me.systembug.utils.Screens.dip2px;

/**
 * Created by albert on 9/21/16.
 */
public class RxQRCode {
    public static Single<Bitmap> createQRCode(String content, Context context, int size) {
        return Single.create(emitter -> {
                    Bitmap image = QRCodeEncoder.syncEncodeQRCode(content, dip2px(context, size));
                    emitter.onSuccess(image);
                });
    }

}
