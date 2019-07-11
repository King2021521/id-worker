# id-worker
分布式全局唯一ID生成器（支持多种注册中心）
#### 简介
全局唯一ID生成器，支持多节点，无冲撞，高性能，支持多种注册中心,使用及配置十分简单。
#### 特性
* 生成方式：基于内存运算生成
* 分布式：支持（多个节点启动时在zk注册机器号）
* 注册中心：zookeeper、mysql
* 适用场景：适用于分布式订单号、支付单号及其他流水号生成场景
#### 注意事项
* 注册中心的高可用由业务自己保证
  
#### 配置方式（基于zk客户端）：
```
 @Configuration
 public class IdConfig {
     @Bean
     public Registry registryAdapter() throws Exception{
         return new ZookeeperRegistry("10.10.10.1:2181,10.10.10.2:2181,10.10.10.3:2181", 3000);
     }
 
     @Bean
     public IdWorker idWorker() throws Exception{
         return new IdWorker(registryAdapter());
     }
 }
```   
#### 基于curator客户端配置：  
```
 @Configuration
 public class IdConfig {
     @Bean
     public Registry registryAdapter() throws Exception{
         //支持自定义命名空间，用于业务隔离
         return new ZookeeperCuratorRegistry("10.10.10.1:2181,10.10.10.2:2181,10.10.10.3:2181", "sequence");
     }
 
     @Bean
     public IdWorker idWorker() throws Exception{
         return new IdWorker(registryAdapter());
     }
 }
```   
#### 使用举例：
```
public class IdWorkerTest{
    @Autowire
    private IdWorker idWorker;
    
    @Test
    public void test(){
        for (int i = 0; i < 10; i++) {
            System.out.println(idWorker.nextId());
        }
    }
}
```
#### 执行结果：
```
169097267071279104
169097267071281152
169097267071280128
169097267071282176
169097267071279616
169097267071281664
169097267071280640
169097267071282688
169097267071279360
169097267071281408
  
Process finished with exit code 0
```
