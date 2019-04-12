package com.zxm;

import com.zxm.adapter.RegistryAdapter;
import com.zxm.adapter.ZkRegistryAdapter;
import com.zxm.core.IdWorker;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {
        RegistryAdapter registryAdapter = new ZkRegistryAdapter("10.10.4.17:2181", 3000);
        Thread.sleep(10 * 1000);
        IdWorker idWorker = new IdWorker(registryAdapter);

        for (int i = 0; i < 10; i++) {
            System.out.println(idWorker.nextId());
        }

    }
}
