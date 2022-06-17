package org.needcoke.rpc.config;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.experimental.UtilityClass;
import org.needcoke.rpc.utils.SpringContextUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicLong;

@UtilityClass
public class RequestIdContextHolder {

    private final ThreadLocal<String> THREAD_LOCAL_REQUEST_ID = new TransmittableThreadLocal<>();

    public void setRequestId(String requestId){
        THREAD_LOCAL_REQUEST_ID.set(requestId);
    }

    public String getRequestId(){
        return THREAD_LOCAL_REQUEST_ID.get();
    }

    public void clear() {
        THREAD_LOCAL_REQUEST_ID.remove();
    }

    private final AtomicLong requestIdMaker = new AtomicLong(1);

    public long getAndAdd(){
        return requestIdMaker.getAndAdd(1);
    }



    public static String newRequestId(){
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String ip = localHost.getHostAddress();
            int port = SpringContextUtils.getBean(ServerConfig.class).getMvcPort();
            return ip+":"+port+"#"+getAndAdd();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return "error-request-id";
    }

}
