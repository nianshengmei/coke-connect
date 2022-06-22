package org.needcoke.rpc.smartsocket.processor;

import org.connect.rpc.link.tracking.config.LinkTrackingContextHolder;
import org.needcoke.rpc.codec.CokeRequest;
import org.needcoke.rpc.invoker.InvokeResult;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.transport.AioSession;
import org.smartboot.socket.transport.WriteBuffer;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class SmartSocketMessageProcessor<T> implements MessageProcessor<T> {

    public void responseHttp(HttpServletResponse response , InvokeResult result){
        try {
            response.setContentType("application/json; charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.print(result.toJson());
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
            // TODO 未来需要处理返回失败的场景
        }
        LinkTrackingContextHolder.clear();
    }

    public void response(AioSession session, CokeRequest response){
        WriteBuffer outputStream = session.writeBuffer();
        try {
            byte[] bytes = response.toBytes();
            outputStream.writeInt(bytes.length);
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LinkTrackingContextHolder.clear();
    }

}
