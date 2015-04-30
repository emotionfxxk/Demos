package ordin.com.protocol.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ordin.com.protocol.command.Command;
import ordin.com.protocol.command.JpgResponse;
import ordin.com.protocol.connection.CommandListener;
import ordin.com.protocol.connection.IState;

/**
 * Repack image packet and decode jpg image as bitmap
 * TODO: may be we need pack the jpg data in individual thread, rather then in JPG connection thread
 * Created by sean on 4/20/15.
 */
public class ImageProcessor extends IState implements CommandListener {
    private final static String TAG = "ImageProcessor";
    public final static int MSG_ON_REC_IMAGE = 0;

    public ImageProcessor() {}

    private SparseArray<ArrayList<JpgResponse>> responseArrays = new SparseArray<ArrayList<JpgResponse>>();

    private ThreadPoolExecutor executor;
    private Handler handler;

    private static class DecoderTask implements Runnable {
        Handler handler;
        ImagePacket packet;
        public DecoderTask(Handler handler, ImagePacket packet) {
            this.handler = handler;
            this.packet = packet;
        }

        @Override
        public void run() {
            // decode bitmap
            Log.i(TAG, "before decode bitmap");
            long start = System.currentTimeMillis();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bitmap = BitmapFactory.decodeByteArray(packet.imageData, 0, packet.imageData.length, options);
            Log.i(TAG, "after decode bitmap, cost:" + (System.currentTimeMillis() - start) + "ms");
            handler.sendMessage(handler.obtainMessage(MSG_ON_REC_IMAGE, packet.imageOrPlanIndex, 0, bitmap));
        }
    }

    public void setUpdaterHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onStart() {
        if(this.handler == null) throw new IllegalStateException("Handler must be set before call start()");
        executor = new ThreadPoolExecutor(6, 6, 0,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
                new ThreadPoolExecutor.DiscardPolicy());
    }

    @Override
    public void onStop() {
        if(executor != null) {
            executor.shutdown();
            executor = null;
        }
    }

    @Override
    public void onSentCommand(Command cmd) {
    }

    @Override
    public void onReceivedCommand(Command cmd) {
        if(cmd instanceof JpgResponse) {
            JpgResponse response = (JpgResponse) cmd;
            int key = getKeyFromJpgResponse(response);
            Log.i(TAG, "onReceivedCommand key:" + key);
            ArrayList<JpgResponse> responses = responseArrays.get(key);
            if(responses == null) {
                responses = new ArrayList<JpgResponse>();
            }
            responses.add(response);
            responseArrays.put(key, responses);

            if(response.totalCount == responses.size()) {
                packImage(responses);
                responseArrays.removeAt(key);
            }
        }
    }

    private void packImage(ArrayList<JpgResponse> responses) {
        if(responses == null || responses.size() <= 0)
            throw new IllegalArgumentException("responses be null or responses size be 0 :" + responses);

        // sort the all jpg response first
        Collections.sort(responses, new Comparator<JpgResponse>() {
            @Override
            public int compare(JpgResponse lhs, JpgResponse rhs) {
                return ImageProcessor.compare(lhs.index, rhs.index);
            }
        });
        // then we repack the jpg data
        int jpgDataLength = 0;
        for(JpgResponse response: responses) {
            jpgDataLength += response.imageData.length;
        }

        ImagePacket packet = new ImagePacket();
        packet.imageType = responses.get(0).imageType;
        packet.imageOrPlanIndex = responses.get(0).planIndex;
        packet.imageData = new byte[jpgDataLength];
        Log.i(TAG, "jpgDataLength:" + jpgDataLength);
        int pos = 0;
        for(JpgResponse response: responses) {
            Log.i(TAG, "totalCount:" + response.totalCount + "response.index:" + response.index);
            System.arraycopy(response.imageData, 0, packet.imageData, pos, response.imageData.length);
            pos += response.imageData.length;
        }
        decodeJpgImageAsync(packet);
    }

    private void decodeJpgImageAsync(ImagePacket packet) {
        executor.execute(new DecoderTask(handler, packet));
    }

    private int getKeyFromJpgResponse(JpgResponse response) {
        if(response == null) throw new IllegalArgumentException("Response should not be null");
        // make key image type(0x01 or 0x00) * 56636 + plan index or signal index
        return (response.imageType & 0x000000FF) << 16 + response.planIndex;
    }

    private static int compare(int lhs, int rhs) {
        return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
    }
}
