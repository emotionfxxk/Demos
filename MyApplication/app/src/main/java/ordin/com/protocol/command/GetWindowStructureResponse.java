package ordin.com.protocol.command;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import ordin.com.protocol.deviceinfo.ScreenGroup;

/**
 * Created by sean on 4/15/15.
 */
public class GetWindowStructureResponse extends Response {
    public ScreenGroup[] screenGroups;

    private GetWindowStructureResponse() {}

    public static Creator sCreator = new Creator() {
        @Override
        public Response createInstance() {
            return new GetWindowStructureResponse();
        }
    };

    @Override
    public void parsePayload(ByteBuffer byteBuffer) {
        int screenGroupCount = byteBuffer.get();
        screenGroups = new ScreenGroup[screenGroupCount];

        for(int pos = 0; pos < screenGroupCount; ++pos) {
            ScreenGroup sg = new ScreenGroup();
            sg.horizontalCount = byteBuffer.get();
            sg.verticalCount = byteBuffer.get();
            sg.startNumber = byteBuffer.get();
            // get description
            // TODO: make sure the description is no longer than 256 bytes
            byte[] descriptionBuffer = new byte[256];
            byte singleChar;
            int charIndex = 0;
            do {
                singleChar = byteBuffer.get();
                if(singleChar != '\0')
                    descriptionBuffer[charIndex++] = singleChar;
                else
                    break;
            } while(true);
            sg.description = new String(descriptionBuffer, 0, charIndex, Charset.forName("UTF-8"));
            screenGroups[pos] = sg;
        }
    }
}
