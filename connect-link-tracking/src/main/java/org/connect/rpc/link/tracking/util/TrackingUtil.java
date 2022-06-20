package org.connect.rpc.link.tracking.util;

import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import lombok.experimental.UtilityClass;
import org.connect.rpc.link.tracking.common.CommonConstant;
import org.connect.rpc.link.tracking.config.LinkTrackingContextHolder;
import org.connect.rpc.link.tracking.net.LinkTracking;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@UtilityClass
public class TrackingUtil {

    private Gson gson = new Gson();
    public void preHttp(HttpServletRequest request, HttpServletResponse response, Object handler, int port) {
        String cokeRequestIdJson = request.getHeader(CommonConstant.COKE_REQUEST_ID_HEADER_ID_NAME);
        if (StrUtil.isEmpty(cokeRequestIdJson)) {
            LinkTracking linkTracking = new LinkTracking(port);
            linkTracking.setIndex(1);
            LinkTrackingContextHolder.setLinkTracking(linkTracking);
        } else {
            LinkTracking linkTracking = gson.fromJson(cokeRequestIdJson, LinkTracking.class);
            linkTracking.setIndex(linkTracking.getIndex() + 1);
            linkTracking.changeIp();
            linkTracking.setPort(port);
            LinkTrackingContextHolder.setLinkTracking(linkTracking);
        }
    }

    public String headerKey() {
        return CommonConstant.COKE_REQUEST_ID_HEADER_ID_NAME;
    }

    public String headerValue() {
        LinkTracking linkTracking = LinkTrackingContextHolder.getLinkTracking();
        linkTracking.setIndex(linkTracking.getIndex());
        return gson.toJson(linkTracking);
    }

    public String getRequestId() {
        return LinkTrackingContextHolder.getLinkTracking().getRequestId().toString();
    }

    public String linkTrackingJsonStr() {
        return gson.toJson(LinkTrackingContextHolder.getLinkTracking());
    }
}
