package org.needcoke.connect.config;

import lombok.Data;

@Data
public class ReceiverConfig {

    private int listenPort = 8888;
    private int serverSelectorThreads = 3;
    private int serverWorkerThreads = 8;

    private int serverSocketSndBufSize = 65535;
    private int serverSocketRcvBufSize = 65535;


    /**
     * make make install
     *
     *
     * ../glibc-2.10.1/configure \ --prefix=/usr \ --with-headers=/usr/include \
     * --host=x86_64-linux-gnu \ --build=x86_64-pc-linux-gnu \ --without-gd
     */
    private boolean useEpollNativeSelector = false;

}
