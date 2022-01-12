package org.needcoke.connect;

import io.netty.channel.Channel;

public interface ChannelEventListener {

    void onChannelConnect(final String remoteAddress, final Channel channel);

    void onChannelClose(final String remoteAddress, final Channel channel);

    void onChannelException(final String remoteAddress, final Channel channel);

    void onChannelIdle(final String remoteAddress, final Channel channel);
}
