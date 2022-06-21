package org.connect.rpc.link.tracking.config;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlRunnable;
import lombok.experimental.UtilityClass;
import org.connect.rpc.link.tracking.net.LinkTracking;

@UtilityClass
public class LinkTrackingContextHolder {

    private final TransmittableThreadLocal<LinkTracking> THREAD_LOCAL_LINK_TRACKING = new TransmittableThreadLocal<>();

    public void setLinkTracking(LinkTracking tracking) {
        THREAD_LOCAL_LINK_TRACKING.set(tracking);
    }

    public LinkTracking getLinkTracking() {
        return THREAD_LOCAL_LINK_TRACKING.get();
    }

    public void clear() {
        THREAD_LOCAL_LINK_TRACKING.remove();
    }


    public boolean isEmpty() {
        return THREAD_LOCAL_LINK_TRACKING.get() == null;
    }

    public boolean isNotEmpty() {
        return THREAD_LOCAL_LINK_TRACKING.get() != null;
    }
}
