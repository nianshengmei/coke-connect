package org.needcoke.connect.protocol;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
@ToString
public class CokeRequest implements Serializable {

    /* 请求主题 */
    private String topic;

    private String requestType;

    private Object data;

    private int ack;

    private Date requestTime;

}
