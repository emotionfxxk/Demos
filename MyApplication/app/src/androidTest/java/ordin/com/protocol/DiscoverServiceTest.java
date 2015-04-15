package ordin.com.protocol;

import android.test.AndroidTestCase;
import android.util.Log;

import java.nio.ByteBuffer;

import ordin.com.protocol.command.CommandDefs;
import ordin.com.protocol.command.Request;
import ordin.com.protocol.command.RequestFactory;
import ordin.com.protocol.command.Response;
import ordin.com.protocol.command.ResponseParser;
import ordin.com.protocol.command.ServiceDiscoverResponse;

/**
 * Created by sean on 4/8/15.
 */
public class DiscoverServiceTest extends AndroidTestCase {
    private final static String TAG = "DiscoverServiceTest";
    public void testDiscoverServiceRequest() {
        Request request = RequestFactory.createServiceDiscoverRequest("192.162.0.1", 5333);
        String toString = request.toString();
        assertTrue(toString.contains("192.162.0.1"));
        assertTrue(toString.contains("5333"));
        byte[] packet = request.getDataPacket();
        assertEquals(16, ((packet[4] & 0x000000FF) << 8) + (packet[5] & 0x000000FF));
        assertEquals(5333, ((packet[13] & 0x000000FF)<< 8) + (packet[14] & 0x000000FF));
    }
    public void testDiscoverServiceResponse() {
        byte[] mockResponsePacket = new byte[] {
                'O', 'd', 'i', 'n',                 // header
                0x00, 0x10,                         // length: 16
                CommandDefs.CMD_DISCOVER_SERVICE,   // command
                (byte)0xC0, (byte)0xA8, 0x00, 0x01, // ip address: 192.168.0.1
                0x15, (byte)0xB4,                   // command port: 5556
                0x15, (byte)0xB5,                   // command port: 5557
                0x00                                // checksum
        };
        ServiceDiscoverResponse response = (ServiceDiscoverResponse)ResponseParser.parserResponse(
                ByteBuffer.wrap(mockResponsePacket));
        assertEquals("192.168.0.1", response.ipAddress);
        assertEquals(5556, response.commandPort);
        assertEquals(5557, response.jpgPort);
    }
}
