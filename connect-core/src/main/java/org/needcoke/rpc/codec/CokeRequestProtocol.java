package org.needcoke.rpc.codec;

import com.alibaba.fastjson.JSONObject;
import org.smartboot.socket.Protocol;
import org.smartboot.socket.transport.AioSession;

import java.nio.ByteBuffer;

public class CokeRequestProtocol implements Protocol<CokeRequest> {

    @Override
    public CokeRequest decode(ByteBuffer readBuffer, AioSession session) {
        int remaining = readBuffer.remaining();
        if (remaining < Integer.BYTES) {
            return null;
        }
        readBuffer.mark();
        int length = readBuffer.getInt();
        if (length > readBuffer.remaining()) {
            readBuffer.reset();
            return null;
        }
        byte[] b = new byte[length];
        readBuffer.get(b);
        readBuffer.mark();
        return JSONObject.parseObject(new String(b),CokeRequest.class);
    }
}