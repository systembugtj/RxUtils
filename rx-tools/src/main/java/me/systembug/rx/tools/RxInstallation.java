package me.systembug.rx.tools;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

import io.reactivex.Single;

/**
 * Created by albert on 10/20/16.
 */
public class RxInstallation {

    private static RxInstallation mInstance;
    private static String sID = null;
    private static final String INSTALLATION = "INSTALLATION";

    public static Single<String> installation(Context context) {
        if (mInstance == null) {
            mInstance = new RxInstallation();
        }
        return mInstance.query(context);
    }

    private RxInstallation() {
    }

    private Single<String> query(Context context) {
        return getId(context);
    }

    private Single<String> getId(Context context) {
        return Single.create((emitter) -> {
            emitter.onSuccess(id(context));
        });
    }

    public String id(Context context) {
        if (sID == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists()) writeInstallationFile(installation);
                sID = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sID;
    }

    private String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }
}