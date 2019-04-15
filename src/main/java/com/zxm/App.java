package com.zxm;

import com.zxm.registry.Registry;
import com.zxm.registry.ZookeeperRegistry;
import com.zxm.core.IdWorker;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {
        Registry registryAdapter = new ZookeeperRegistry("10.10.4.17:2181", 3000);
        Thread.sleep(10 * 1000);
        IdWorker idWorker = new IdWorker(registryAdapter);

        for (int i = 0; i < 10; i++) {
            System.out.println(idWorker.nextId());
        }
    }
}
