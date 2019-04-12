package com.zxm.adapter;

import org.apache.zookeeper.KeeperException;

/**
 * @Author zxm
 * @Description 注册适配器
 * @Date Create in 上午 9:45 2019/4/12 0012
 */
public interface RegistryAdapter {
    long getWorkerId() throws KeeperException, InterruptedException;
}
