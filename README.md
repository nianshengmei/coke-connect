# coke-connect

#### 介绍
基于spring cloud生态的跨服务rpc调用组件。支持eureka和nacos两种注册中心，支持负载均衡配置(默认轮询)。

灵活的函数名称调用方式。

#### 特点
支持选配基于okHttps,smartSocket,netty作为跨服务调用的消息组件

支持多种负载均衡策略

调用远程方法就像调用本地方法一样

#### 展望

@RpcClient注解的支持

接口快速失败 ，失败重试 

接口调用失败日志采集 ，链路追踪 ，实时分析修改注册中心中该节点的健康状态

通过注册中心直接获取Rpc端口号（现在是从注册中心获取）{
    step 1: 将rpc注解以metadata的方式存入注册中心，后续调用不需要调接口
    step 2: 适配spring-boot-protocol，采用代理的方式统一端口
    step 3: 覆盖主流tomcat 、undertow 、 jetty，增设拦截器，共享一个服务器
}

