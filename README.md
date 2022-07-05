# coke-connect

coke-connect是一款基于spring-cloud的方法级的跨服务调用工具。

coke-connect即提供了类open feign的client的调用方式，如下。

```java
import org.needcoke.rpc.annotation.RpcClient;

@RpcClient(name = "cClient" , serviceId = "service-c" ,beanName = "c_bean")
public interface ConClient {

    Object cTest2(String word);
}

@RestController
@RequestMapping("api/b")
public class TestController {

    @Resource
    private final ConClient client;

    @GetMapping("test")
    public String test() {

        return client.cTest2("hello");
    }
}

```
声明如上的一个connect client 即可实现调用服务类别为 service-c ,中的c_bean这个组件的  cTest2 方法

更有灵活的函数调用方式，如下：
```java
class Main {
    public static void main(String[] args) {
        InvokeResult result = connectorFactory.connector("bussiness-c").execute("cCon", "cTest2", args);
    }
}



public class InvokeResult implements Serializable {

    /**
     * 状态
     */
    private int status;

    /**
     * 响应时间
     */
    private long time;

    /**
     *  结果
     */
    private Object body;
}

```

coke-connect除了提供了基本的rpc调用方式之外，还提供了负载均衡，链路追踪，熔断等策略。默认的调用方式基于http,更可选配netty 和 smart-socket

```java

class config{
    
    @Bean
    public NettyInvoker nettyInvoker(){
        return new NettyInvoker();
    }

    @Bean
    public NettyServer nettyServer(){
        return new NettyServer();
    }

//    @Bean
//    public SmartSocketInvoker nettyInvoker(){
//        return new SmartSocketInvoker();
//    }
//
//    @Bean
//    public SmartSocketServer nettyServer(){
//        return new SmartSocketServer();
//    }
    
    
    //指定负载均衡策略    下面的例子是  响应时间决定权重 的负载均衡策略
    @Bean
    public WeightedResponseTimeBalance weightedResponseTimeBalance(){
        return new WeightedResponseTimeBalance();
    }
    
}

```