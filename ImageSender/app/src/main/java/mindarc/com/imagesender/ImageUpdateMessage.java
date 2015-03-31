package mindarc.com.imagesender;

/**
 * Created by sean on 3/30/15.
 */
public class ImageUpdateMessage {
    String filePath;
    int filePos;
    public ImageUpdateMessage(String filePath, int filePos) {
        this.filePath = filePath;
        this.filePos = filePos;
    }
}
