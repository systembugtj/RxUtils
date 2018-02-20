package me.systembug.reactivex;

import android.content.Context;
import android.graphics.Bitmap;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by albert on 9/21/16.
 */
public class RxQRCode {
    public static Single<Bitmap> createQRCode(String content, Context context, int size) {
        return Single.create(emitter -> {
                    Bitmap image = QRCodeEncoder.syncEncodeQRCode(content, dp2px(context, size));
                    emitter.onSuccess(image);
                });
    }

    private static int dp2px(Context context, int dip){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dip * scale + 0.5f);
    }
}
