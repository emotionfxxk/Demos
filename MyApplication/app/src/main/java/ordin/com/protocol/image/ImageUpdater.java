package ordin.com.protocol.image;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;
import android.widget.ImageView;

import java.lang.ref.SoftReference;

/**
 * Created by sean on 4/22/15.
 */
public class ImageUpdater {
    private final static String TAG = "ImageUpdater";

    public final Handler handler;
    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == ImageProcessor.MSG_ON_REC_IMAGE) {
                // on received image from
                updateImage(msg.arg1, (Bitmap)msg.obj);
            }
            return true;
        }
    };
    private SparseArray<SoftReference<ImageView>> imageViewMap;

    public ImageUpdater() {
        if(Thread.currentThread() != Looper.getMainLooper().getThread()) {
            throw new IllegalStateException("ImageUpdater must be instantiated from main thread!");
        }
        handler = new Handler(callback);
        imageViewMap = new SparseArray<SoftReference<ImageView>>();
    }

    public void subscribe(int planOrSignalIndex, ImageView imageView) {
        synchronized (imageViewMap) {
            imageViewMap.put(planOrSignalIndex, new SoftReference<ImageView>(imageView));
        }
    }

    public void unsubscribe(int planOrSignalIndex) {
        synchronized (imageViewMap) {
            imageViewMap.removeAt(planOrSignalIndex);
        }
    }

    private void updateImage(int planOrSignalIndex, Bitmap bitmap) {
        SoftReference<ImageView> srImageView = null;
        synchronized (imageViewMap) {
            srImageView = imageViewMap.get(planOrSignalIndex);
            if(srImageView == null || srImageView.get() == null) {
                imageViewMap.removeAt(planOrSignalIndex);
                bitmap.recycle();
            } else {
                srImageView.get().setImageBitmap(bitmap);
            }
        }

    }
}
