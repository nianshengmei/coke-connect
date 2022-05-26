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
        String json = new String(b);
        CokeRequest request = null;
        try {
            request = JSONObject.parseObject(json, CokeRequest.class);
        }catch (Exception e){
            System.out.println("error { "+json+" }");
            throw new RuntimeException(e);
        }
        return request;
    }
}