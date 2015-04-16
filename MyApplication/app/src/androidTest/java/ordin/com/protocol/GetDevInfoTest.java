package ordin.com.protocol;

import android.test.AndroidTestCase;
import android.util.Log;

import java.nio.ByteBuffer;

import ordin.com.protocol.command.CommandDefs;
import ordin.com.protocol.command.GetWindowStructureResponse;
import ordin.com.protocol.command.Request;
import ordin.com.protocol.command.RequestFactory;
import ordin.com.protocol.command.ResponseParser;
import ordin.com.protocol.command.ServiceDiscoverResponse;

/**
 * Created by sean on 4/8/15.
 */
public class GetDevInfoTest extends AndroidTestCase {

    public void testGetWindowStructureResponse() {
        String chinese = "你好";
        byte[] desBytes = chinese.getBytes();
        byte[] mockResponsePacket = new byte[] {
                'O', 'd', 'i', 'n',                             // header
                0x10, 0x00,                                     // length: 16
                CommandDefs.CMD_GET_PLAN_WINDOW_STRUCTURE,      // command
                0x02,                                           // screen group count : 1
                0x02, 0x05, 0x01,
                desBytes[0], desBytes[1],
                desBytes[2], desBytes[3],
                desBytes[4], desBytes[5], '\0',                 // group 1
                0x04, 0x02, 0x01, 'w', 'o', '\0',               // group 2
                0x00                                            // checksum
        };
        GetWindowStructureResponse response = (GetWindowStructureResponse)ResponseParser.parserResponse(
                ByteBuffer.wrap(mockResponsePacket));
        assertEquals(2, response.screenGroups.length);
        assertEquals(2, response.screenGroups[0].horizontalCount);
        assertEquals(5, response.screenGroups[0].verticalCount);
        assertEquals(1, response.screenGroups[0].startNumber);
        assertTrue("你好".equals(response.screenGroups[0].description));
        assertTrue("wo".equals(response.screenGroups[1].description));
    }
}
